package xin.manong.stream.boost.resource.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.aliyun.log.LogClient;
import xin.manong.weapon.aliyun.log.LogClientConfig;

import java.util.Map;

/**
 * 阿里云SLS日志客户端资源
 *
 * @author frankcl
 * @date 2023-07-06 15:18:01
 */
public class LogClientResource extends Resource<LogClient> {

    private static final Logger logger = LoggerFactory.getLogger(LogClientResource.class);

    public LogClientResource(String name) {
        super(name);
    }

    @Override
    public LogClient create(Map<String, Object> configMap) {
        LogClientConfig clientConfig = JSON.toJavaObject(new JSONObject(configMap), LogClientConfig.class);
        if (clientConfig == null) {
            logger.error("Parse log client config failed");
            return null;
        }
        LogClient logClient = new LogClient(clientConfig);
        if (!logClient.init()) {
            logger.error("Init log client failed");
            return null;
        }
        logger.info("Create log client success");
        return logClient;
    }

    @Override
    public void destroy() {
        if (object != null) object.destroy();
        logger.error("Destroy log client success");
        object = null;
    }
}
