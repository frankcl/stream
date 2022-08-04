package com.manong.stream.sdk.receiver;

import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.record.KVRecords;

import java.util.Map;

/**
 * 接收数据转换器
 *
 * @author frankcl
 * @date 2022-08-01 10:32:15
 */
public abstract class ReceiveConverter {

    protected Map<String, Object> configMap;

    public ReceiveConverter(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    /**
     * 初始化
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
        return true;
    }

    /**
     * 销毁
     */
    public void destroy() {
    }

    /**
     * 处理数据
     *
     * @param context 上下文
     * @param object 待处理数据
     * @throws Exception 转换异常
     * @return KVRecords
     */
    public abstract KVRecords convert(Context context, Object object) throws Exception;
}
