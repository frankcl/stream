package com.manong.stream.sdk.receiver;

import java.util.Map;

/**
 * 数据接收器
 * 针对不同数据源实现不同接收器，实现数据接收器的启动和停止逻辑
 * 实现自行调用receiveProcessor处理接收数据
 *
 * @author frankcl
 * @date 2022-07-31 10:32:23
 */
public abstract class Receiver {

    /* 接收器配置 */
    protected Map<String, Object> configMap;
    /* 接收数据处理器，stream框架负责注入，对外不可见 */
    protected ReceiveProcessor receiveProcessor;

    public Receiver(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    /**
     * 启动接收器
     *
     * @return 成功返回true，否则返回false
     */
    public abstract boolean start();

    /**
     * 停止接收器
     *
     * @return 成功返回true，否则返回false
     */
    public abstract void stop();
}
