package com.manong.stream.framework.resource;

import com.manong.stream.sdk.resource.Resource;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 资源池化管理
 *
 * @author frankcl
 * @create 2019-06-01 11:08
 */
public class ResourcePool extends GenericObjectPool<Resource> {

    public ResourcePool(ResourceConfig resourceConfig) {
        super(new ResourceFactory(resourceConfig));
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(resourceConfig.num);
        poolConfig.setMaxIdle(resourceConfig.num);
        poolConfig.setMinIdle(0);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestOnBorrow(true);
        setConfig(poolConfig);
    }

}
