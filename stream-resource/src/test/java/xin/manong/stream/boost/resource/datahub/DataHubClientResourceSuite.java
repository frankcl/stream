package xin.manong.stream.boost.resource.datahub;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2023-07-06 15:29:33
 */
public class DataHubClientResourceSuite {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> keySecret = new HashMap<>();
        keySecret.put("accessKey", "ak");
        keySecret.put("secretKey", "sk");
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("endpoint", "https://dh-cn-hangzhou.aliyuncs.com");
        configMap.put("aliyunSecret", keySecret);
        DataHubClientResource resource = new DataHubClientResource("data_hub_client");
        resource.build(configMap);
        Assert.assertTrue(resource.get() != null);
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("endpoint", "https://dh-cn-hangzhou.aliyuncs.com");
        DataHubClientResource resource = new DataHubClientResource("data_hub_client");
        resource.build(configMap);
        Assert.assertTrue(resource.get() == null);
        resource.destroy();
    }
}
