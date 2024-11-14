package xin.manong.stream.boost.receiver.mns;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.stream.sdk.receiver.Receiver;
import xin.manong.weapon.aliyun.mns.MNSClient;
import xin.manong.weapon.aliyun.mns.MNSQueueConsumer;
import xin.manong.weapon.aliyun.mns.MNSQueueConsumerConfig;
import xin.manong.weapon.base.rebuild.RebuildListener;
import xin.manong.weapon.base.rebuild.Rebuildable;

import java.util.Map;

/**
 * MNS消息接收器
 *
 * @author frankcl
 * @date 2022-08-03 19:09:57
 */
public class MNSReceiver extends Receiver implements RebuildListener {

    private final static Logger logger = LoggerFactory.getLogger(MNSReceiver.class);

    private MNSQueueConsumer consumer;
    @Resource(name = "mnsClient")
    protected MNSClient mnsClient;

    public MNSReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    /**
     * 填充消息监听器
     *
     * @param consumerConfig 消费配置
     */
    private void fillMessageListeners(MNSQueueConsumerConfig consumerConfig) {

    }

    @Override
    public boolean start() {
        logger.info("MNS receiver is starting ...");
        MNSQueueConsumerConfig consumerConfig = JSON.toJavaObject(new JSONObject(configMap), MNSQueueConsumerConfig.class);
        if (consumerConfig == null) {
            logger.error("parse MNS queue consumer config failed");
            return false;
        }
        if (receiveProcessor == null) {
            logger.error("receive processor is null");
            return false;
        }
        MNSProcessor processor = new MNSProcessor(receiveProcessor);
        consumer.setProcessor(processor);
        consumer.setMnsClient(mnsClient);
        if (!consumerConfig.check()) return false;
        consumer = new MNSQueueConsumer(consumerConfig);
        if (!consumer.start()) return false;
        consumer.addRebuildListener(this);
        logger.info("MNS receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("MNS receiver is stopping ...");
        if (consumer != null) consumer.stop();
        logger.info("MNS receiver has been stopped");
    }

    @Override
    public void onRebuild(Rebuildable rebuildTarget) {
        if (rebuildTarget == null || rebuildTarget != consumer) return;
        if (receiveProcessor == null) return;
        receiveProcessor.sweep();
    }
}
