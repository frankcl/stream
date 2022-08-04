package com.manong.stream.framework.processor;

import com.alibaba.fastjson.JSON;
import com.manong.stream.sdk.common.UnacceptableException;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import com.manong.weapon.base.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author frankcl
 * @date 2022-07-31 10:00:13
 */
public class ProcessorGraphSuite {

    private String processorGraphFile = this.getClass().getResource(
            "/processor/processor_graph.json").getPath();
    private String processorGraphCycleFile = this.getClass().getResource(
            "/processor/processor_graph_has_cycle.json").getPath();
    private String processorGraphNotFoundFile = this.getClass().getResource(
            "/processor/processor_graph_not_found.json").getPath();

    @Test
    public void testMakeProcessorGraph() throws Exception {
        String content = FileUtil.read(processorGraphFile, Charset.forName("UTF-8"));
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertTrue(processorConfigList != null);
        ProcessorGraph processorGraph = ProcessorGraphFactory.make(processorConfigList);
        Assert.assertTrue(processorGraph != null);
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("fork", "success");
            KVRecords kvRecords = new KVRecords();
            kvRecords.addRecord(kvRecord);
            processorGraph.process("dummy_processor1", kvRecords);
        }
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("fork", "fail");
            KVRecords kvRecords = new KVRecords();
            kvRecords.addRecord(kvRecord);
            processorGraph.process("dummy_processor1", kvRecords);
        }
        ProcessorGraphFactory.sweep();
    }

    @Test
    public void testMakeProcessorGraphHasCycle() {
        String content = FileUtil.read(processorGraphCycleFile, Charset.forName("UTF-8"));
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertTrue(processorConfigList != null);
        try {
            ProcessorGraphFactory.make(processorConfigList);
        } catch (UnacceptableException e) {
            ProcessorGraphFactory.sweep();
            return;
        }
        Assert.assertTrue(false);

    }

    @Test
    public void testMakeProcessorGraphNotFound() {
        String content = FileUtil.read(processorGraphNotFoundFile, Charset.forName("UTF-8"));
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertTrue(processorConfigList != null);
        try {
            ProcessorGraphFactory.make(processorConfigList);
        } catch (UnacceptableException e) {
            ProcessorGraphFactory.sweep();
            return;
        }
        Assert.assertTrue(false);
    }
}
