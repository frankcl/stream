package com.manong.stream.boost.resource;

import com.google.common.util.concurrent.RateLimiter;
import com.manong.stream.sdk.resource.Resource;
import com.manong.weapon.base.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 速率控制器资源
 * 使用guava速率控制器实现
 *
 * @author frankcl
 * @create 2019-12-03 13:57:53
 */
public class RateLimiterResource extends Resource<RateLimiter> {

    private final static Logger logger = LoggerFactory.getLogger(RateLimiterResource.class);

    private final static String KEY_PERMITS_PER_SECOND = "permitsPerSecond";

    public RateLimiterResource(String name) {
        super(name);
    }

    @Override
    public RateLimiter create(Map<String, Object> configMap) {
        if (!configMap.containsKey(KEY_PERMITS_PER_SECOND)) {
            logger.error("missing param[{}]", KEY_PERMITS_PER_SECOND);
            return null;
        }
        Double permitsPerSecond = MapUtil.getValue(configMap, KEY_PERMITS_PER_SECOND, Double.class);
        if (permitsPerSecond == null) return null;
        return RateLimiter.create(permitsPerSecond);
    }

    @Override
    public void destroy() {
        logger.info("rate limiter has been destroyed");
        object = null;
    }
}
