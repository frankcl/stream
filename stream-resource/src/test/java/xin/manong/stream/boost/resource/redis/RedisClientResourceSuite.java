package xin.manong.stream.boost.resource.redis;

import org.junit.Assert;
import org.junit.Test;
import xin.manong.weapon.base.redis.RedisMode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-12-21 11:17:01
 */
public class RedisClientResourceSuite {

    private String nodeAddress = "r-bp172bf1e294b244.redis.rds.aliyuncs.com:6379";
    private String password = "";

    @Test
    public void testCreateSingleRedisClient() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("mode", RedisMode.SINGLE.name());
        configMap.put("connectionPoolSize", 200);
        configMap.put("password", password);
        configMap.put("address", nodeAddress);
        configMap.put("db", 1);
        RedisClientResource resource = new RedisClientResource("redis_client");
        resource.build(configMap);
        Assert.assertTrue(resource.get() != null);
        resource.destroy();
    }
}
