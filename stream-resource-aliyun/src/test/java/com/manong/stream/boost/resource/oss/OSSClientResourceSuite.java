package com.manong.stream.boost.resource.oss;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 14:04:47
 */
public class OSSClientResourceSuite {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> keySecret = new HashMap<>();
        keySecret.put("accessKey", "ak");
        keySecret.put("secretKey", "sk");
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("keySecret", keySecret);
        configMap.put("endpoint", "http://oss-cn-hangzhou.aliyuncs.com");
        OSSClientResource resource = new OSSClientResource("oss_client");
        resource.build(configMap);
        Assert.assertTrue(resource.get() != null);
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("endpoint", "http://oss-cn-hangzhou.aliyuncs.com");
        OSSClientResource resource = new OSSClientResource("oss_client");
        resource.build(configMap);
        Assert.assertTrue(resource.get() == null);
        resource.destroy();
    }
}
