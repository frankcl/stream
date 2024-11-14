package xin.manong.stream.test.resource;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 自增ID生成器
 *
 * @author frankcl
 * @date 2023-05-25 11:51:57
 */
public class AutoIncreasedIDBuilder {

    private final AtomicLong counter = new AtomicLong(0L);

    /**
     * 获取新ID
     *
     * @return 新ID
     */
    public Long getNewID() {
        return counter.getAndIncrement();
    }

}
