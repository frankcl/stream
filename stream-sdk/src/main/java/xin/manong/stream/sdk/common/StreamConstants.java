package xin.manong.stream.sdk.common;

/**
 * stream常量定义
 *
 * @author frankcl
 * @date 2022-07-28 23:04:07
 */
public class StreamConstants {

    /* stream保留字段前缀 */
    public final static String STREAM_PREFIX = "__STREAM_";
    /* 数据接收器 */
    public final static String STREAM_RECEIVER = "__STREAM_RECEIVER__";
    /* 当前数据处理器 */
    public final static String STREAM_PROCESSOR = "__STREAM_PROCESSOR__";
    /* 数据处理时间 */
    public final static String STREAM_PROCESS_TIME = "__STREAM_PROCESS_TIME__";
    /* 数据开始处理时间 */
    public final static String STREAM_START_PROCESS_TIME = "__STREAM_START_PROCESS_TIME__";
    /* 数据处理轨迹 */
    public final static String STREAM_PROCESS_TRACE = "__STREAM_PROCESS_TRACE__";
    /* 数据处理器时间统计 */
    public final static String STREAM_PROCESSOR_TIME = "__STREAM_PROCESSOR_TIME__";
    /* 异常发生数据处理器 */
    public final static String STREAM_EXCEPTION_PROCESSOR = "__STREAM_EXCEPTION_PROCESSOR__";
    /* 异常发生数据接收器 */
    public final static String STREAM_EXCEPTION_RECEIVER = "__STREAM_EXCEPTION_RECEIVER__";
    public final static String STREAM_EXCEPTION_STACK = "__STREAM_EXCEPTION_STACK__";
    /* 调试信息 */
    public final static String STREAM_DEBUG_MESSAGE = "__STREAM_DEBUG_MESSAGE__";
    /* 数据轨迹ID */
    public final static String STREAM_TRACE_ID = "__STREAM_TRACE_ID__";
    /* 数据ID */
    public final static String STREAM_RECORD_ID = "__STREAM_RECORD_ID__";
    /* 数据类型 */
    public final static String STREAM_RECORD_TYPE = "__STREAM_RECORD_TYPE__";
    /* 数据产生处理器 */
    public final static String STREAM_BIRTH_PROCESSOR = "__STREAM_BIRTH_PROCESSOR__";
    /* 流程跟踪数据集合 */
    public final static String STREAM_KEEP_WATCH = "__STREAM_KEEP_WATCH__";
    /* 数据历史信息 记录同一条数据被多条链路处理信息 */
    public final static String STREAM_HISTORY = "__STREAM_HISTORY__";
    /* 消息ID */
    public final static String STREAM_MESSAGE_ID = "__STREAM_MESSAGE_ID__";
    /* 消息key */
    public final static String STREAM_MESSAGE_KEY = "__STREAM_MESSAGE_KEY__";
    /* 消息topic */
    public final static String STREAM_MESSAGE_TOPIC = "__STREAM_MESSAGE_TOPIC__";
    /* 消息TAG */
    public final static String STREAM_MESSAGE_TAG = "__STREAM_MESSAGE_TAG__";
    /* 消息所属分区 */
    public final static String STREAM_MESSAGE_PARTITION = "__STREAM_MESSAGE_PARTITION__";
    /* 消息偏移 */
    public final static String STREAM_MESSAGE_OFFSET = "__STREAM_MESSAGE_OFFSET__";
    /* 消息产生时间戳 */
    public final static String STREAM_MESSAGE_TIMESTAMP = "__STREAM_MESSAGE_TIMESTAMP__";
}
