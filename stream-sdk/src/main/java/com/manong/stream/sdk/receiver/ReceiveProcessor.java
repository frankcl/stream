package com.manong.stream.sdk.receiver;

import com.manong.stream.sdk.common.UnacceptableException;
import com.manong.weapon.base.common.Context;

/**
 * 接收数据处理器
 * 处理接收器接受数据
 *
 * @author frankcl
 * @date 2022-08-01 10:18:20
 */
public abstract class ReceiveProcessor {

    protected ReceiveConverter converter;

    /**
     * 接收数据处理
     * 处理接收数据，将处理结果发送到下游processor进行处理
     *
     * @param context 上下文
     * @param object 接受数据
     * @throws UnacceptableException 不可接受异常
     */
    public abstract void process(Context context, Object object) throws UnacceptableException;
}
