package xin.manong.stream.boost.resource.dashscope;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.aliyun.dashscope.EmbeddingClient;
import xin.manong.weapon.aliyun.dashscope.EmbeddingClientConfig;

import java.util.Map;

/**
 * 阿里云Embedding客户端资源
 *
 * @author frankcl
 * @date 2026-02-03 16:05:10
 */
public class EmbeddingClientResource extends Resource<EmbeddingClient> {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingClientResource.class);

    public EmbeddingClientResource(String name) {
        super(name);
    }

    @Override
    public EmbeddingClient create(Map<String, Object> configMap) {
        EmbeddingClientConfig clientConfig = JSON.toJavaObject(
                new JSONObject(configMap), EmbeddingClientConfig.class);
        if (clientConfig == null) {
            logger.error("Parse embedding client config failed");
            return null;
        }
        EmbeddingClient client = new EmbeddingClient(clientConfig);
        if (!client.open()) {
            logger.error("Open embedding client failed");
            return null;
        }
        logger.info("Create embedding client success");
        return client;
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        logger.info("Close embedding client success");
        object = null;
    }
}
