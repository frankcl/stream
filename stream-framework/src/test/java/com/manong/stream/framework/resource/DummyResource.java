package com.manong.stream.framework.resource;

import com.manong.stream.sdk.resource.Resource;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dummy资源
 *
 * @author frankcl
 * @create 2019-06-01 12:24
 */
class DummyResource extends Resource<AtomicInteger> {

    public DummyResource(String name) {
        super(name);
    }

    @Override
    public AtomicInteger create(Map<String, Object> configMap) {
        return new AtomicInteger(0);
    }

    @Override
    public void destroy() {
    }
}
