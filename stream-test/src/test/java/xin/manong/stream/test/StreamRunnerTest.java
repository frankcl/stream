package xin.manong.stream.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.runner.StreamRunner;

import java.util.Objects;

/**
 * @author frankcl
 * @date 2022-08-04 17:27:09
 */
public class StreamRunnerTest {

    private final static Logger logger = LoggerFactory.getLogger(StreamRunnerTest.class);

    private final String streamSchedulerFile = Objects.requireNonNull(this.getClass().
            getResource("/runner/scheduler.json")).getPath();

    @Test
    public void testRunWithCmd() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                String[] args = new String[] {"-c", streamSchedulerFile};
                StreamRunner.run(null, args);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
        thread.start();
        Thread.sleep(5000);
    }
}
