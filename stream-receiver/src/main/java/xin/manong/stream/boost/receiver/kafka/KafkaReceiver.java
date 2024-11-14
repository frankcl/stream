package xin.manong.stream.boost.receiver.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.Receiver;
import xin.manong.weapon.base.kafka.KafkaConsumeConfig;
import xin.manong.weapon.base.kafka.KafkaConsumeGroup;

import java.util.Map;

/**
 * kafka数据接收器
 *
 * @author frankcl
 * @date 2023-01-06 11:08:01
 */
public class KafkaReceiver extends Receiver {

    private final static Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);

    private KafkaConsumeGroup consumeGroup;

    public KafkaReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        logger.info("kafka receiver is starting ...");
        KafkaConsumeConfig consumerConfig = JSON.toJavaObject(new JSONObject(configMap), KafkaConsumeConfig.class);
        if (consumerConfig == null) {
            logger.error("parse kafka consume config failed");
            return false;
        }
        if (!consumerConfig.check()) return false;
        if (receiveProcessor == null) {
            logger.error("receive processor is null");
            return false;
        }
        KafkaProcessor processor = new KafkaProcessor(receiveProcessor);
        consumeGroup = new KafkaConsumeGroup(consumerConfig, processor);
        if (!consumeGroup.start()) {
            logger.error("start kafka consume group failed");
            return false;
        }
        logger.info("kafka receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("kafka receiver is stopping ...");
        if (consumeGroup != null) consumeGroup.stop();
        logger.info("kafka receiver has been stopped");
    }
}
