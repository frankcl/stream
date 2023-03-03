package xin.manong.stream.boost.resource.common;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.api.RRateLimiter;
import xin.manong.stream.framework.resource.ResourceConfig;
import xin.manong.stream.framework.resource.ResourceManager;
import xin.manong.weapon.base.redis.RedisMode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 13:46:26
 */
public class RRateLimiterResourceSuite {

    private String password = "";
    private String nodeAddress = "r-bp1fac3adaa6aa14.redis.rds.aliyuncs.com:6379";

    @Before
    public void setUp() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.name = "redis_client";
        resourceConfig.className = "xin.manong.stream.boost.resource.redis.RedisClientResource";
        resourceConfig.configMap.put("mode", RedisMode.SINGLE.name());
        resourceConfig.configMap.put("connectionPoolSize", 200);
        resourceConfig.configMap.put("password", password);
        resourceConfig.configMap.put("address", nodeAddress);
        resourceConfig.configMap.put("db", 1);
        ResourceManager.registerResource(resourceConfig);
    }

    @After
    public void tearDown() {
        ResourceManager.unregisterAllResources();
    }

    @Test
    public void testBuildSuccess() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.name = "rate_limiter";
        resourceConfig.className = "xin.manong.stream.boost.resource.common.RRateLimiterResource";
        resourceConfig.configMap.put("rateLimiterKey", "test_rate_limiter");
        resourceConfig.configMap.put("permitsPerSecond", 10);
        resourceConfig.configMap.put("redisClient", "redis_client");
        ResourceManager.registerResource(resourceConfig);

        RRateLimiter rateLimiter = ResourceManager.getResource("rate_limiter", RRateLimiter.class);
        Assert.assertTrue(rateLimiter != null);

        ResourceManager.unregisterResource("rate_limiter");
    }

    @Test
    public void testBuildFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("permitsPerSecond", 10d);
        configMap.put("rateLimiterKey", "test_rate_limiter");
        RRateLimiterResource resource = new RRateLimiterResource("rate_limiter");
        resource.build(configMap);
        Assert.assertTrue(resource.get() == null);
        resource.destroy();
    }
}
