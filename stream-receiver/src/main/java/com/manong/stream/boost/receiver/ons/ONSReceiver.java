package com.manong.stream.boost.receiver.ons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import com.manong.stream.sdk.receiver.Receiver;
import com.manong.weapon.aliyun.common.RebuildManager;
import com.manong.weapon.aliyun.common.Rebuildable;
import com.manong.weapon.aliyun.secret.DynamicSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * ONS消息接收器
 *
 * @author frankcl
 * @date 2022-08-03 19:09:57
 */
public class ONSReceiver extends Receiver implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(ONSReceiver.class);

    private ONSConsumerConfig consumerConfig;
    private Consumer consumer;

    public ONSReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    /**
     * 构建消息接收器实例
     *
     * @return 构建成功返回true，否则返回false
     */
    private boolean build() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESRV_ADDR, consumerConfig.serverURL);
        properties.put(PropertyKeyConst.GROUP_ID, consumerConfig.consumeId);
        properties.put(PropertyKeyConst.AccessKey, consumerConfig.aliyunSecret.accessKey);
        properties.put(PropertyKeyConst.SecretKey, consumerConfig.aliyunSecret.secretKey);
        properties.put(PropertyKeyConst.ConsumeThreadNums, consumerConfig.consumeThreadNum);
        properties.put(PropertyKeyConst.MaxCachedMessageAmount, consumerConfig.maxCachedMessageNum);
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        try {
            consumer = ONSFactory.createConsumer(properties);
            consumer.subscribe(consumerConfig.topic, consumerConfig.tags, (message, consumeContext) -> {
                try {
                    receiveProcessor.process(message);
                    return Action.CommitMessage;
                } catch (Throwable e) {
                    logger.error("process message[{}] failed", message.getMsgID());
                    logger.error(e.getMessage(), e);
                    return Action.ReconsumeLater;
                }
            });
            consumer.start();
            logger.info("subscribe topic[{}] and tags[{}] success", consumerConfig.topic, consumerConfig.tags);
            logger.info("build ONS receiver success");
        } catch (Exception e) {
            logger.error("build ONS receiver failed");
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void rebuild() {
        logger.info("ONS receiver is rebuilding ...");
        if (DynamicSecret.accessKey.equals(consumerConfig.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(consumerConfig.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore ONS receiver rebuilding");
            return;
        }
        consumerConfig.aliyunSecret.accessKey = DynamicSecret.accessKey;
        consumerConfig.aliyunSecret.secretKey = DynamicSecret.secretKey;
        Consumer prevConsumer = consumer;
        if (!build()) throw new RuntimeException("rebuild ONS receiver failed");
        if (prevConsumer != null) prevConsumer.shutdown();
        logger.info("ONS receiver rebuild success");
    }

    @Override
    public boolean start() {
        logger.info("ONS receiver is starting ...");
        consumerConfig = JSON.toJavaObject(new JSONObject(configMap), ONSConsumerConfig.class);
        if (!consumerConfig.check()) return false;
        if (!build()) return false;
        RebuildManager.register(this);
        logger.info("ONS receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("ONS receiver is stopping ...");
        RebuildManager.unregister(this);
        if (consumer != null) consumer.shutdown();
        logger.info("ONS receiver has been stopped");
    }
}
