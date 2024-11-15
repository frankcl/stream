package xin.manong.stream.framework.resource;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import xin.manong.stream.sdk.resource.Resource;

/**
 * 资源池化管理
 *
 * @author frankcl
 * @date 2019-06-01 11:08
 */
public class ResourcePool<T> extends GenericObjectPool<Resource<T>> {

    public ResourcePool(ResourceConfig resourceConfig) {
        super(new ResourceFactory<>(resourceConfig));
        GenericObjectPoolConfig<Resource<T>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(resourceConfig.num);
        poolConfig.setMaxIdle(resourceConfig.num);
        poolConfig.setMinIdle(0);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestOnBorrow(true);
        setConfig(poolConfig);
    }
}
