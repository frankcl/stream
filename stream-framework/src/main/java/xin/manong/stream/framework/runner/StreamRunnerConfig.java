package xin.manong.stream.framework.runner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.stream.framework.receiver.ReceiveControllerConfig;
import xin.manong.stream.framework.resource.ResourceConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据流运行配置
 *
 * @author frankcl
 * @date 2022-08-03 14:37:09
 */
public class StreamRunnerConfig {

    private final static Logger logger = LoggerFactory.getLogger(StreamRunnerConfig.class);

    public String name;
    public String loggerFile;
    public List<String> loggerKeys;
    public List<ReceiveControllerConfig> receivers = new ArrayList<>();
    public List<ProcessorConfig> processors = new ArrayList<>();
    public List<ResourceConfig> resources = new ArrayList<>();

    /**
     * 检测有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(name)) {
            logger.error("stream name is empty");
            return false;
        }
        if (loggerKeys == null || loggerKeys.isEmpty()) {
            logger.warn("logger keys are empty");
        }
        return true;
    }
}
