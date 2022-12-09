package xin.manong.stream.boost.plugin;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.record.KVRecord;

import java.util.HashMap;

/**
 * @author frankcl
 * @date 2022-08-06 22:51:34
 */
public class SpeedControllerSuite {

    private final static Logger logger = LoggerFactory.getLogger(SpeedControllerSuite.class);

    private SpeedController speedController;

    @Before
    public void setUp() {
        speedController = new SpeedController(new HashMap<>());
        speedController.rateLimiter = RateLimiter.create(5d);
        Assert.assertTrue(speedController.init());
    }

    @After
    public void tearDown() {
        speedController.destroy();
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
