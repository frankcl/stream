package com.manong.stream.boost.resource.ots;

import com.manong.stream.boost.resource.oss.OSSClientResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frankcl
 * @date 2022-08-12 14:07:54
 */
public class OTSClientResourceSuite {

    @Test
    public void testCreateSuccess() {
        Map<String, Object> keySecret = new HashMap<>();
        keySecret.put("accessKey", "ak");
        keySecret.put("secretKey", "sk");
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("keySecret", keySecret);
        configMap.put("instance", "test");
        configMap.put("endpoint", "http://newsDataTest-news-data-test.cn-hangzhou.vpc.ots.aliyuncs.com");
        OTSClientResource resource = new OTSClientResource("ots_client");
        resource.build(configMap);
        Assert.assertTrue(resource.get() != null);
        resource.destroy();
    }

    @Test
    public void testCreateFail() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("instance", "test");
        configMap.put("endpoint", "http://newsDataTest-news-data-test.cn-hangzhou.vpc.ots.aliyuncs.com");
        OTSClientResource resource = new OTSClientResource("ots_client");
        resource.build(configMap);
        Assert.assertTrue(resource.get() == null);
        resource.destroy();
    }
}
