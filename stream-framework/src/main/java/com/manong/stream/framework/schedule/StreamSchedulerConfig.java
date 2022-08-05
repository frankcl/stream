package com.manong.stream.framework.schedule;

import com.manong.stream.framework.processor.ProcessorConfig;
import com.manong.stream.framework.receiver.ReceiveControllerConfig;
import com.manong.stream.framework.resource.ResourceConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据流调度器配置
 *
 * @author frankcl
 * @date 2022-08-03 14:37:09
 */
public class StreamSchedulerConfig {

    private final static Logger logger = LoggerFactory.getLogger(StreamSchedulerConfig.class);

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
            logger.error("app name is empty");
            return false;
        }
        if (loggerKeys == null || loggerKeys.isEmpty()) {
            logger.warn("logger keys are empty");
        }
        return true;
    }
}
