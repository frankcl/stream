package xin.manong.stream.boost.resource.oss;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 14:04:47
 */
public class OSSClientResourceTest {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> keySecret = new HashMap<>();
        keySecret.put("accessKey", "ak");
        keySecret.put("secretKey", "sk");
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("aliyunSecret", keySecret);
        configMap.put("endpoint", "http://oss-cn-hangzhou.aliyuncs.com");
        OSSClientResource resource = new OSSClientResource("oss_client");
        resource.build(configMap);
        Assert.assertNotNull(resource.get());
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("endpoint", "http://oss-cn-hangzhou.aliyuncs.com");
        OSSClientResource resource = new OSSClientResource("oss_client");
        resource.build(configMap);
        Assert.assertNull(resource.get());
        resource.destroy();
    }
}
