package xin.manong.stream.framework.common;

import xin.manong.stream.sdk.common.StreamConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.log.JSONLogger;
import xin.manong.weapon.base.record.KVRecord;

import java.util.*;

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
     * 提交stream日志
     *
     * @param kvRecord 数据
     */
    public static void commitLog(KVRecord kvRecord) {
        Map<String, Object> featureMap = new HashMap<>(kvRecord.getFieldMap());
        featureMap.put(StreamConstants.STREAM_RECORD_ID, kvRecord.getId());
        featureMap.put(StreamConstants.STREAM_RECORD_TYPE, kvRecord.getRecordType().name());
        if (streamLogger != null) streamLogger.commit(featureMap);
        else logger.warn("stream logger not init");
    }

    /**
     * 提交stream日志
     *
     * @param context 上下文
     */
    public static void commitLog(Context context) {
        if (streamLogger != null) streamLogger.commit(context.getFeatureMap());
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
        String traceId = (String) context.get(StreamConstants.STREAM_TRACE_ID);
        String processor = (String) context.get(StreamConstants.STREAM_PROCESSOR);
        if (!StringUtils.isEmpty(traceId)) kvRecord.put(StreamConstants.STREAM_TRACE_ID, traceId);
        if (!StringUtils.isEmpty(processor)) kvRecord.put(StreamConstants.STREAM_BIRTH_PROCESSOR, processor);
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
        baseLoggerKeys.add(StreamConstants.STREAM_BIRTH_PROCESSOR);
        baseLoggerKeys.add(StreamConstants.STREAM_RECORD_TYPE);
        baseLoggerKeys.add(StreamConstants.STREAM_RECORD_ID);
        baseLoggerKeys.add(StreamConstants.STREAM_TRACE_ID);
        baseLoggerKeys.add(StreamConstants.STREAM_MESSAGE_ID);
        baseLoggerKeys.add(StreamConstants.STREAM_MESSAGE_KEY);
        baseLoggerKeys.add(StreamConstants.STREAM_MESSAGE_TOPIC);
        baseLoggerKeys.add(StreamConstants.STREAM_MESSAGE_TAG);
        baseLoggerKeys.add(StreamConstants.STREAM_MESSAGE_PARTITION);
        baseLoggerKeys.add(StreamConstants.STREAM_MESSAGE_OFFSET);
        baseLoggerKeys.add(StreamConstants.STREAM_MESSAGE_TIMESTAMP);
        return baseLoggerKeys;
    }
}
