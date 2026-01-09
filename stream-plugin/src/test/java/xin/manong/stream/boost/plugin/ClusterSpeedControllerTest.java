package xin.manong.stream.boost.plugin;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.redis.RedisClient;
import xin.manong.weapon.base.redis.RedisSingleConfig;

import java.time.Duration;
import java.util.HashMap;

/**
 * @author frankcl
 * @date 2022-08-06 22:51:34
 */
public class ClusterSpeedControllerTest {

    private final static Logger logger = LoggerFactory.getLogger(ClusterSpeedControllerTest.class);

    private RRateLimiter rateLimiter;
    private RedisClient redisClient;
    private ClusterSpeedController speedController;

    @Before
    public void setUp() {
        RedisSingleConfig redisConfig = new RedisSingleConfig();
        redisConfig.db = 1;
        redisConfig.address = "127.0.0.1:6379";
        redisConfig.password = "";
        Assert.assertTrue(redisConfig.check());
        redisClient = RedisClient.buildRedisClient(redisConfig);
        rateLimiter = redisClient.getRateLimiter("TEST_STREAM_SPEED_CONTROLLER");
        rateLimiter.setRate(RateType.OVERALL, 10, Duration.ofSeconds(1));
        speedController = new ClusterSpeedController(new HashMap<>());
        speedController.rateLimiter = rateLimiter;
        Assert.assertTrue(speedController.init());
    }

    @After
    public void tearDown() {
        speedController.destroy();
        rateLimiter.delete();
        redisClient.close();
    }

    @Test
    public void testControlSpeed() throws Exception {
        for (int i = 0; i < 50; i++) {
            KVRecord kvRecord = new KVRecord();
            logger.info("handle record[{}]", i);
            speedController.handle(kvRecord);
        }
    }
}
