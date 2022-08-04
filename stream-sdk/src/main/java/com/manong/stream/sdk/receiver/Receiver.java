package com.manong.stream.sdk.receiver;

import java.util.Map;

/**
 * 数据接收器
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
