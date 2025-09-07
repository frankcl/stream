package xin.manong.stream.framework.processor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.StreamConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * processor配置
 *
 * @author frankcl
 * @date 2019-05-27 16:23
 */
public class ProcessorConfig {

    private final static Logger logger = LoggerFactory.getLogger(ProcessorConfig.class);

    public String name;
    public String className;
    public String pythonEnv;
    public Map<String, Object> pluginConfig = new HashMap<>();
    public Map<String, String> processors = new HashMap<>();

    /**
     * 检测processor配置合法性
     * processor名称和类名不能为空
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(name)) {
            logger.error("processor name is empty");
            return false;
        }
        if (StringUtils.isEmpty(className)) {
            logger.error("processor[{}] class name is empty", name);
            return false;
        }
        if (StringUtils.isNotEmpty(pythonEnv)) pluginConfig.put(StreamConstants.PYTHON_ENV, pythonEnv);
        return true;
    }
}
