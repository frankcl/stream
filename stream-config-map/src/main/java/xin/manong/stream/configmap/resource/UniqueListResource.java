package xin.manong.stream.configmap.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.configmap.core.EtcdConfigMap;
import xin.manong.weapon.base.util.MapUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据集合资源封装
 *
 * @author frankcl
 * @date 2025-10-24 19:23
 */
public class UniqueListResource extends EtcdConfigResource<Set<String>> {

    private final static Logger logger = LoggerFactory.getLogger(UniqueListResource.class);

    public UniqueListResource(String name) {
        super(name);
    }

    @Override
    public Set<String> create(Map<String, Object> configMap) {
        String key = MapUtil.getValue(configMap, KEY_CONFIG_MAP_KEY, String.class);
        if (StringUtils.isEmpty(key)) return null;
        String content = EtcdConfigMap.get(key);
        if (StringUtils.isEmpty(content)) return null;
        Set<String> uniqueList = Arrays.stream(content.trim().split("\n")).filter(
                StringUtils::isNotEmpty).collect(Collectors.toSet());
        addListener(key);
        logger.info("Create unique list resource success");
        return uniqueList;
    }

    @Override
    public void destroy() {
        logger.info("destroy set resource success");
        object = null;
    }

    /**
     * 处理配置变化
     *
     * @param key 配置key
     * @param content 配置内容
     */
    @Override
    public void processChanged(String key, String content) {
        try {
            logger.info("Unique list config[{}] is changed, reload it", key);
            Set<String> uniqueList = Arrays.stream(content.trim().split("\n")).filter(
                    StringUtils::isNotEmpty).collect(Collectors.toSet());
            if (uniqueList.isEmpty()) return;
            if (object == null) {
                object = uniqueList;
                return;
            }
            object.addAll(uniqueList);
            object.removeIf(name -> !uniqueList.contains(name));
        } catch (Exception e) {
            logger.error("Reload unique list config[{}] failed", key);
            logger.error(e.getMessage(), e);
        }
    }
}
