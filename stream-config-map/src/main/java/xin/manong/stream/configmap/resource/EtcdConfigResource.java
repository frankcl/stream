package xin.manong.stream.configmap.resource;

import org.jetbrains.annotations.NotNull;
import xin.manong.stream.configmap.core.EtcdConfigMap;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.event.ChangeEvent;
import xin.manong.weapon.base.event.EventListener;

/**
 * ETCD配置资源
 *
 * @author frankcl
 * @date 2025-10-20 15:07:19
 */
public abstract class EtcdConfigResource<T> extends Resource<T> {

    protected final static String KEY_CONFIG_MAP_KEY = "configMapKey";

    public EtcdConfigResource(String name) {
        super(name);
    }

    /**
     * 处理配置变化
     *
     * @param key 配置key
     * @param content 配置内容
     */
    public abstract void processChanged(String key, String content);

    /**
     * 添加配置变化监听器
     *
     * @param key 配置key
     */
    protected void addListener(String key) {
        EtcdConfigMap.addListener(key, new EventListener() {
            @Override
            public void onChange(@NotNull ChangeEvent<?> event) {
                processChanged(key, (String) event.getAfter());
            }
        });
    }
}
