package xin.manong.stream.framework.resource;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.util.FileUtil;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        Resource resource = ResourceManager.borrowResource("counter2");
        Assert.assertTrue(resource != null);
        AtomicInteger counter = (AtomicInteger) resource.get();
        Assert.assertEquals(10, counter.get());
        counter.addAndGet(2);
        Assert.assertTrue(ResourceManager.returnResource(resource));

        resource = ResourceManager.borrowResource("counter2");
        counter = (AtomicInteger) resource.get();
        Assert.assertEquals(12, counter.get());
        Assert.assertTrue(ResourceManager.returnResource(resource));
    }

    @Test
    public void testUnregister() {
        ResourceManager.unregisterResource("counter1");
        Resource resource = ResourceManager.borrowResource("counter1");
        Assert.assertTrue(resource == null);
    }

    @Test
    public void testGetResource() {
        {
            AtomicInteger counter = ResourceManager.getResource("counter1", AtomicInteger.class);
            Assert.assertTrue(counter.get() == 0);
        }
        {
            AtomicInteger counter = ResourceManager.getResource("counter", AtomicInteger.class);
            Assert.assertTrue(counter == null);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testGetResourceAndException() {
        ResourceManager.getResource(AtomicInteger.class);
    }

}
