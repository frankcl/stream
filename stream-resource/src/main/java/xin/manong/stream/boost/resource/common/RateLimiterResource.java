package xin.manong.stream.boost.resource.common;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.redis.RedisClient;
import xin.manong.weapon.base.util.MapUtil;

import java.time.Duration;
import java.util.Map;

/**
 * Redisson RateLimiter资源
 *
 * @author frankcl
 * @date 2023-03-03 13:58:20
 */
public class RateLimiterResource extends Resource<RRateLimiter> {

    private final static Logger logger = LoggerFactory.getLogger(RateLimiterResource.class);

    private final static String KEY_RATE_LIMITER_KEY = "rateLimiterKey";
    private final static String KEY_PERMITS = "permits";
    private final static String KEY_TIME_SPAN_SECONDS = "timeSpanSeconds";

    @xin.manong.stream.sdk.annotation.Resource(name = "${redisClient}")
    protected RedisClient redisClient;

    public RateLimiterResource(String name) {
        super(name);
    }

    @Override
    public RRateLimiter create(Map<String, Object> configMap) {
        if (!configMap.containsKey(KEY_PERMITS)) {
            logger.error("Missing param[{}]", KEY_PERMITS);
            return null;
        }
        if (!configMap.containsKey(KEY_TIME_SPAN_SECONDS)) {
            logger.error("Missing param[{}]", KEY_TIME_SPAN_SECONDS);
            return null;
        }
        if (!configMap.containsKey(KEY_RATE_LIMITER_KEY)) {
            logger.error("Missing param[{}]", KEY_RATE_LIMITER_KEY);
            return null;
        }
        String key = MapUtil.getValue(configMap, KEY_RATE_LIMITER_KEY, String.class);
        long permits = MapUtil.getValue(configMap, KEY_PERMITS, Number.class).longValue();
        int timeSpanSeconds = MapUtil.getValue(configMap, KEY_TIME_SPAN_SECONDS, Integer.class);
        if (StringUtils.isEmpty(key) || permits <= 0 || timeSpanSeconds <= 0) return null;
        RRateLimiter rateLimiter = redisClient.getRateLimiter(key);
        rateLimiter.setRate(RateType.OVERALL, permits, Duration.ofSeconds(timeSpanSeconds));
        logger.info("create redisson rate limiter success");
        return rateLimiter;
    }

    @Override
    public void destroy() {
        logger.info("destroy redisson rate limiter success");
        if (object != null) object.delete();
        object = null;
    }
}
