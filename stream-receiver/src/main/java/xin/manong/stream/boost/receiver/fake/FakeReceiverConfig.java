package xin.manong.stream.boost.receiver.fake;

/**
 * fake接收器配置
 *
 * @author frankcl
 * @date 2022-08-04 23:07:48
 */
public class FakeReceiverConfig {

    private final static int DEFAULT_THREAD_NUM = 1;
    private final static long DEFAULT_TIME_INTERVAL_MS = 1000L;

    public int threadNum = DEFAULT_THREAD_NUM;
    public long timeIntervalMs = DEFAULT_TIME_INTERVAL_MS;

    /**
     * 检测配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    boolean check() {
        if (threadNum <= 0) threadNum = DEFAULT_THREAD_NUM;
        if (timeIntervalMs < 0L) timeIntervalMs = DEFAULT_TIME_INTERVAL_MS;
        return true;
    }
}
