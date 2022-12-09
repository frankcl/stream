package xin.manong.stream.test.resource;

import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.util.MapUtil;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计数资源
 *
 * @author frankcl
 * @create 2019-06-01 12:24
 */
class CounterResource extends Resource<AtomicInteger> {

    private final static String KEY_INIT_VALUE = "initValue";

    public CounterResource(String name) {
        super(name);
    }

    @Override
    public AtomicInteger create(Map<String, Object> configMap) {
        Integer initValue = MapUtil.getValue(configMap, KEY_INIT_VALUE, Integer.class);
        return new AtomicInteger(initValue == null ? 0 : initValue);
    }

    @Override
    public void destroy() {
        object = null;
    }
}
