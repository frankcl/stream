package xin.manong.stream.configmap.core;

import xin.manong.weapon.base.etcd.EtcdClient;
import xin.manong.weapon.base.event.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ETCD配置中心
 *
 * @author frankcl
 * @date 2025-10-20 13:32:23
 */
public class EtcdConfigMap {

    private static List<WatchValueConsumer> consumers;
    private static EtcdClient etcdClient;

    /**
     * 初始化ETCD配置中心
     *
     * @param etcdClient ETCD客户端
     */
    public static void init(EtcdClient etcdClient) {
        if (EtcdConfigMap.etcdClient == etcdClient) return;
        if (EtcdConfigMap.etcdClient != null) destroy();
        consumers = new ArrayList<>();
        EtcdConfigMap.etcdClient = etcdClient;
    }

    /**
     * 销毁ETCD配置中心
     */
    public static void destroy() {
        if (etcdClient != null) {
            if (consumers != null) {
                consumers.forEach(consumer -> etcdClient.removeWatch(consumer.getKey(), consumer));
            }
            etcdClient.close();
        }
        if (consumers != null) consumers.clear();
    }

    /**
     * 获取配置信息
     *
     * @param key 配置key
     * @return 配置值
     */
    public static String get(String key) {
        return etcdClient.get(key);
    }

    /**
     * 添加配置监听器
     *
     * @param key 配置key
     * @param listener 配置监听器
     */
    public static void addListener(String key, EventListener listener) {
        WatchValueConsumer consumer = new WatchValueConsumer(key, listener);
        etcdClient.addWatch(key, consumer);
        consumers.add(consumer);
    }
}
