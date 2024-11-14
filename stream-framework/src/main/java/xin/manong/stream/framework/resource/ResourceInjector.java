package xin.manong.stream.framework.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.weapon.base.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 资源注入器
 * 根据注解为目标对象注入资源
 *
 * @author frankcl
 * @date 2019-06-13 11:20
 */
public class ResourceInjector {

    private final static Logger logger = LoggerFactory.getLogger(ResourceInjector.class);

    private final static Pattern resourceNamePattern = Pattern.compile("\\$\\{(.+?)\\}");

    /**
     * 解析资源名
     *
     * @param resource 资源注解
     * @param configMap 配置
     * @return 资源名
     */
    private static String parseResourceName(Resource resource, Map<String, Object> configMap) {
        Matcher matcher = resourceNamePattern.matcher(resource.name());
        if (!matcher.matches()) return resource.name().trim();
        String name = matcher.group(1).trim();
        if (configMap == null || !configMap.containsKey(name)) {
            String message = String.format("resource name is not found in config map for key[%s]", name);
            logger.error(message);
            throw new IllegalStateException(message);
        }
        return (String) configMap.get(name);
    }

    /**
     * 根据资源名或类型获取资源，获取资源方式如下
     * 1. 根据资源名获取
     * 2. 根据资源类型获取
     *
     * @param resourceName 资源名
     * @param field 字段
     * @return 成功返回资源实例，否则返回null；如果存在多个资源抛出异常
     */
    private static Object getResource(String resourceName, Field field) {
        Class<?> fieldClass = field.getType();
        if (StringUtils.isEmpty(resourceName)) {
            return ResourceManager.getResource(fieldClass);
        } else {
            return ResourceManager.getResource(resourceName, fieldClass);
        }
    }

    /**
     * 注入资源
     *
     * @param object 注入对象
     * @param configMap 配置
     */
    public static void inject(Object object, Map<String, Object> configMap) {
        Field[] fields = ReflectUtil.getFields(object);
        for (Field field : fields) {
            Resource resource = field.getAnnotation(Resource.class);
            if (resource == null) continue;
            String name = parseResourceName(resource, configMap);
            Object resourceObject = getResource(name, field);
            if (resourceObject == null && resource.required()) {
                String message = String.format("resource[%s] is not found for field[%s] of object[%s]",
                        StringUtils.isEmpty(name) ? field.getType().getName() : name, field.getName(),
                        object.getClass().getName());
                logger.error(message);
                throw new IllegalStateException(message);
            }
            try {
                ReflectUtil.setFieldValue(object, field.getName(), resourceObject);
            } catch (Exception e) {
                String message = String.format("inject resource[%s] failed for field[%s] of object[%s]",
                        StringUtils.isEmpty(name) ? field.getType().getName() : name, field.getName(),
                        object.getClass().getName());
                logger.error(message);
                logger.error(e.getMessage(), e);
                throw new RuntimeException(message);
            }
        }
    }
}
