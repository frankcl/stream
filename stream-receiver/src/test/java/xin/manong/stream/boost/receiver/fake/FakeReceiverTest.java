package xin.manong.stream.boost.receiver.fake;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.stream.framework.receiver.ReceiveProcessorImpl;
import xin.manong.stream.framework.resource.ResourceInjector;
import xin.manong.stream.framework.resource.ResourceManager;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;
import xin.manong.weapon.base.util.ReflectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 11:14:42
 */
public class FakeReceiverTest {

    private FakeReceiver receiver;

    @Before
    public void setUp() {

        List<String> processors = new ArrayList<>() { { add("processor"); } };
        List<ProcessorConfig> processorGraphConfig = new ArrayList<>();
        ProcessorConfig processorConfig = new ProcessorConfig();
        processorConfig.name = "processor";
        processorConfig.className = "xin.manong.stream.boost.receiver.plugin.DummyHandler";
        processorGraphConfig.add(processorConfig);
        ReceiveProcessor receiveProcessor = new ReceiveProcessorImpl(
                "fake_receive_processor", processors, processorGraphConfig, null);

        Map<String, Object> configMap = new HashMap<>();
        FakeReceiverConfig config = new FakeReceiverConfig();
        config.threadNum = 2;
        config.timeIntervalMs = 2000L;
        receiver = new FakeReceiver(JSON.parseObject(JSON.toJSONString(config)));
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
        Thread.sleep(3000);
    }
}
