package xin.manong.stream.boost.resource.log;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2023-07-06 15:24:16
 */
public class LogClientResourceTest {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> keySecret = new HashMap<>();
        keySecret.put("accessKey", "ak");
        keySecret.put("secretKey", "sk");
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("endpoint", "http://cn-hangzhou.log.aliyuncs.com");
        configMap.put("aliyunSecret", keySecret);
        LogClientResource resource = new LogClientResource("log_client");
        resource.build(configMap);
        Assert.assertNotNull(resource.get());
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("endpoint", "http://cn-hangzhou.log.aliyuncs.com");
        LogClientResource resource = new LogClientResource("log_client");
        resource.build(configMap);
        Assert.assertNull(resource.get());
        resource.destroy();
    }
}
