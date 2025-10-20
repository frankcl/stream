package xin.manong.stream.configmap.secret;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.configmap.core.EtcdConfigMap;
import xin.manong.weapon.aliyun.secret.DynamicSecret;
import xin.manong.weapon.base.event.ChangeEvent;
import xin.manong.weapon.base.event.EventListener;
import xin.manong.weapon.base.rebuild.RebuildManager;

/**
 * ETCD动态秘钥监听器
 *
 * @author frankcl
 * @date 2025-10-20 14:15:29
 */
public class EtcdDynamicSecretListener implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(EtcdDynamicSecretListener.class);
    private static final String KEY = "weapon/aliyun/ak-sk";

    @Override
    public void init() throws Exception {
        onChange(new ChangeEvent<>(null, EtcdConfigMap.get(KEY)));
        EtcdConfigMap.addListener(KEY, this);
    }

    @Override
    public void onChange(@NotNull ChangeEvent<?> event) {
        String newValue = (String) event.getAfter();
        if (newValue == null) return;
        String[] items = newValue.trim().split("/");
        if (items.length != 2) {
            logger.error("Invalid AK/SK:{} format", newValue);
            throw new RuntimeException(String.format("Invalid AK/SK:%s format", newValue));
        }
        DynamicSecret.accessKey = items[0].trim();
        DynamicSecret.secretKey = items[1].trim();
        if (StringUtils.isEmpty(DynamicSecret.accessKey)) {
            logger.error("Access key is empty");
            throw new RuntimeException("Access key is empty");
        }
        if (StringUtils.isEmpty(DynamicSecret.secretKey)) {
            logger.error("Secret key is empty");
            throw new RuntimeException("Secret key is empty");
        }
        logger.info("Parse AK/SK:{} success", newValue);
        RebuildManager.rebuild();
    }
}
