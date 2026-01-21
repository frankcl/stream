package xin.manong.stream.boost.resource.milvus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.milvus.MilvusClient;
import xin.manong.weapon.base.milvus.MilvusClientConfig;
import xin.manong.stream.sdk.resource.Resource;

import java.util.Map;

/**
 * Milvus客户端资源定义
 *
 * @author frankcl
 * @date 2025-09-29 17:25:18
 */
public class MilvusClientResource extends Resource<MilvusClient> {

    private static final Logger logger = LoggerFactory.getLogger(MilvusClientResource.class);

    public MilvusClientResource(String name) {
        super(name);
    }

    @Override
    public MilvusClient create(Map<String, Object> configMap) {
        MilvusClientConfig config = JSON.toJavaObject(
                new JSONObject(configMap), MilvusClientConfig.class);
        if (config == null) {
            logger.error("Parse milvus client config failed");
            return null;
        }
        MilvusClient client = new MilvusClient(config);
        if (!client.open()) return null;
        return client;
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        object = null;
    }
}
