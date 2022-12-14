package xin.manong.stream.boost.resource.oss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.aliyun.oss.OSSClient;
import xin.manong.weapon.aliyun.oss.OSSClientConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.base.secret.DynamicSecret;

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
        if (clientConfig == null) {
            logger.error("parse OSS client config failed");
            return null;
        }
        clientConfig.aliyunSecret = new AliyunSecret();
        clientConfig.aliyunSecret.accessKey = DynamicSecret.accessKey;
        clientConfig.aliyunSecret.secretKey = DynamicSecret.secretKey;
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
