package xin.manong.stream.test;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.stream.framework.resource.ResourceConfig;
import xin.manong.stream.framework.resource.ResourceManager;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.stream.test.resource.AutoIncreasedIDBuilder;
import xin.manong.weapon.base.util.FileUtil;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author frankcl
 * @create 2019-06-01 12:31
 */
public class ResourceManagerSuite {

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
    public void testBorrowAndReturn() throws Exception {
        Resource resource = ResourceManager.borrowResource("idBuilder2");
        Assert.assertTrue(resource != null);
        AutoIncreasedIDBuilder idBuilder = (AutoIncreasedIDBuilder) resource.get();
        Assert.assertEquals(0L, idBuilder.getNewID().longValue());
        Assert.assertTrue(ResourceManager.returnResource(resource));

        resource = ResourceManager.borrowResource("idBuilder2");
        idBuilder = (AutoIncreasedIDBuilder) resource.get();
        Assert.assertEquals(1L, idBuilder.getNewID().longValue());
        Assert.assertTrue(ResourceManager.returnResource(resource));
    }

    @Test
    public void testUnregister() {
        ResourceManager.unregisterResource("idBuilder1");
        Resource resource = ResourceManager.borrowResource("idBuilder1");
        Assert.assertTrue(resource == null);
    }

    @Test
    public void testGetResource() {
        {
            AutoIncreasedIDBuilder idBuilder = ResourceManager.getResource(
                    "idBuilder1", AutoIncreasedIDBuilder.class);
            Assert.assertTrue(idBuilder.getNewID().longValue() == 0L);
        }
        {
            AutoIncreasedIDBuilder idBuilder = ResourceManager.getResource(
                    "idBuilder", AutoIncreasedIDBuilder.class);
            Assert.assertTrue(idBuilder == null);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testGetResourceAndException() {
        ResourceManager.getResource(AutoIncreasedIDBuilder.class);
    }

}
