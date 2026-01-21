package xin.manong.stream.framework.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源管理器
 * 管理所有资源的生命周期
 *
 * @author frankcl
 * @date 2019-06-01 12:45
 */
public class ResourceManager {

    private final static Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    private static final Map<String, ResourcePool<?>> resourcePoolMap = new HashMap<>();

    /**
     * 根据资源类型获取资源实例
     * 资源归还资源池
     *
     * @param clazz 资源实例class
     * @return 获取成功返回资源实例，否则返回null；如果存在多个类型相同资源抛出异常
     * @param <T> 资源类型
     */
    public static <T> T getResource(Class<T> clazz) {
        synchronized (resourcePoolMap) {
            List<Object> resources = new ArrayList<>();
            for (ResourcePool<?> resourcePool : resourcePoolMap.values()) {
                Resource<?> resource = null;
                try {
                    resource = resourcePool.borrowObject();
                    Object object = resource.get();
                    if (!clazz.isAssignableFrom(object.getClass())) continue;
                    resources.add(object);
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                } finally {
                    if (resource != null) returnResource(resource);
                }
            }
            if (resources.isEmpty()) return null;
            if (resources.size() > 1) {
                logger.error("More than one candidate resource for class:{}", clazz.getName());
                throw new IllegalStateException(String.format(
                        "More than one candidate resource for class:%s", clazz.getName()));
            }
            return clazz.cast(resources.get(0));
        }
    }

    /**
     * 获取资源实例，资源归还资源池
     *
     * @param resourceName 资源名
     * @param clazz 资源实例class
     * @param <T> 资源实例类型
     * @return 获取成功返回资源实例，否则返回null
     */
    public static <T> T getResource(String resourceName, Class<T> clazz) {
        synchronized (resourcePoolMap) {
            Resource<?> resource = borrowResource(resourceName);
            if (resource == null) {
                logger.warn("Get resource failed for name:{}", resourceName);
                return null;
            }
            try {
                return clazz.cast(resource.get());
            } catch (Exception e) {
                logger.error("Convert resource failed for name:{}, class:{}",
                        resourceName, clazz.getName());
                logger.error(e.getMessage(), e);
                return null;
            } finally {
                returnResource(resource);
            }
        }
    }

    /**
     * 归还资源
     *
     * @param resource 待归还资源
     * @return 归还成功返回true，否则返回false
     */
    public static boolean returnResource(Resource<?> resource) {
        synchronized (resourcePoolMap) {
            ResourcePool pool = resourcePoolMap.get(resource.getName());
            if (pool == null) {
                logger.warn("Resource:{} is not found, ignore return", resource.getName());
                return false;
            }
            try {
                pool.returnObject(resource);
                return true;
            } catch (Exception e) {
                logger.error("Return resource:{} failed", resource.getName());
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
    public static Resource<?> borrowResource(String resourceName) {
        synchronized (resourcePoolMap) {
            ResourcePool<?> pool = resourcePoolMap.get(resourceName);
            if (pool == null) {
                logger.warn("Resource:{} is not found, ignore borrow", resourceName);
                return null;
            }
            try {
                return pool.borrowObject();
            } catch (Exception e) {
                logger.error("Borrow resource:{} failed", resourceName);
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
                logger.info("Resource:{} has been registered, unregister it", resourceConfig.name);
                unregisterResource(resourceConfig.name);
            }
            ResourcePool<?> pool = new ResourcePool<>(resourceConfig);
            resourcePoolMap.put(resourceConfig.name, pool);
            logger.info("Register resource success for name:{}", resourceConfig.name);
        }
    }

    /**
     * 注销资源
     *
     * @param resourceName 资源名
     */
    public static void unregisterResource(String resourceName) {
        if (!resourcePoolMap.containsKey(resourceName)) {
            logger.warn("Resource:{} is not found, ignore unregister", resourceName);
            return;
        }
        synchronized (resourcePoolMap) {
            ResourcePool<?> resourcePool = resourcePoolMap.remove(resourceName);
            if (resourcePool == null) {
                logger.info("Resource:{} has been unregistered", resourceName);
                return;
            }
            resourcePool.close();
            logger.info("Unregister resource success for name:{}", resourceName);
        }
    }

    /**
     * 注销所有资源
     */
    public static void unregisterAllResources() {
        synchronized (resourcePoolMap) {
            for (ResourcePool<?> pool : resourcePoolMap.values()) pool.close();
            resourcePoolMap.clear();
            logger.info("Unregister all resources success");
        }
    }
}
