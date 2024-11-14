package xin.manong.stream.boost.resource.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 13:53:28
 */
public class RecordQueueResourceTest {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("queueSize", 100);
        RecordQueueResource resource = new RecordQueueResource("record_queue");
        resource.build(configMap);
        Assert.assertNotNull(resource.get());
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("queueSize", -1);
        RecordQueueResource resource = new RecordQueueResource("record_queue");
        resource.build(configMap);
        Assert.assertNull(resource.get());
        resource.destroy();
    }
}
