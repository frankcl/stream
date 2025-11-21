package xin.manong.stream.boost.receiver.rocketmq;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;

import java.util.List;

/**
 * RocketMQ消息处理器
 *
 * @author frankcl
 * @date 2025-11-01 19:45:29
 */
class RocketMQProcessor implements MessageListenerConcurrently {

    private final static Logger logger = LoggerFactory.getLogger(RocketMQProcessor.class);

    private final ReceiveProcessor receiveProcessor;

    public RocketMQProcessor(ReceiveProcessor receiveProcessor) {
        this.receiveProcessor = receiveProcessor;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages,
                                                    ConsumeConcurrentlyContext consumeContext) {
        for (MessageExt message : messages) {
            try {
                receiveProcessor.process(message);
            } catch (Throwable e) {
                if (message.getReconsumeTimes() > 3) continue;
                logger.error("Process message:{} failed", message.getMsgId());
                logger.error(e.getMessage(), e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
