package com.manong.stream.boost.receiver.ons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import com.manong.stream.sdk.receiver.Receiver;
import com.manong.weapon.base.common.Context;
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
public class ONSReceiver extends Receiver {

    private final static Logger logger = LoggerFactory.getLogger(ONSReceiver.class);

    private Consumer consumer;

    public ONSReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        logger.info("ONS receiver is starting ...");
        ONSConsumerConfig consumerConfig = JSON.toJavaObject(
                new JSONObject(configMap), ONSConsumerConfig.class);
        if (!consumerConfig.check()) return false;
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESRV_ADDR, consumerConfig.serverURL);
        properties.put(PropertyKeyConst.GROUP_ID, consumerConfig.consumeId);
        properties.put(PropertyKeyConst.AccessKey, consumerConfig.keySecret.accessKey);
        properties.put(PropertyKeyConst.SecretKey, consumerConfig.keySecret.secretKey);
        properties.put(PropertyKeyConst.ConsumeThreadNums, consumerConfig.consumeThreadNum);
        properties.put(PropertyKeyConst.MaxCachedMessageAmount, consumerConfig.maxCachedMessageNum);
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        try {
            consumer = ONSFactory.createConsumer(properties);
            consumer.subscribe(consumerConfig.topic, consumerConfig.tags, (message, consumeContext) -> {
                try {
                    receiveProcessor.process(new Context(), message);
                    return Action.CommitMessage;
                } catch (Throwable e) {
                    logger.error("process message[{}] failed", message.getMsgID());
                    logger.error(e.getMessage(), e);
                    return Action.ReconsumeLater;
                }
            });
            consumer.start();
            logger.info("subscribe topic[{}] and tags[{}] success", consumerConfig.topic, consumerConfig.tags);
            logger.info("ONS receiver has been started");
        } catch (Exception e) {
            logger.error("start ONS receiver failed");
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void stop() {
        logger.info("ONS receiver is stopping ...");
        if (consumer != null) consumer.shutdown();
        logger.info("ONS receiver has been stopped");
    }
}
