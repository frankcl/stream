package com.manong.stream.framework.common;

import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.log.JSONLogger;
import com.manong.weapon.base.record.KVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据流管理
 *
 * @author frankcl
 * @date 2022-08-03 15:03:06
 */
public class StreamManager {

    private final static Logger logger = LoggerFactory.getLogger(StreamManager.class);

    /* 缺省stream框架日志文件 */
    private final static String STREAM_LOGGER_FILE = "./logs/stream.log";
    /* 基本日志key */
    private final static Set<String> BASE_LOGGER_KEYS = buildBaseLoggerKeys();

    /* stream框架日志 */
    private static JSONLogger streamLogger;

    /**
     * 构建stream框架日志
     *
     * @param filename 日志文件
     * @param loggerKeys 日志记录key集合
     */
    public static void buildStreamLogger(String filename, List<String> loggerKeys) {
        Set<String> keys = new HashSet<>(loggerKeys);
        keys.addAll(BASE_LOGGER_KEYS);
        filename = StringUtils.isEmpty(filename) ? STREAM_LOGGER_FILE : filename;
        streamLogger = new JSONLogger(filename, keys);
    }

    /**
     * 提交stream框架日志
     *
     * @param featureMap 日志数据
     */
    public static void commitLog(Map<String, Object> featureMap) {
        if (streamLogger != null) streamLogger.commit(featureMap);
        else logger.warn("stream logger not init");
    }

    /**
     * 监管流程数据
     *
     * @param kvRecord 数据
     * @param context 上下文
     */
    public static void keepWatchRecord(KVRecord kvRecord, Context context) {
        Set<KVRecord> watchRecords = context.contains(StreamConstants.STREAM_KEEP_WATCH) ?
            (Set<KVRecord>) context.get(StreamConstants.STREAM_KEEP_WATCH) : new HashSet<>();
        if (watchRecords.contains(kvRecord)) return;
        watchRecords.add(kvRecord);
        if (!context.contains(StreamConstants.STREAM_KEEP_WATCH)) {
            context.put(StreamConstants.STREAM_KEEP_WATCH, watchRecords);
        }
    }

    /**
     * 初始化日志字段，填充stream框架字段
     *
     * @return 初始化日志字段
     */
    private static Set<String> buildBaseLoggerKeys() {
        Set<String> baseLoggerKeys = new HashSet<>();
        baseLoggerKeys.add(StreamConstants.STREAM_DEBUG_MESSAGE);
        baseLoggerKeys.add(StreamConstants.STREAM_EXCEPTION_PROCESSOR);
        baseLoggerKeys.add(StreamConstants.STREAM_PROCESS_TIME);
        baseLoggerKeys.add(StreamConstants.STREAM_RECEIVER);
        baseLoggerKeys.add(StreamConstants.STREAM_PROCESS_TRACE);
        baseLoggerKeys.add(StreamConstants.STREAM_PROCESSOR_TIME);
        baseLoggerKeys.add(StreamConstants.STREAM_START_PROCESS_TIME);
        baseLoggerKeys.add(StreamConstants.STREAM_EXCEPTION_RECEIVER);
        return baseLoggerKeys;
    }
}
