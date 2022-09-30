package com.manong.stream.framework.runner;

import org.junit.Test;

/**
 * @author frankcl
 * @date 2022-08-04 17:27:09
 */
public class StreamRunnerSuite {

    private String streamSchedulerFile = this.getClass().getResource(
            "/runner/scheduler.json").getPath();
    private StreamRunner streamRunner;

    @Test
    public void testRun() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.exit(0);
            } catch (Exception e) {
            }
        });
        thread.start();
        String[] args = new String[] {"-c", streamSchedulerFile};
        StreamRunner.run(args);
    }
}
