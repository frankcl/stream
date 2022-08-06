package com.manong.stream.framework.resource;

import com.alibaba.fastjson.JSON;
import com.manong.stream.test.common.TestObject;
import com.manong.weapon.base.util.FileUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @create 2019-06-01 12:31
 */
public class ResourceInjectorSuite {

    private String resourcesFile = this.getClass().getResource("/resource/resources.json").getPath();

    @Before
    public void setUp() {
        String content = FileUtil.read(resourcesFile, Charset.forName("UTF-8"));
        List<ResourceConfig> resourceConfigList = JSON.parseArray(content, ResourceConfig.class);
        Assert.assertTrue(resourceConfigList != null);
        for (ResourceConfig resourceConfig : resourceConfigList) {
            ResourceManager.registerResource(resourceConfig);
        }
    }

    @After
    public void tearDown() {
        ResourceManager.unregisterAllResources();
    }

    @Test
    public void testInjectSuccess() throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("counter2", "counter2");
        TestObject testObject = new TestObject();
        ResourceInjector.inject(testObject, configMap);
        Assert.assertTrue(testObject.counter1 != null);
        Assert.assertTrue(testObject.counter2 != null);
    }

    @Test(expected = RuntimeException.class)
    public void testInjectFail() throws Exception {
        ResourceManager.unregisterAllResources();
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("counter", "counter2");
        TestObject testObject = new TestObject();
        ResourceInjector.inject(testObject, configMap);
    }
}
