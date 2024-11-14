package xin.manong.stream.boost.receiver.mns;

import com.aliyun.mns.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;
import xin.manong.weapon.aliyun.mns.MessageProcessor;

/**
 * MNS消息处理器
 *
 * @author frankcl
 * @date 2024-01-12 14:45:49
 */
class MNSProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MNSProcessor.class);

    private final ReceiveProcessor receiveProcessor;

    public MNSProcessor(ReceiveProcessor receiveProcessor) {
        this.receiveProcessor = receiveProcessor;
    }

    @Override
    public boolean process(Message message) {
        try {
            receiveProcessor.process(message);
            return true;
        } catch (Throwable e) {
            logger.error("process message[{}] failed", message.getMessageId());
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
