package xin.manong.stream.boost.resource.datahub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.aliyun.datahub.DataHubClient;
import xin.manong.weapon.aliyun.datahub.DataHubClientConfig;

import java.util.Map;

/**
 * DataHub客户端资源
 *
 * @author frankcl
 * @date 2023-07-06 15:06:21
 */
public class DataHubClientResource extends Resource<DataHubClient> {

    private static final Logger logger = LoggerFactory.getLogger(DataHubClientResource.class);

    public DataHubClientResource(String name) {
        super(name);
    }

    @Override
    public DataHubClient create(Map<String, Object> configMap) {
        DataHubClientConfig clientConfig = JSON.toJavaObject(new JSONObject(configMap), DataHubClientConfig.class);
        if (clientConfig == null) {
            logger.error("parse data hub client config failed");
            return null;
        }
        DataHubClient dataHubClient = new DataHubClient(clientConfig);
        logger.info("create data hub client success");
        return dataHubClient;
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        logger.info("close data hub client success");
        object = null;
    }
}
