package xin.manong.stream.boost.resource.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.redis.*;

import java.util.Map;

/**
 * redis客户端资源
 *
 * @author frankcl
 * @date 2022-12-21 11:01:52
 */
public class RedisClientResource extends Resource<RedisClient> {

    private final static Logger logger = LoggerFactory.getLogger(RedisClientResource.class);

    private final static String KEY_MODE = "mode";

    public RedisClientResource(String name) {
        super(name);
    }

    @Override
    public RedisClient create(Map<String, Object> configMap) {
        RedisMode redisMode = RedisMode.SINGLE;
        if (configMap.containsKey(KEY_MODE)) {
            String value = (String) configMap.get(KEY_MODE);
            try {
                redisMode = RedisMode.valueOf(value.trim());
            } catch (Exception e) {
                logger.error("Invalid redis mode[{}]", value.trim());
                return null;
            }
        }
        RedisClient redisClient = null;
        switch (redisMode) {
            case SINGLE:
                RedisSingleConfig redisSingleConfig = JSON.toJavaObject(
                        new JSONObject(configMap), RedisSingleConfig.class);
                if (redisSingleConfig == null || !redisSingleConfig.check()) {
                    logger.error("Redis single config is invalid");
                    return null;
                }
                redisClient = RedisClient.buildRedisClient(redisSingleConfig);
                break;
            case CLUSTER:
                RedisClusterConfig redisClusterConfig = JSON.toJavaObject(
                        new JSONObject(configMap), RedisClusterConfig.class);
                if (redisClusterConfig == null || !redisClusterConfig.check()) {
                    logger.error("Redis cluster config is invalid");
                    return null;
                }
                redisClient = RedisClient.buildRedisClient(redisClusterConfig);
                break;
            case MASTER_SLAVE:
                RedisMasterSlaveConfig redisMasterSlaveConfig = JSON.toJavaObject(
                        new JSONObject(configMap), RedisMasterSlaveConfig.class);
                if (redisMasterSlaveConfig == null || !redisMasterSlaveConfig.check()) {
                    logger.error("Redis master/slave config is invalid");
                    return null;
                }
                redisClient = RedisClient.buildRedisClient(redisMasterSlaveConfig);
                break;
        }
        if (redisClient != null) logger.info("Create redis client success");
        else logger.error("Create redis client failed");
        return redisClient;
    }

    @Override
    public void destroy() {
        if (object != null) object.close();
        object = null;
        logger.info("Close redis client success");
    }
}
