package com.manong.stream.boost.receiver.memory;

import com.manong.stream.sdk.receiver.Receiver;

import java.util.Map;

/**
 * 内存数据接收器
 * 接收流程插件处理的数据，分发下游处理
 * 主要用于数据重新处理场景
 *
 * @author frankcl
 * @date 2022-08-04 23:04:57
 */
public class MemoryReceiver extends Receiver {

    public MemoryReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public void stop() {

    }
}
