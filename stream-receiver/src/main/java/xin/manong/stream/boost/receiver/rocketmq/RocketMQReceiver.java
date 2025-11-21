package xin.manong.stream.boost.receiver.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.Receiver;
import xin.manong.weapon.base.rocketmq.RocketMQConsumer;
import xin.manong.weapon.base.rocketmq.RocketMQConsumerConfig;

import java.util.Map;

/**
 * RocketMQ数据接收器
 *
 * @author frankcl
 * @date 2025-11-01 11:08:01
 */
public class RocketMQReceiver extends Receiver {

    private final static Logger logger = LoggerFactory.getLogger(RocketMQReceiver.class);

    private RocketMQConsumer consumer;

    public RocketMQReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        logger.info("RocketMQ receiver is starting ...");
        RocketMQConsumerConfig consumerConfig = JSON.toJavaObject(
                new JSONObject(configMap), RocketMQConsumerConfig.class);
        if (receiveProcessor == null) {
            logger.error("Receive processor is null");
            return false;
        }
        consumerConfig.messageListener = new RocketMQProcessor(receiveProcessor);
        if (!consumerConfig.check()) return false;
        consumer = new RocketMQConsumer(consumerConfig);
        if (!consumer.start()) {
            logger.error("Start RocketMQ consumer failed");
            return false;
        }
        logger.info("RocketMQ receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("RocketMQ receiver is stopping ...");
        if (consumer != null) consumer.stop();
        logger.info("RocketMQ receiver has been stopped");
    }
}
