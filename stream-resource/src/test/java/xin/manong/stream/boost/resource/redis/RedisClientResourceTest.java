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
public class RedisClientResourceTest {

    @Test
    public void testCreateSingleRedisClient() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("mode", RedisMode.SINGLE.name());
        configMap.put("connectionPoolSize", 200);
        String password = "";
        configMap.put("password", password);
        String nodeAddress = "127.0.0.1:6379";
        configMap.put("address", nodeAddress);
        configMap.put("db", 1);
        RedisClientResource resource = new RedisClientResource("redis_client");
        resource.build(configMap);
        Assert.assertNotNull(resource.get());
        resource.destroy();
    }
}
