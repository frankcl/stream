package com.manong.stream.framework.receiver;

import com.alibaba.fastjson.JSON;
import com.manong.stream.framework.processor.ProcessorConfig;
import com.manong.stream.sdk.receiver.ReceiveProcessor;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import com.manong.weapon.base.util.FileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author frankcl
 * @date 2022-08-04 16:38:51
 */
public class ReceiveProcessorSuite {

    private String processorGraphFile = this.getClass().getResource(
            "/processor/processor_graph.json").getPath();
    private ReceiveProcessor receiveProcessor;

    @Before
    public void setUp() {
        String content = FileUtil.read(processorGraphFile, Charset.forName("UTF-8"));
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertTrue(processorConfigList != null);
        List<String> processors = new ArrayList<>();
        processors.add("processor1");
        receiveProcessor = new ReceiveProcessorImpl("receiver", processors,
                processorConfigList, null);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testProcessNormalRecord() throws Exception {
        KVRecords kvRecords = new KVRecords();
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("key", "value1");
            kvRecord.put("fork", "success");
            kvRecords.addRecord(kvRecord);
        }
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("key", "value2");
            kvRecord.put("fork", "fail");
            kvRecords.addRecord(kvRecord);
        }
        receiveProcessor.process(kvRecords);
    }

    @Test
    public void testProcessNullRecord() throws Exception {
        receiveProcessor.process(null);
    }
}
