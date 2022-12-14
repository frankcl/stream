package xin.manong.stream.boost.resource.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 13:53:28
 */
public class RecordQueueResourceSuite {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("queueSize", 100);
        RecordQueueResource resource = new RecordQueueResource("record_queue");
        resource.build(configMap);
        Assert.assertTrue(resource.get() != null);
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("queueSize", -1);
        RecordQueueResource resource = new RecordQueueResource("record_queue");
        resource.build(configMap);
        Assert.assertTrue(resource.get() == null);
        resource.destroy();
    }
}
