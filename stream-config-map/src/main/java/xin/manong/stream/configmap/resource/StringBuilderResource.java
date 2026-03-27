package xin.manong.stream.configmap.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.configmap.core.EtcdConfigMap;
import xin.manong.weapon.base.util.MapUtil;

import java.util.Map;

/**
 * 字符串资源
 *
 * @author frankcl
 * @date 2021-06-13 16:38:47
 */
public class StringBuilderResource extends EtcdConfigResource<StringBuilder> {

    private final static Logger logger = LoggerFactory.getLogger(StringBuilderResource.class);

    public StringBuilderResource(String name) {
        super(name);
    }

    @Override
    public StringBuilder create(Map<String, Object> configMap) {
        String key = MapUtil.getValue(configMap, KEY_CONFIG_MAP_KEY, String.class);
        if (StringUtils.isEmpty(key)) return null;
        String value = EtcdConfigMap.get(key);
        if (StringUtils.isEmpty(value)) return null;
        logger.info("Create string resource success");
        addListener(key);
        return new StringBuilder(value);
    }

    @Override
    public void destroy() {
        logger.info("Destroy boolean resource success");
        object = null;
    }

    @Override
    public void processChanged(String key, String content) {
        try {
            logger.info("String config is changed, reload it");
            if (content == null) return;
            if (object == null) object = new StringBuilder(content);
            else {
                object.setLength(0);
                object.append(content);
            }
        } catch (Exception e) {
            logger.error("Reload string config:{} failed", key);
            logger.error(e.getMessage(), e);
        }
    }
}
