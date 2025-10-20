package xin.manong.stream.configmap.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.configmap.core.EtcdConfigMap;
import xin.manong.weapon.base.util.MapUtil;
import xin.manong.weapon.base.util.ReflectUtil;

import java.util.Map;

/**
 * 布尔值资源
 *
 * @author frankcl
 * @date 2021-06-13 16:38:47
 */
public class BoolResource extends EtcdConfigResource<Boolean> {

    private final static Logger logger = LoggerFactory.getLogger(BoolResource.class);

    public BoolResource(String name) {
        super(name);
    }

    @Override
    public Boolean create(Map<String, Object> configMap) {
        String key = MapUtil.getValue(configMap, KEY_CONFIG_MAP_KEY, String.class);
        if (StringUtils.isEmpty(key)) return null;
        String value = EtcdConfigMap.get(key);
        if (StringUtils.isEmpty(value)) return null;
        logger.info("Create boolean resource success");
        addListener(key);
        return Boolean.parseBoolean(value);
    }

    @Override
    public void destroy() {
        logger.info("Destroy boolean resource success");
        object = null;
    }

    @Override
    public void processChanged(String key, String content) {
        try {
            logger.info("Boolean config is changed, reload it");
            boolean v = !StringUtils.isEmpty(content) && Boolean.parseBoolean(content);
            if (object == null) object = v;
            else ReflectUtil.setFieldValue(object, "value", v);
        } catch (Exception e) {
            logger.error("Reload boolean config:{} failed", key);
            logger.error(e.getMessage(), e);
        }
    }
}
