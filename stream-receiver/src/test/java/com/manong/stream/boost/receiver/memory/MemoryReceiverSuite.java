package com.manong.stream.boost.receiver.memory;

import com.alibaba.fastjson.JSON;
import com.manong.stream.framework.processor.ProcessorConfig;
import com.manong.stream.framework.processor.ProcessorGraph;
import com.manong.stream.framework.receiver.ReceiveProcessorImpl;
import com.manong.stream.framework.resource.ResourceConfig;
import com.manong.stream.framework.resource.ResourceInjector;
import com.manong.stream.framework.resource.ResourceManager;
import com.manong.stream.sdk.receiver.ReceiveProcessor;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import com.manong.weapon.base.util.ReflectUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 11:14:42
 */
public class MemoryReceiverSuite {

    private MemoryReceiver receiver;

    @Before
    public void setUp() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.name = "record_queue";
        resourceConfig.className = "com.manong.stream.boost.resource.RecordQueueResource";
        resourceConfig.configMap = new HashMap<>();
        resourceConfig.configMap.put("queueSize", 10);
        ResourceManager.registerResource(resourceConfig);

        List<String> processors = new ArrayList<String>() { { add("processor"); } };
        List<ProcessorConfig> processorGraphConfig = new ArrayList<>();
        ProcessorConfig processorConfig = new ProcessorConfig();
        processorConfig.name = "processor";
        processorConfig.className = "com.manong.stream.test.plugin.TestPlugin";
        processorGraphConfig.add(processorConfig);
        ProcessorGraph processorGraph = new ProcessorGraph(processorGraphConfig);
        ReceiveProcessor receiveProcessor = new ReceiveProcessorImpl(
                "memory_receive_processor", processors, processorGraphConfig, null);

        Map<String, Object> configMap = new HashMap<>();
        configMap.put("recordQueue", "record_queue");
        MemoryReceiverConfig config = new MemoryReceiverConfig();
        config.threadNum = 2;
        receiver = new MemoryReceiver(JSON.parseObject(JSON.toJSONString(config)));
        ReflectUtil.setFieldValue(receiver, "receiveProcessor", receiveProcessor);
        ResourceInjector.inject(receiver, configMap);
    }

    @After
    public void tearDown() {
        receiver.stop();
        ResourceManager.unregisterAllResources();
    }

    @Test
    public void testReceiver() throws Exception {
        Assert.assertTrue(receiver.start());
        KVRecord kvRecord = new KVRecord();
        kvRecord.put("k1", "v1");
        kvRecord.put("k2", 100L);
        KVRecords kvRecords = new KVRecords() { { addRecord(kvRecord); } };
        receiver.recordQueue.add(kvRecords);
        Thread.sleep(3000);
    }
}
