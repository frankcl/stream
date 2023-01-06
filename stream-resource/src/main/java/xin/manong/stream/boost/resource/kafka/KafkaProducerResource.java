package xin.manong.stream.boost.resource.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.kafka.KafkaProduceConfig;
import xin.manong.weapon.base.kafka.KafkaProducer;

import java.util.Map;

/**
 * kafka消息生产资源
 *
 * @author frankcl
 * @date 2023-01-06 14:07:36
 */
public class KafkaProducerResource extends Resource<KafkaProducer> {

    private final static Logger logger = LoggerFactory.getLogger(KafkaProducerResource.class);

    public KafkaProducerResource(String name) {
        super(name);
    }

    @Override
    public KafkaProducer create(Map<String, Object> configMap) {
        KafkaProduceConfig producerConfig = JSON.toJavaObject(
                new JSONObject(configMap), KafkaProduceConfig.class);
        if (producerConfig == null) {
            logger.error("parse kafka producer config failed");
            return null;
        }
        KafkaProducer producer = new KafkaProducer(producerConfig);
        if (!producer.init()) {
            logger.error("init kafka producer failed");
            return null;
        }
        logger.info("create kafka producer success");
        return producer;
    }

    @Override
    public void destroy() {
        if (object != null) object.destroy();
        logger.info("destroy kafka producer success");
        object = null;
    }
}
