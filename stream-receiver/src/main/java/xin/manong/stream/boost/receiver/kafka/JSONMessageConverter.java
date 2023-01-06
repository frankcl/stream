package xin.manong.stream.boost.receiver.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.stream.sdk.receiver.ReceiveConverter;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * kafka JSON消息装换器
 *
 * @author frankcl
 * @date 2023-01-06 11:17:04
 */
public class JSONMessageConverter extends ReceiveConverter {

    private final static Logger logger = LoggerFactory.getLogger(JSONMessageConverter.class);

    public JSONMessageConverter(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public KVRecords convert(Context context, Object object) throws Exception {
        if (object == null || !(object instanceof ConsumerRecord)) {
            logger.error("convert record is null or not kafka message");
            return null;
        }
        ConsumerRecord<byte[], byte[]> consumerRecord = (ConsumerRecord<byte[], byte[]>) object;
        String key = new String(consumerRecord.key(), Charset.forName("UTF-8"));
        context.put(StreamConstants.STREAM_MESSAGE_KEY, key);
        context.put(StreamConstants.STREAM_MESSAGE_TOPIC, consumerRecord.topic());
        context.put(StreamConstants.STREAM_MESSAGE_PARTITION, consumerRecord.partition());
        context.put(StreamConstants.STREAM_MESSAGE_OFFSET, consumerRecord.offset());
        context.put(StreamConstants.STREAM_MESSAGE_TIMESTAMP, consumerRecord.timestamp());
        String content = new String(consumerRecord.value(), Charset.forName("UTF-8"));
        JSONObject jsonMessage = JSON.parseObject(content);
        KVRecord kvRecord = new KVRecord();
        for (Map.Entry<String, Object> entry : jsonMessage.entrySet()) {
            kvRecord.put(entry.getKey(), entry.getValue());
        }
        kvRecord.put(StreamConstants.STREAM_MESSAGE_KEY, key);
        kvRecord.put(StreamConstants.STREAM_MESSAGE_TOPIC, consumerRecord.topic());
        kvRecord.put(StreamConstants.STREAM_MESSAGE_PARTITION, consumerRecord.partition());
        kvRecord.put(StreamConstants.STREAM_MESSAGE_OFFSET, consumerRecord.offset());
        kvRecord.put(StreamConstants.STREAM_MESSAGE_TIMESTAMP, consumerRecord.timestamp());
        KVRecords kvRecords = new KVRecords();
        kvRecords.addRecord(kvRecord);
        return kvRecords;
    }
}
