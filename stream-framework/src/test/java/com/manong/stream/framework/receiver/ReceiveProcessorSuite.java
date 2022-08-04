package com.manong.stream.framework.receiver;

import com.alibaba.fastjson.JSON;
import com.manong.stream.framework.processor.ProcessorConfig;
import com.manong.stream.sdk.receiver.ReceiveConverter;
import com.manong.stream.sdk.receiver.ReceiveProcessor;
import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import com.manong.weapon.base.util.FileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author frankcl
 * @date 2022-08-04 16:38:51
 */
public class ReceiveProcessorSuite {

    private String processorGraphFile = this.getClass().getResource(
            "/processor/processor_graph.json").getPath();
    private ReceiveConverter receiveConverter;
    private ReceiveProcessor receiveProcessor;

    @Before
    public void setUp() {
        String content = FileUtil.read(processorGraphFile, Charset.forName("UTF-8"));
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertTrue(processorConfigList != null);
        List<String> processors = new ArrayList<>();
        processors.add("dummy_processor1");
        receiveConverter = new DummyConverter(new HashMap<>());
        Assert.assertTrue(receiveConverter.init());
        receiveProcessor = new ReceiveProcessorImpl("test_receiver", processors,
                processorConfigList, receiveConverter);
    }

    @After
    public void tearDown() {
        receiveConverter.destroy();
    }

    @Test
    public void testProcessNormalRecord() throws Exception {
        KVRecords kvRecords = new KVRecords();
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("key", "value1");
            kvRecords.addRecord(kvRecord);
        }
        {
            KVRecord kvRecord = new KVRecord();
            kvRecord.put("key", "value2");
            kvRecords.addRecord(kvRecord);
        }
        Context context = new Context();
        receiveProcessor.process(context, kvRecords);
    }

    @Test
    public void testProcessNullRecord() throws Exception {
        Context context = new Context();
        receiveProcessor.process(context, null);
    }
}
