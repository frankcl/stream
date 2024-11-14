package xin.manong.stream.framework.prepare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.prepare.Preprocessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 预处理管理器
 *
 * @author frankcl
 * @date 2022-12-13 20:42:57
 */
public class PreprocessManager {

    private final static Logger logger = LoggerFactory.getLogger(PreprocessManager.class);

    private static final List<Preprocessor> preprocessors = new ArrayList<>();

    /**
     * 注册预处理器
     *
     * @param preprocessor 预处理器
     */
    public static void register(Preprocessor preprocessor) {
        if (preprocessor == null) {
            logger.warn("preprocessor is null, ignore registering");
            return;
        }
        synchronized (PreprocessManager.class) {
            preprocessors.add(preprocessor);
            logger.info("register preprocessor[{}] success", preprocessor.getClass().getName());
        }
    }

    /**
     * 执行预处理
     */
    public static void preprocess() {
        synchronized (PreprocessManager.class) {
            for (Preprocessor preprocessor : preprocessors) preprocessor.process();
        }
    }
}
