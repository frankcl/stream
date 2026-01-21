package xin.manong.stream.boost.receiver.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;
import xin.manong.weapon.base.kafka.KafkaRecordProcessor;

import java.nio.charset.StandardCharsets;

/**
 * kafka消息处理器
 *
 * @author frankcl
 * @date 2023-01-06 11:08:59
 */
public class KafkaProcessor implements KafkaRecordProcessor {

    private final static Logger logger = LoggerFactory.getLogger(KafkaProcessor.class);

    private final ReceiveProcessor receiveProcessor;

    public KafkaProcessor(ReceiveProcessor receiveProcessor) {
        this.receiveProcessor = receiveProcessor;
    }

    @Override
    public void process(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        try {
            receiveProcessor.process(consumerRecord);
        } catch (Throwable e) {
            logger.error("Process message:{} failed", new String(consumerRecord.key(), StandardCharsets.UTF_8));
            logger.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }
}
