package com.manong.stream.boost.receiver.ons;

import com.manong.weapon.aliyun.secret.KeySecret;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ONS消息消费者配置
 *
 * @author frankcl
 * @create 2019-05-29 18:52
 */
public class ONSConsumerConfig {

    private final static Logger logger = LoggerFactory.getLogger(ONSConsumerConfig.class);

    private final static int DEFAULT_CONSUME_THREAD_NUM = 1;
    private final static int DEFAULT_MAX_CACHED_MESSAGE_NUM = 1000;
    private final static String DEFAULT_TAGS = "*";

    public int consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
    public int maxCachedMessageNum = DEFAULT_MAX_CACHED_MESSAGE_NUM;
    public String consumeId;
    public String serverURL;
    public String topic;
    public String tags = DEFAULT_TAGS;
    public KeySecret keySecret;

    /**
     * 检测配置合法性
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(serverURL)) {
            logger.error("server url is empty");
            return false;
        }
        if (StringUtils.isEmpty(topic)) {
            logger.error("consume topic is empty");
            return false;
        }
        if (StringUtils.isEmpty(consumeId)) {
            logger.error("consume id is empty");
            return false;
        }
        if (keySecret == null || !keySecret.check()) {
            logger.error("key secret is invalid");
            return false;
        }
        if (consumeThreadNum <= 0) consumeThreadNum = DEFAULT_CONSUME_THREAD_NUM;
        if (maxCachedMessageNum <= 0) maxCachedMessageNum = DEFAULT_MAX_CACHED_MESSAGE_NUM;
        if (StringUtils.isEmpty(tags)) tags = DEFAULT_TAGS;
        return true;
    }
}
