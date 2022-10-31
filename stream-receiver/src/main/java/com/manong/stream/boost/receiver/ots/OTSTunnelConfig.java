package com.manong.stream.boost.receiver.ots;

import com.manong.weapon.aliyun.secret.AliyunSecret;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OTS通道配置
 *
 * @author frankcl
 * @date 2022-08-04 23:08:14
 */
public class OTSTunnelConfig {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelConfig.class);

    private final static int DEFAULT_CONSUME_THREAD_NUM = 5;
    private final static int DEFAULT_HEARTBEAT_INTERVAL_SEC = 30;
    private final static int DEFAULT_MAX_CONSUME_DELAY_MS = 60000;
    private final static int DEFAULT_MAX_RETRY_INTERVAL_MS = 200;
    private final static int DEFAULT_MAX_CHANNEL_PARALLEL = -1;
    private final static int MAX_CONSUME_THREAD_NUM = 32;

    public int consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
    public int heartBeatIntervalSec = DEFAULT_HEARTBEAT_INTERVAL_SEC;
    public int maxRetryIntervalMs = DEFAULT_MAX_RETRY_INTERVAL_MS;
    public int maxChannelParallel = DEFAULT_MAX_CHANNEL_PARALLEL;
    public long maxConsumeDelayMs = DEFAULT_MAX_CONSUME_DELAY_MS;
    public String endpoint;
    public String instance;
    public String table;
    public String tunnel;
    public AliyunSecret aliyunSecret;

    /**
     * 检测OTS通道配置有效性
     *
     * @return 有效返回true，否则返回false
     */
    public boolean check() {
        if (aliyunSecret == null || !aliyunSecret.check()) return false;
        if (StringUtils.isEmpty(table)) {
            logger.error("oss table name is empty");
            return false;
        }
        if (StringUtils.isEmpty(tunnel)) {
            logger.error("oss tunnel name is empty");
            return false;
        }
        if (StringUtils.isEmpty(instance)) {
            logger.error("oss instance is empty");
            return false;
        }
        if (StringUtils.isEmpty(endpoint)) {
            logger.error("oss endpoint is empty");
            return false;
        }
        if (consumeThreadNum <= 0) consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
        if (heartBeatIntervalSec <= 0) heartBeatIntervalSec = DEFAULT_HEARTBEAT_INTERVAL_SEC;
        if (maxRetryIntervalMs <= 0) maxRetryIntervalMs = DEFAULT_MAX_RETRY_INTERVAL_MS;
        if (maxConsumeDelayMs <= 0) maxConsumeDelayMs = DEFAULT_MAX_CONSUME_DELAY_MS;
        if (consumeThreadNum > MAX_CONSUME_THREAD_NUM) consumeThreadNum = MAX_CONSUME_THREAD_NUM;
        return true;
    }
}
