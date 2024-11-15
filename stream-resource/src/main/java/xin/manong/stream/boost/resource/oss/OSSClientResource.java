package xin.manong.stream.boost.resource.oss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.aliyun.oss.OSSClient;
import xin.manong.weapon.aliyun.oss.OSSClientConfig;

import java.util.Map;

/**
 * OSS客户端资源
 *
 * @author frankcl
 * @date 2019-08-26 18:23:27
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
        OSSClient ossClient = new OSSClient(clientConfig);
        logger.info("create OSS client success");
        return ossClient;
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        logger.info("close OSS client success");
        object = null;
    }
}
