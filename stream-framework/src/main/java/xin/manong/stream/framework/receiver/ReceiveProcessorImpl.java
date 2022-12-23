package xin.manong.stream.framework.receiver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.common.StreamManager;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.stream.framework.processor.ProcessorGraph;
import xin.manong.stream.framework.processor.ProcessorGraphFactory;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.stream.sdk.common.UnacceptableException;
import xin.manong.stream.sdk.receiver.ReceiveConverter;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;
import xin.manong.weapon.alarm.Alarm;
import xin.manong.weapon.alarm.AlarmSender;
import xin.manong.weapon.alarm.AlarmStatus;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.base.util.RandomID;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 接收数据处理器实现
 * 处理接收数据，根据配置分发到对应processor进行处理
 *
 * @author frankcl
 * @date 2022-08-01 10:37:06
 */
public class ReceiveProcessorImpl extends ReceiveProcessor {

    private final static Logger logger = LoggerFactory.getLogger(ReceiveProcessorImpl.class);

    private String name;
    private List<String> processors;
    private List<ProcessorConfig> processorGraphConfig;
    private Queue<String> processorGraphIds;
    private ThreadLocal<ProcessorGraph> processorGraph;
    private AlarmSender alarmSender;

    public ReceiveProcessorImpl(String name, List<String> processors,
                                List<ProcessorConfig> processorGraphConfig,
                                ReceiveConverter converter) {
        this.name = name;
        this.processors = processors;
        this.processorGraphConfig = processorGraphConfig;
        this.converter = converter;
        this.processorGraphIds = new ConcurrentLinkedQueue<>();
        this.processorGraph = new ThreadLocal<>();
    }

    @Override
    public void process(Object object) throws Throwable {
        KVRecords kvRecords;
        Context context = new Context();
        try {
            kvRecords = converter == null ? (KVRecords) object : converter.convert(context, object);
            if (kvRecords == null) throw new RuntimeException("convert record failed");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            context.put(StreamConstants.STREAM_EXCEPTION_RECEIVER, name);
            context.put(StreamConstants.STREAM_DEBUG_MESSAGE, "接收数据转换异常");
            StreamManager.commitLog(context);
            return;
        }
        ProcessorGraph processorGraph = currentThreadProcessorGraph();
        for (String processor : processors) {
            for (int i = 0; i < kvRecords.getRecordCount(); i++) {
                process(processor, processorGraph, kvRecords.getRecord(i).copy());
            }
        }
    }

    @Override
    public void sweep() {
        while (!processorGraphIds.isEmpty()) {
            String id = processorGraphIds.poll();
            if (StringUtils.isEmpty(id)) continue;
            ProcessorGraphFactory.clean(id);
        }
    }

    /**
     * 处理异常
     *
     * @param kvRecord 数据
     * @param e 异常
     */
    private void handleException(KVRecord kvRecord, Throwable e) {
        if (alarmSender != null) {
            AlarmStatus alarmStatus = e instanceof UnacceptableException || e instanceof Error ?
                    AlarmStatus.FATAL : AlarmStatus.ERROR;
            Alarm alarm = new Alarm(String.format(alarmStatus == AlarmStatus.FATAL ?
                    "严重错误发生[%s]" : "链路异常发生[%s]", e.getMessage()), alarmStatus);
            alarmSender.submit(alarm);
        }
        kvRecord.put(StreamConstants.STREAM_DEBUG_MESSAGE, e.getMessage());
        logger.error(e.getMessage(), e);
        logger.error("process record exception for receiver[{}]", name);
    }

    /**
     * 处理数据
     *
     * @param processor processor名称
     * @param processorGraph processorGraph实例
     * @param kvRecord 数据
     * @throws UnacceptableException 不可接受异常
     */
    private void process(String processor, ProcessorGraph processorGraph,
                         KVRecord kvRecord) throws Throwable {
        long startProcessTime = System.currentTimeMillis();
        Context context = new Context();
        try {
            context.put(StreamConstants.STREAM_TRACE_ID, RandomID.build());
            kvRecord.put(StreamConstants.STREAM_RECEIVER, name);
            kvRecord.put(StreamConstants.STREAM_START_PROCESS_TIME, startProcessTime);
            KVRecords kvRecords = new KVRecords();
            kvRecords.addRecord(kvRecord);
            processorGraph.process(processor, kvRecords, context);
        } catch (UnacceptableException e) {
            handleException(kvRecord, e);
            throw e;
        } catch (Exception e) {
            handleException(kvRecord, e);
        } catch (Throwable t) {
            handleException(kvRecord, t);
            throw t;
        } finally {
            kvRecord.put(StreamConstants.STREAM_PROCESS_TIME, System.currentTimeMillis() - startProcessTime);
            Set<KVRecord> watchRecords = (Set<KVRecord>) context.get(StreamConstants.STREAM_KEEP_WATCH);
            for (KVRecord watchRecord : watchRecords) {
                StreamManager.commitLog(watchRecord);
            }
            context.sweep();
        }
    }

    /**
     * 获取当前线程processorGraph
     *
     * @throws UnacceptableException 不可接受异常
     * @return 当前线程processorGraph
     */
    private ProcessorGraph currentThreadProcessorGraph() throws UnacceptableException {
        ProcessorGraph threadProcessorGraph = processorGraph.get();
        if (threadProcessorGraph != null) return threadProcessorGraph;
        threadProcessorGraph = ProcessorGraphFactory.make(processorGraphConfig);
        processorGraphIds.add(threadProcessorGraph.getId());
        processorGraph.set(threadProcessorGraph);
        return threadProcessorGraph;
    }

    /**
     * 设置报警发送器
     *
     * @param alarmSender 报警发送器
     */
    public void setAlarmSender(AlarmSender alarmSender) {
        this.alarmSender = alarmSender;
    }
}
