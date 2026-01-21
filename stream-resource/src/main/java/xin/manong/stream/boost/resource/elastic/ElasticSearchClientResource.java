package xin.manong.stream.boost.resource.elastic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.elasticsearch.ElasticSearchClient;
import xin.manong.weapon.base.elasticsearch.ElasticSearchClientConfig;

import java.util.Map;

/**
 * ES客户端资源定义
 *
 * @author frankcl
 * @date 2025-09-29 17:25:18
 */
public class ElasticSearchClientResource extends Resource<ElasticSearchClient> {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchClientResource.class);

    public ElasticSearchClientResource(String name) {
        super(name);
    }

    @Override
    public ElasticSearchClient create(Map<String, Object> configMap) {
        ElasticSearchClientConfig config = JSON.toJavaObject(
                new JSONObject(configMap), ElasticSearchClientConfig.class);
        if (config == null) {
            logger.error("Parse elastic search client config failed");
            return null;
        }
        ElasticSearchClient client = new ElasticSearchClient(config);
        if (!client.open()) return null;
        return client;
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        object = null;
    }
}
