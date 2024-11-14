package xin.manong.stream.boost.resource.kafka;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2023-01-06 14:11:52
 */
public class KafkaProducerResourceTest {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("servers", "localhost:8888");
        KafkaProducerResource resource = new KafkaProducerResource("kafka_producer");
        resource.build(configMap);
        Assert.assertNotNull(resource.get());
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("servers", "");
        KafkaProducerResource resource = new KafkaProducerResource("ons_producer");
        resource.build(configMap);
        Assert.assertNull(resource.get());
        resource.destroy();
    }
}
