package xin.manong.stream.configmap.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.configmap.core.EtcdConfigMap;
import xin.manong.weapon.aliyun.dashscope.DashScopeApiKey;
import xin.manong.weapon.base.util.MapUtil;

import java.util.Map;

/**
 * 阿里云DashScope API key
 *
 * @author frankcl
 * @date 2026-02-03 15:42:32
 */
public class DashScopeApiKeyResource extends EtcdConfigResource<String> {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeApiKeyResource.class);

    public DashScopeApiKeyResource(String name) {
        super(name);
    }

    @Override
    public void processChanged(String key, String content) {
        try {
            logger.info("DashScope api key:{} is changed, reload it", key);
            if (StringUtils.isEmpty(content)) return;
            object = DashScopeApiKey.apiKey = content;
        } catch (Exception e) {
            logger.error("Reload DashScope api key:{} failed", key);
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String create(Map<String, Object> configMap) {
        String key = MapUtil.getValue(configMap, KEY_CONFIG_MAP_KEY, String.class);
        if (StringUtils.isEmpty(key)) return null;
        String value = EtcdConfigMap.get(key);
        if (StringUtils.isEmpty(value)) return null;
        logger.info("Create DashScope api key resource success");
        addListener(key);
        DashScopeApiKey.apiKey = value;
        return value;
    }

    @Override
    public void destroy() {
        logger.info("Destroy DashScope api key success");
        object = null;
    }
}
