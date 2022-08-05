package com.manong.stream.boost.receiver.memory;

/**
 * 内存接收器配置
 *
 * @author frankcl
 * @date 2022-08-04 23:07:48
 */
public class MemoryReceiverConfig {

    private final static int DEFAULT_THREAD_NUM = 1;

    public int threadNum = DEFAULT_THREAD_NUM;

    /**
     * 检测配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    boolean check() {
        if (threadNum <= 0) threadNum = DEFAULT_THREAD_NUM;
        return true;
    }
}
