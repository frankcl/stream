package com.manong.stream.framework.receiver;

import com.manong.stream.sdk.receiver.ReceiveConverter;
import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.record.KVRecords;

import java.util.Map;

/**
 * 直接转换为KVRecords
 *
 * @author frankcl
 * @date 2022-08-04 16:37:10
 */
public class DummyConverter extends ReceiveConverter {

    public DummyConverter(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public KVRecords convert(Context context, Object object) throws Exception {
        return (KVRecords) object;
    }
}
