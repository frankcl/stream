package xin.manong.stream.framework.resource;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.util.ReflectParams;
import xin.manong.weapon.base.util.ReflectUtil;

/**
 * 资源工厂
 * 1. 资源池化
 * 2. 资源生命周期管理
 *
 * @author frankcl
 * @create 2019-06-01 12:00
 */
public class ResourceFactory implements PooledObjectFactory<Resource> {

    private final static Logger logger = LoggerFactory.getLogger(ResourceFactory.class);

    private ResourceConfig resourceConfig;

    public ResourceFactory(ResourceConfig resourceConfig) {
        if (!resourceConfig.check()) throw new RuntimeException("resource config is invalid");
        this.resourceConfig = resourceConfig;
    }

    /**
     * 创建资源
     *
     * @return 池化资源
     * @throws Exception
     */
    @Override
    public PooledObject<Resource> makeObject() throws Exception {
        ReflectParams params = new ReflectParams();
        params.types = new Class[] { String.class };
        params.values = new Object[] { resourceConfig.name };
        Resource resource = (Resource) ReflectUtil.newInstance(resourceConfig.className, params);
        if (resource == null) {
            logger.error("create resource[{}] failed for class[{}]",
                    resourceConfig.name, resourceConfig.className);
            return null;
        }
        ResourceInjector.inject(resource, resourceConfig.configMap);
        resource.build(resourceConfig.configMap);
        if (resource.get() == null) {
            logger.error("build resource[{}] failed", resourceConfig.name);
            return null;
        }
        logger.info("create resource success for name[{}]", resourceConfig.name);
        return new DefaultPooledObject<>(resource);
    }

    /**
     * 销毁资源
     *
     * @param pooledObject 待销毁池化资源
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<Resource> pooledObject) throws Exception {
        Resource resource = pooledObject.getObject();
        if (resource == null) {
            logger.warn("resource[{}] is null, ignore destroying", resourceConfig.name);
            return;
        }
        resource.destroy();
        logger.info("destroy resource success for name[{}]", resourceConfig.name);
    }

    /**
     * 验证资源有效性
     *
     * @param pooledObject 池化资源
     * @return 有效返回true，否则返回false
     */
    @Override
    public boolean validateObject(PooledObject<Resource> pooledObject) {
        Resource resource = pooledObject.getObject();
        if (resource == null) {
            logger.warn("resource[{}] is null, validate failed", resourceConfig.name);
            return false;
        }
        return resource.validate();
    }

    /**
     * 激活资源，暂无实现
     *
     * @param pooledObject 池化对象
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<Resource> pooledObject) throws Exception {
    }

    /**
     * 钝化资源，暂无实现
     *
     * @param pooledObject 池化对象
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<Resource> pooledObject) throws Exception {
    }
}
