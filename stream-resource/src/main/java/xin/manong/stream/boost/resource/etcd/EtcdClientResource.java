package xin.manong.stream.boost.resource.etcd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.etcd.EtcdConfig;

import java.util.Map;

/**
 * ES客户端资源定义
 *
 * @author frankcl
 * @date 2025-09-29 17:25:18
 */
public class EtcdClientResource extends Resource<EtcdClient> {

    private static final Logger logger = LoggerFactory.getLogger(EtcdClientResource.class);

    public EtcdClientResource(String name) {
        super(name);
    }

    @Override
    public EtcdClient create(Map<String, Object> configMap) {
        EtcdConfig config = JSON.toJavaObject(
                new JSONObject(configMap), EtcdConfig.class);
        if (config == null) {
            logger.error("Parse etcd client config failed");
            return null;
        }
        return new EtcdClient(config);
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        object = null;
    }
}
