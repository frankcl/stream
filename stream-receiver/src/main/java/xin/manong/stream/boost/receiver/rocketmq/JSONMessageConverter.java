package xin.manong.stream.boost.receiver.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.stream.sdk.receiver.ReceiveConverter;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * JSON格式RocketMQ消息转换器
 *
 * @author frankcl
 * @date 2025-11-01 15:39:18
 */
public class JSONMessageConverter extends ReceiveConverter {

    private final static Logger logger = LoggerFactory.getLogger(JSONMessageConverter.class);

    public JSONMessageConverter(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public KVRecords convert(Context context, Object object) throws Exception {
        if (!(object instanceof MessageExt message)) {
            logger.error("Convert record is null or not RocketMQ message");
            return null;
        }
        context.put(StreamConstants.STREAM_MESSAGE_ID, message.getMsgId());
        context.put(StreamConstants.STREAM_MESSAGE_TOPIC, message.getTopic());
        context.put(StreamConstants.STREAM_MESSAGE_TIMESTAMP, message.getBornTimestamp());
        if (StringUtils.isNotEmpty(message.getKeys())) context.put(StreamConstants.STREAM_MESSAGE_KEY, message.getKeys());
        if (StringUtils.isNotEmpty(message.getTags())) context.put(StreamConstants.STREAM_MESSAGE_TAG, message.getTags());
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        JSONObject jsonMessage = JSON.parseObject(content);
        KVRecord kvRecord = new KVRecord();
        for (Map.Entry<String, Object> entry : jsonMessage.entrySet()) {
            kvRecord.put(entry.getKey(), entry.getValue());
        }
        kvRecord.put(StreamConstants.STREAM_MESSAGE_ID, message.getMsgId());
        kvRecord.put(StreamConstants.STREAM_MESSAGE_TOPIC, message.getTopic());
        kvRecord.put(StreamConstants.STREAM_MESSAGE_TIMESTAMP, message.getBornTimestamp());
        if (!StringUtils.isEmpty(message.getKeys())) kvRecord.put(StreamConstants.STREAM_MESSAGE_KEY, message.getKeys());
        if (!StringUtils.isEmpty(message.getTags())) kvRecord.put(StreamConstants.STREAM_MESSAGE_TAG, message.getTags());
        KVRecords kvRecords = new KVRecords();
        kvRecords.addRecord(kvRecord);
        return kvRecords;
    }
}
