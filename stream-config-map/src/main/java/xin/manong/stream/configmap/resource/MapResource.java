package xin.manong.stream.configmap.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.configmap.core.EtcdConfigMap;
import xin.manong.weapon.base.util.MapUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据map资源封装
 *
 * @author frankcl
 * @date 2025-10-24 19:23
 */
public class MapResource extends EtcdConfigResource<Map<String, Object>> {

    private final static Logger logger = LoggerFactory.getLogger(MapResource.class);

    public MapResource(String name) {
        super(name);
    }

    @Override
    public Map<String, Object> create(Map<String, Object> configMap) {
        String key = MapUtil.getValue(configMap, KEY_CONFIG_MAP_KEY, String.class);
        if (StringUtils.isEmpty(key)) return null;
        String content = EtcdConfigMap.get(key);
        if (StringUtils.isEmpty(content)) return null;
        ConcurrentHashMap<String, Object> map = JSON.parseObject(content, new TypeReference<>() {});
        addListener(key);
        logger.info("Create map resource success");
        return map;
    }

    @Override
    public void destroy() {
        logger.info("Destroy map resource success");
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
            logger.info("Map config[{}] is changed, reload it", key);
            ConcurrentHashMap<String, Object> map = JSON.parseObject(content, new TypeReference<>() {});
            if (map.isEmpty()) return;
            if (object == null) {
                object = map;
                return;
            }
            MapUtil.safeReplace(object, map);
        } catch (Exception e) {
            logger.error("Reload map config[{}] failed", key);
            logger.error(e.getMessage(), e);
        }
    }
}
