package com.manong.stream.boost.resource.oss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manong.stream.sdk.resource.Resource;
import com.manong.weapon.aliyun.oss.OSSClient;
import com.manong.weapon.aliyun.oss.OSSClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * OSS客户端资源
 *
 * @author frankcl
 * @create 2019-08-26 18:23:27
 */
public class OSSClientResource extends Resource<OSSClient> {

    private final static Logger logger = LoggerFactory.getLogger(OSSClientResource.class);

    public OSSClientResource(String name) {
        super(name);
    }

    @Override
    public OSSClient create(Map<String, Object> configMap) {
        OSSClientConfig clientConfig = JSON.toJavaObject(
                new JSONObject(configMap), OSSClientConfig.class);
        if (clientConfig == null || !clientConfig.check()) {
            logger.error("check OSS client config failed");
            return null;
        }
        logger.info("create OSS client success");
        return new OSSClient(clientConfig);
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        logger.info("close OSS client success");
        object = null;
    }
}
