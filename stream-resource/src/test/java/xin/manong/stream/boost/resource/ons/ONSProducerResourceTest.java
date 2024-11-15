package xin.manong.stream.boost.resource.ons;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 13:57:51
 */
public class ONSProducerResourceTest {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> keySecret = new HashMap<>();
        keySecret.put("accessKey", "ak");
        keySecret.put("secretKey", "sk");
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("serverURL", "http://onsaddr.mq-internet-access.mq-internet.aliyuncs.com:80");
        configMap.put("aliyunSecret", keySecret);
        ONSProducerResource resource = new ONSProducerResource("ons_producer");
        resource.build(configMap);
        Assert.assertNotNull(resource.get());
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("serverURL", "http://onsaddr.mq-internet-access.mq-internet.aliyuncs.com:80");
        ONSProducerResource resource = new ONSProducerResource("ons_producer");
        resource.build(configMap);
        Assert.assertNull(resource.get());
        resource.destroy();
    }
}
