package xin.manong.stream.test;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.stream.framework.receiver.ReceiveProcessorImpl;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.base.util.FileUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author frankcl
 * @date 2022-08-04 16:38:51
 */
public class ReceiveProcessorTest {

    private final String processorGraphFile = Objects.requireNonNull(this.getClass().
            getResource("/processor/processor_graph.json")).getPath();
    private ReceiveProcessor receiveProcessor;

    @Before
    public void setUp() {
        String content = FileUtil.read(processorGraphFile, StandardCharsets.UTF_8);
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertNotNull(processorConfigList);
        List<String> processors = new ArrayList<>();
        processors.add("processor1");
        receiveProcessor = new ReceiveProcessorImpl("receiver", processors,
                processorConfigList, null);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testProcessNormalRecord() throws Throwable {
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
    public void testProcessNullRecord() throws Throwable {
        receiveProcessor.process(null);
    }
}
