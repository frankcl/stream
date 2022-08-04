package com.manong.stream.sdk.common;

/**
 * 不可接受异常
 * 抛出该异常表述不可接受处理失败，框架捕获该异常后对数据进行重新处理
 *
 * @author frankcl
 * @date 2022-08-04 14:09:41
 */
public class UnacceptableException extends Exception {

    public UnacceptableException(String message) {
        super(message);
    }

    public UnacceptableException(Throwable cause) {
        super(cause);
    }
}
