package com.manong.stream.framework.common;

import java.util.HashSet;
import java.util.Set;

/**
 * 数据流管理
 *
 * @author frankcl
 * @date 2022-08-03 15:03:06
 */
public class StreamManager {

    /* 日志key */
    public static Set<String> loggerKeys = initLoggerKeys();

    /**
     * 初始化日志字段，填充stream框架字段
     *
     * @return 初始化日志字段
     */
    private static Set<String> initLoggerKeys() {
        Set<String> loggerKeys = new HashSet<>();
        loggerKeys.add(StreamConstants.STREAM_DEBUG_MESSAGE);
        loggerKeys.add(StreamConstants.STREAM_EXCEPTION_PROCESSOR);
        loggerKeys.add(StreamConstants.STREAM_PROCESS_TIME);
        loggerKeys.add(StreamConstants.STREAM_RECEIVER);
        loggerKeys.add(StreamConstants.STREAM_PROCESS_TRACE);
        loggerKeys.add(StreamConstants.STREAM_PROCESSOR_TIME);
        loggerKeys.add(StreamConstants.STREAM_START_PROCESS_TIME);
        loggerKeys.add(StreamConstants.STREAM_EXCEPTION_RECEIVER);
        return loggerKeys;
    }
}
