package com.manong.stream.framework.resource;

import com.manong.stream.sdk.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源管理器
 * 管理所有资源的生命周期
 *
 * @author frankcl
 * @create 2019-06-01 12:45
 */
public class ResourceManager {

    private final static Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    private static Map<String, ResourcePool> resourcePoolMap = new HashMap<>();

    /**
     * 获取资源实例，资源归还资源池
     *
     * @param resourceName 资源名
     * @param clazz 资源实例class
     * @param <T> 资源实例类型
     * @return 获取成功返回资源实例，否则返回null
     */
    public static <T> T getResource(String resourceName, Class<T> clazz) {
        Resource resource = borrowResource(resourceName);
        if (resource == null) {
            logger.warn("get resource failed for name[{}]", resourceName);
            return null;
        }
        try {
            return clazz.cast(resource.get());
        } catch (Exception e) {
            logger.error("convert resource failed for name[{}], class[{}]",
                    resourceName, clazz.getName());
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            returnResource(resource);
        }
    }

    /**
     * 归还资源
     *
     * @param resource 待归还资源
     * @return 归还成功返回true，否则返回false
     */
    public static boolean returnResource(Resource resource) {
        synchronized (resourcePoolMap) {
            ResourcePool pool = resourcePoolMap.get(resource.getName());
            if (pool == null) {
                logger.warn("resource[{}] is not found, ignore return", resource.getName());
                return false;
            }
            try {
                pool.returnObject(resource);
                return true;
            } catch (Exception e) {
                logger.error("return resource[{}] failed", resource.getName());
                logger.error(e.getMessage(), e);
                return false;
            }
        }
    }

    /**
     * 借出资源
     * 支持多线程并发
     *
     * @param resourceName 资源名
     * @return 如果资源不存在返回null，否则返回资源对象
     */
    public static Resource borrowResource(String resourceName) {
        synchronized (resourcePoolMap) {
            ResourcePool pool = resourcePoolMap.get(resourceName);
            if (pool == null) {
                logger.warn("resource[{}] is not found, ignore borrow", resourceName);
                return null;
            }
            try {
                return pool.borrowObject();
            } catch (Exception e) {
                logger.error("borrow resource[{}] failed", resourceName);
                logger.error(e.getMessage(), e);
                return null;
            }
        }
    }

    /**
     * 注册资源
     *
     * @param resourceConfig 资源配置
     */
    public static void registerResource(ResourceConfig resourceConfig) {
        synchronized (resourcePoolMap) {
            if (resourcePoolMap.containsKey(resourceConfig.name)) {
                logger.info("resource[{}] has been registered, unregister it", resourceConfig.name);
                unregisterResource(resourceConfig.name);
            }
            ResourcePool pool = new ResourcePool(resourceConfig);
            resourcePoolMap.put(resourceConfig.name, pool);
            logger.info("register resource success for name[{}]", resourceConfig.name);
        }
    }

    /**
     * 注销资源
     *
     * @param resourceName 资源名
     */
    public static void unregisterResource(String resourceName) {
        if (!resourcePoolMap.containsKey(resourceName)) {
            logger.warn("resource[{}] is not found, ignore unregister", resourceName);
            return;
        }
        synchronized (resourcePoolMap) {
            ResourcePool resourcePool = resourcePoolMap.remove(resourceName);
            if (resourcePool == null) {
                logger.info("resource[{}] has been unregistered", resourceName);
                return;
            }
            resourcePool.close();
            logger.info("unregister resource success for name[{}]", resourceName);
        }
    }

    /**
     * 注销所有资源
     */
    public static void unregisterAllResources() {
        synchronized (resourcePoolMap) {
            for (ResourcePool pool : resourcePoolMap.values()) pool.close();
            resourcePoolMap.clear();
            logger.info("unregister all resources success");
        }
    }
}
