package com.manong.stream.framework.receiver;

import com.manong.stream.sdk.common.StreamConstants;
import com.manong.stream.framework.common.StreamManager;
import com.manong.stream.framework.processor.ProcessorConfig;
import com.manong.stream.framework.processor.ProcessorGraph;
import com.manong.stream.framework.processor.ProcessorGraphFactory;
import com.manong.stream.sdk.common.UnacceptableException;
import com.manong.stream.sdk.receiver.ReceiveConverter;
import com.manong.stream.sdk.receiver.ReceiveProcessor;
import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import com.manong.weapon.base.util.RandomID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

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
    private ThreadLocal<ProcessorGraph> processorGraph;

    public ReceiveProcessorImpl(String name, List<String> processors,
                                List<ProcessorConfig> processorGraphConfig,
                                ReceiveConverter converter) {
        this.name = name;
        this.processors = processors;
        this.processorGraphConfig = processorGraphConfig;
        this.converter = converter;
        this.processorGraph = new ThreadLocal<>();
    }

    @Override
    public void process(Object object) throws UnacceptableException {
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

    /**
     * 处理数据
     *
     * @param processor processor名称
     * @param processorGraph processorGraph实例
     * @param kvRecord 数据
     * @throws UnacceptableException 不可接受异常
     */
    private void process(String processor, ProcessorGraph processorGraph,
                         KVRecord kvRecord) throws UnacceptableException {
        long startProcessTime = System.currentTimeMillis();
        Context context = new Context();
        try {
            context.put(StreamConstants.STREAM_TRACE_ID, RandomID.build());
            kvRecord.put(StreamConstants.STREAM_RECEIVER, name);
            kvRecord.put(StreamConstants.STREAM_START_PROCESS_TIME, startProcessTime);
            KVRecords kvRecords = new KVRecords();
            kvRecords.addRecord(kvRecord);
            processorGraph.process(processor, kvRecords, context);
        } catch(UnacceptableException e) {
            logger.error("unacceptable exception occurred for receiver[{}]", name);
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("process record exception for receiver[{}]", name);
            logger.error(e.getMessage(), e);
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
        processorGraph.set(threadProcessorGraph);
        return threadProcessorGraph;
    }
}
