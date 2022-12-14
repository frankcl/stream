package xin.manong.stream.boost.resource.ots;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.aliyun.ots.OTSClient;
import xin.manong.weapon.aliyun.ots.OTSClientConfig;
import xin.manong.weapon.aliyun.secret.AliyunSecret;
import xin.manong.weapon.base.secret.DynamicSecret;

import java.util.Map;

/**
 * OTS客户端资源
 *
 * @author frankcl
 * @create 2019-06-01 13:55
 */
public class OTSClientResource extends Resource<OTSClient> {

    private final static Logger logger = LoggerFactory.getLogger(OTSClientResource.class);

    public OTSClientResource(String name) {
        super(name);
    }

    @Override
    public OTSClient create(Map<String, Object> configMap) {
        OTSClientConfig clientConfig = JSON.toJavaObject(new JSONObject(configMap), OTSClientConfig.class);
        if (clientConfig == null) {
            logger.error("parse OTS client config failed");
            return null;
        }
        clientConfig.aliyunSecret = new AliyunSecret();
        clientConfig.aliyunSecret.accessKey = DynamicSecret.accessKey;
        clientConfig.aliyunSecret.secretKey = DynamicSecret.secretKey;
        logger.info("create OTS client success");
        return new OTSClient(clientConfig);
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        logger.info("close OTS client success");
        object = null;
    }
}
