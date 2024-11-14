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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author frankcl
 * @date 2019-06-01 12:31
 */
public class ResourceInjectorTest {

    private static class InjectObject {

        @Resource(name = "idBuilder1")
        AutoIncreasedIDBuilder idBuilder1;

        @Resource(name = "${idBuilder2}")
        AutoIncreasedIDBuilder idBuilder2;
    }

    private final String resourcesFile = Objects.requireNonNull(this.getClass().
            getResource("/resource/resources.json")).getPath();

    @Before
    public void setUp() {
        String content = FileUtil.read(resourcesFile, StandardCharsets.UTF_8);
        List<ResourceConfig> resourceConfigList = JSON.parseArray(content, ResourceConfig.class);
        Assert.assertNotNull(resourceConfigList);
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
        Assert.assertNotNull(injectObject.idBuilder1);
        Assert.assertNotNull(injectObject.idBuilder2);
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
