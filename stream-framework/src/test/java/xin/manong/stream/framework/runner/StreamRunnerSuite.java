package xin.manong.stream.framework.runner;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.app.TestApplication;

/**
 * @author frankcl
 * @date 2022-08-04 17:27:09
 */
public class StreamRunnerSuite {

    private final static Logger logger = LoggerFactory.getLogger(StreamRunnerSuite.class);

    private String streamSchedulerFile = this.getClass().getResource(
            "/runner/scheduler.json").getPath();
    private StreamRunner streamRunner;

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

    @Test
    public void testRunWithApplication() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                StreamRunner.run(TestApplication.class, null);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
        thread.start();
        Thread.sleep(5000);
    }
}
