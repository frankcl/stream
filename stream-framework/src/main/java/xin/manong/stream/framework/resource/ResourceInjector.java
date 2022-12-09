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
 * @create 2019-06-13 11:20
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
        if (!matcher.matches()) return resource.name();
        String name = matcher.group(1);
        if (resource.required() && (configMap == null || !configMap.containsKey(name))) {
            String message = String.format("resource name is not found in config map for key[{}]", name);
            logger.error(message);
            throw new RuntimeException(message);
        }
        return (String) configMap.get(name);
    }

    /**
     * 注入资源
     *
     * @param object 注入对象
     * @param configMap 配置
     */
    public static void inject(Object object, Map<String, Object> configMap) {
        Field[] fields = ReflectUtil.getFields(object);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Resource resource = field.getAnnotation(Resource.class);
            if (resource == null) continue;
            if (StringUtils.isEmpty(resource.name())) {
                String message = String.format("resource name is empty for field[%s] in object[%s]",
                        field.getName(), object.getClass().getName());
                logger.error(message);
                throw new RuntimeException(message);
            }
            String name = parseResourceName(resource, configMap);
            Object resourceObject = ResourceManager.getResource(name, field.getType());
            if (resourceObject == null && resource.required()) {
                String message = String.format("resource[%s] is not found for field[%s] of object[%s]",
                        name, field.getName(), object.getClass().getName());
                logger.error(message);
                throw new RuntimeException(message);
            }
            try {
                ReflectUtil.setFieldValue(object, field.getName(), resourceObject);
            } catch (Exception e) {
                String message = String.format("inject resource[%s] failed for field[%s] of object[%s]",
                        name, field.getName(), object.getClass().getName());
                logger.error(message);
                logger.error(e.getMessage(), e);
                throw new RuntimeException(message);
            }
        }
    }
}
