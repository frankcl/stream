package xin.manong.stream.boost.receiver.ons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.Receiver;
import xin.manong.weapon.aliyun.ons.ONSConsumer;
import xin.manong.weapon.aliyun.ons.ONSConsumerConfig;
import xin.manong.weapon.aliyun.ons.Subscribe;
import xin.manong.weapon.base.rebuild.RebuildListener;
import xin.manong.weapon.base.rebuild.Rebuildable;

import java.util.List;
import java.util.Map;

/**
 * ONS消息接收器
 *
 * @author frankcl
 * @date 2022-08-03 19:09:57
 */
public class ONSReceiver extends Receiver implements RebuildListener {

    private final static Logger logger = LoggerFactory.getLogger(ONSReceiver.class);

    private ONSProcessor processor;
    private ONSConsumer consumer;

    public ONSReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    /**
     * 填充消息监听器
     *
     * @param consumerConfig 消费配置
     */
    private void fillMessageListeners(ONSConsumerConfig consumerConfig) {
        if (consumerConfig.subscribes == null || consumerConfig.subscribes.isEmpty()) return;
        for (Subscribe subscribe : consumerConfig.subscribes) subscribe.listener = processor;
    }

    @Override
    public boolean start() {
        logger.info("ONS receiver is starting ...");
        ONSConsumerConfig consumerConfig = JSON.toJavaObject(new JSONObject(configMap), ONSConsumerConfig.class);
        if (consumerConfig == null) {
            logger.error("parse ONS consumer config failed");
            return false;
        }
        if (receiveProcessor == null) {
            logger.error("receive processor is null");
            return false;
        }
        processor = new ONSProcessor(receiveProcessor);
        fillMessageListeners(consumerConfig);
        if (!consumerConfig.check()) return false;
        consumer = new ONSConsumer(consumerConfig);
        if (!consumer.start()) return false;
        consumer.addRebuildListener(this);
        logger.info("ONS receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("ONS receiver is stopping ...");
        if (consumer != null) consumer.stop();
        logger.info("ONS receiver has been stopped");
    }

    @Override
    public void notifyRebuildEvent(Rebuildable rebuildObject) {
        if (rebuildObject == null || rebuildObject != consumer) return;
        if (receiveProcessor == null) return;
        receiveProcessor.sweep();
    }
}
