package com.manong.stream.test.common;

import com.manong.stream.sdk.annotation.Resource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author frankcl
 * @date 2022-07-28 18:17:47
 */
public class TestObject {

    @Resource(name = "counter1", required = false)
    public AtomicInteger counter1;

    @Resource(name = "${counter2}")
    public AtomicInteger counter2;
}
