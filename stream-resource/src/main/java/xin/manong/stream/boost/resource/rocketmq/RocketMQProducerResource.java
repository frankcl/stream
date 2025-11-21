package xin.manong.stream.boost.resource.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.rocketmq.RocketMQProducer;
import xin.manong.weapon.base.rocketmq.RocketMQProducerConfig;

import java.util.Map;

/**
 * RocketMQ消息生产资源
 *
 * @author frankcl
 * @date 2025-11-01 14:07:36
 */
public class RocketMQProducerResource extends Resource<RocketMQProducer> {

    private final static Logger logger = LoggerFactory.getLogger(RocketMQProducerResource.class);

    public RocketMQProducerResource(String name) {
        super(name);
    }

    @Override
    public RocketMQProducer create(Map<String, Object> configMap) {
        RocketMQProducerConfig producerConfig = JSON.toJavaObject(
                new JSONObject(configMap), RocketMQProducerConfig.class);
        if (producerConfig == null) {
            logger.error("Parse RocketMQ producer config failed");
            return null;
        }
        RocketMQProducer producer = new RocketMQProducer(producerConfig);
        if (!producer.init()) {
            logger.error("Init RocketMQ producer failed");
            return null;
        }
        logger.info("Create RocketMQ producer success");
        return producer;
    }

    @Override
    public void destroy() {
        if (object != null) object.destroy();
        logger.info("Destroy RocketMQ producer success");
        object = null;
    }
}
