package com.manong.stream.boost.resource.ons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manong.stream.sdk.resource.Resource;
import com.manong.weapon.aliyun.ons.ONSProducer;
import com.manong.weapon.aliyun.ons.ONSProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * ONS消息生产资源
 *
 * @author frankcl
 * @create 2019-09-04 16:13:07
 */
public class ONSProducerResource extends Resource<ONSProducer> {

    private final static Logger logger = LoggerFactory.getLogger(ONSProducerResource.class);

    public ONSProducerResource(String name) {
        super(name);
    }

    @Override
    public ONSProducer create(Map<String, Object> configMap) {
        ONSProducerConfig producerConfig = JSON.toJavaObject(
                new JSONObject(configMap), ONSProducerConfig.class);
        if (producerConfig == null) {
            logger.error("parse ONS producer config failed");
            return null;
        }
        ONSProducer producer = new ONSProducer(producerConfig);
        if (!producer.init()) {
            logger.error("init ONS producer failed");
            return null;
        }
        logger.info("create ONS producer success");
        return producer;
    }

    @Override
    public void destroy() {
        if (object != null) object.destroy();
        logger.error("destroy ONS producer success");
        object = null;
    }
}
