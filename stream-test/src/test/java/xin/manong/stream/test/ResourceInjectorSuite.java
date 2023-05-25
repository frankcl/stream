package xin.manong.stream.test;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.stream.framework.resource.ResourceConfig;
import xin.manong.stream.framework.resource.ResourceInjector;
import xin.manong.stream.framework.resource.ResourceManager;
import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.stream.test.resource.AutoIncreasedIDBuilder;
import xin.manong.weapon.base.util.FileUtil;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frankcl
 * @create 2019-06-01 12:31
 */
public class ResourceInjectorSuite {

    private static class InjectObject {

        @Resource(name = "idBuilder1")
        AutoIncreasedIDBuilder idBuilder1;

        @Resource(name = "${idBuilder2}")
        AutoIncreasedIDBuilder idBuilder2;
    }

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
        configMap.put("idBuilder2", "idBuilder2");
        InjectObject injectObject = new InjectObject();
        ResourceInjector.inject(injectObject, configMap);
        Assert.assertTrue(injectObject.idBuilder1 != null);
        Assert.assertTrue(injectObject.idBuilder2 != null);
    }

    @Test(expected = RuntimeException.class)
    public void testInjectFail() throws Exception {
        ResourceManager.unregisterAllResources();
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("idBuilder1", "counter2");
        InjectObject injectObject = new InjectObject();
        ResourceInjector.inject(injectObject, configMap);
    }
}
