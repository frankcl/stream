package xin.manong.stream.test;

import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.stream.framework.processor.ProcessorGraph;
import xin.manong.stream.framework.processor.ProcessorGraphFactory;
import xin.manong.stream.sdk.common.UnacceptableException;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.base.util.FileUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author frankcl
 * @date 2022-07-31 10:00:13
 */
public class ProcessorGraphTest {

    private final String processorGraphFile = Objects.requireNonNull(this.getClass().
            getResource("/processor/processor_graph.json")).getPath();
    private final String processorGraphCommonFile = Objects.requireNonNull(this.getClass().
            getResource("/processor/processor_graph_common.json")).getPath();
    private final String processorGraphCycleFile = Objects.requireNonNull(this.getClass().
            getResource("/processor/processor_graph_has_cycle.json")).getPath();
    private final String processorGraphNotFoundFile = Objects.requireNonNull(this.getClass().
            getResource("/processor/processor_graph_not_found.json")).getPath();

    @Test
    public void testMakeProcessorGraph() throws Exception {
        String content = FileUtil.read(processorGraphFile, StandardCharsets.UTF_8);
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertNotNull(processorConfigList);
        ProcessorGraph processorGraph = ProcessorGraphFactory.make(processorConfigList);
        Assert.assertNotNull(processorGraph);
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("fork", "success");
            KVRecords kvRecords = new KVRecords();
            kvRecords.addRecord(kvRecord);
            processorGraph.process("processor1", kvRecords, new Context());
        }
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("fork", "fail");
            KVRecords kvRecords = new KVRecords();
            kvRecords.addRecord(kvRecord);
            processorGraph.process("processor1", kvRecords, new Context());
        }
        ProcessorGraphFactory.sweep();
    }

    @Test
    public void testMakeProcessorGraphCommon() throws Exception {
        String content = FileUtil.read(processorGraphCommonFile, StandardCharsets.UTF_8);
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertNotNull(processorConfigList);
        ProcessorGraph processorGraph = ProcessorGraphFactory.make(processorConfigList);
        Assert.assertNotNull(processorGraph);
        ProcessorGraphFactory.sweep();
    }

    @Test
    public void testMakeProcessorGraphHasCycle() {
        String content = FileUtil.read(processorGraphCycleFile, StandardCharsets.UTF_8);
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertNotNull(processorConfigList);
        try {
            ProcessorGraphFactory.make(processorConfigList);
        } catch (UnacceptableException e) {
            ProcessorGraphFactory.sweep();
            return;
        }
        Assert.fail();

    }

    @Test
    public void testMakeProcessorGraphNotFound() {
        String content = FileUtil.read(processorGraphNotFoundFile, StandardCharsets.UTF_8);
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertNotNull(processorConfigList);
        try {
            ProcessorGraphFactory.make(processorConfigList);
        } catch (UnacceptableException e) {
            ProcessorGraphFactory.sweep();
            return;
        }
        Assert.fail();
    }
}
