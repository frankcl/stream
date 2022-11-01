package com.manong.stream.boost.receiver.ons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manong.stream.sdk.receiver.Receiver;
import com.manong.weapon.aliyun.common.Rebuildable;
import com.manong.weapon.aliyun.ons.ONSConsumer;
import com.manong.weapon.aliyun.ons.ONSConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * ONS消息接收器
 *
 * @author frankcl
 * @date 2022-08-03 19:09:57
 */
public class ONSReceiver extends Receiver implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(ONSReceiver.class);

    private ONSConsumerConfig consumerConfig;
    private ONSProcessor processor;
    private ONSConsumer consumer;

    public ONSReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        logger.info("ONS receiver is starting ...");
        consumerConfig = JSON.toJavaObject(new JSONObject(configMap), ONSConsumerConfig.class);
        if (!consumerConfig.check()) return false;
        if (receiveProcessor == null) {
            logger.error("receive processor is null");
            return false;
        }
        processor = new ONSProcessor(receiveProcessor);
        consumer = new ONSConsumer(consumerConfig, processor);
        if (!consumer.start()) return false;
        logger.info("ONS receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("ONS receiver is stopping ...");
        if (consumer != null) consumer.stop();
        logger.info("ONS receiver has been stopped");
    }
}
