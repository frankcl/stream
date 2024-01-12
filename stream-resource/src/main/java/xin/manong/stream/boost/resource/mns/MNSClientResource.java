package xin.manong.stream.boost.resource.mns;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.aliyun.mns.MNSClient;
import xin.manong.weapon.aliyun.mns.MNSClientConfig;

import java.util.Map;

/**
 * MNS客户端资源
 *
 * @author frankcl
 * @create 2024-01-12 13:55
 */
public class MNSClientResource extends Resource<MNSClient> {

    private final static Logger logger = LoggerFactory.getLogger(MNSClientResource.class);

    public MNSClientResource(String name) {
        super(name);
    }

    @Override
    public MNSClient create(Map<String, Object> configMap) {
        MNSClientConfig clientConfig = JSON.toJavaObject(new JSONObject(configMap), MNSClientConfig.class);
        if (clientConfig == null) {
            logger.error("parse MNS client config failed");
            return null;
        }
        MNSClient mnsClient = new MNSClient(clientConfig);
        logger.info("create MNS client success");
        return mnsClient;
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        logger.info("close MNS client success");
        object = null;
    }
}
