package xin.manong.stream.boost.resource.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 13:46:26
 */
public class RateLimiterResourceSuite {

    @Test
    public void testBuildSuccess() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("permitsPerSecond", 10d);
        RateLimiterResource resource = new RateLimiterResource("rate_limiter");
        resource.build(configMap);
        Assert.assertTrue(resource.get() != null);
        resource.destroy();
    }

    @Test
    public void testBuildFail() {
        Map<String, Object> configMap = new HashMap<>();
        RateLimiterResource resource = new RateLimiterResource("rate_limiter");
        resource.build(configMap);
        Assert.assertTrue(resource.get() == null);
        resource.destroy();
    }
}
