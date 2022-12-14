package xin.manong.stream.sdk.receiver;

/**
 * 接收数据处理器
 * 处理接收器数据，用户不用关心此类，框架提供默认实现
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
     * @param object 接受数据
     * @throws Throwable 不可接受异常
     */
    public abstract void process(Object object) throws Throwable;

    /**
     * 清理资源
     */
    public abstract void sweep();
}
