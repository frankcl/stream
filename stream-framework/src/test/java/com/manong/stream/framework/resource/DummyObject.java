package com.manong.stream.framework.resource;

import com.manong.stream.sdk.annotation.Resource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author frankcl
 * @date 2022-07-28 18:17:47
 */
public class DummyObject {

    @Resource(name = "dummy_resource1", required = false)
    public AtomicInteger counter1;

    @Resource(name = "${dummy_resource}")
    public AtomicInteger counter2;
}
