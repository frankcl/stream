package xin.manong.stream.boost.receiver.ons;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;

/**
 * ONS消息处理器
 *
 * @author frankcl
 * @date 2022-11-01 19:45:29
 */
class ONSProcessor implements MessageListener {

    private final static Logger logger = LoggerFactory.getLogger(ONSProcessor.class);

    private final ReceiveProcessor receiveProcessor;

    public ONSProcessor(ReceiveProcessor receiveProcessor) {
        this.receiveProcessor = receiveProcessor;
    }

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            receiveProcessor.process(message);
            return Action.CommitMessage;
        } catch (Throwable e) {
            logger.error("process message[{}] failed", message.getMsgID());
            logger.error(e.getMessage(), e);
            return Action.ReconsumeLater;
        }
    }
}
