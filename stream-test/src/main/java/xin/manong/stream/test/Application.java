package xin.manong.stream.test;

import xin.manong.stream.framework.runner.StreamRunner;
import xin.manong.stream.sdk.annotation.StreamApplication;

/**
 * stream应用入口
 *
 * @author frankcl
 * @date 2022-12-12 20:59:56
 */
@StreamApplication(name = "fake_stream")
public class Application {

    public static void main(String[] args) throws Exception {
        StreamRunner.run(Application.class, args);
    }
}
