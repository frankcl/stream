package xin.manong.stream.test;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.stream.framework.receiver.ReceiveControllerConfig;
import xin.manong.stream.framework.receiver.ReceiveManager;
import xin.manong.weapon.base.util.FileUtil;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author frankcl
 * @date 2022-08-04 17:11:47
 */
public class ReceiveManagerSuite {

    private String receiversFile = this.getClass().getResource(
            "/receiver/receivers.json").getPath();
    private String processorGraphFile = this.getClass().getResource(
            "/processor/processor_graph.json").getPath();
    private ReceiveManager receiveManager;

    @Before
    public void setUp() {
        String content = FileUtil.read(processorGraphFile, Charset.forName("UTF-8"));
        List<ProcessorConfig> processorConfigList = JSON.parseArray(content, ProcessorConfig.class);
        Assert.assertTrue(processorConfigList != null);
        content = FileUtil.read(receiversFile, Charset.forName("UTF-8"));
        List<ReceiveControllerConfig> receiverConfigList = JSON.parseArray(content, ReceiveControllerConfig.class);
        Assert.assertTrue(receiverConfigList != null);
        receiveManager = new ReceiveManager(receiverConfigList, processorConfigList);
        Assert.assertTrue(receiveManager.init());
    }

    @After
    public void tearDown() {
        receiveManager.destroy();
    }

    @Test
    public void testProcessRecords() throws Exception {
        Assert.assertTrue(receiveManager.start());
        Thread.sleep(3000);
    }
}
