package xin.manong.stream.boost.receiver.ons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import org.apache.commons.lang3.StringUtils;
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
 * JSON格式MQ消息转换器
 *
 * @author frankcl
 * @date 2022-08-04 15:39:18
 */
public class JSONMessageConverter extends ReceiveConverter {

    private final static Logger logger = LoggerFactory.getLogger(JSONMessageConverter.class);

    public JSONMessageConverter(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public KVRecords convert(Context context, Object object) throws Exception {
        if (!(object instanceof Message)) {
            logger.error("convert record is null or not ONS message");
            return null;
        }
        Message message = (Message) object;
        context.put(StreamConstants.STREAM_MESSAGE_ID, message.getMsgID());
        context.put(StreamConstants.STREAM_MESSAGE_TOPIC, message.getTopic());
        context.put(StreamConstants.STREAM_MESSAGE_TIMESTAMP, message.getBornTimestamp());
        if (!StringUtils.isEmpty(message.getKey())) context.put(StreamConstants.STREAM_MESSAGE_KEY, message.getKey());
        if (!StringUtils.isEmpty(message.getTag())) context.put(StreamConstants.STREAM_MESSAGE_TAG, message.getTag());
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        JSONObject jsonMessage = JSON.parseObject(content);
        KVRecord kvRecord = new KVRecord();
        for (Map.Entry<String, Object> entry : jsonMessage.entrySet()) {
            kvRecord.put(entry.getKey(), entry.getValue());
        }
        kvRecord.put(StreamConstants.STREAM_MESSAGE_ID, message.getMsgID());
        kvRecord.put(StreamConstants.STREAM_MESSAGE_TOPIC, message.getTopic());
        kvRecord.put(StreamConstants.STREAM_MESSAGE_TIMESTAMP, message.getBornTimestamp());
        if (!StringUtils.isEmpty(message.getKey())) kvRecord.put(StreamConstants.STREAM_MESSAGE_KEY, message.getKey());
        if (!StringUtils.isEmpty(message.getTag())) kvRecord.put(StreamConstants.STREAM_MESSAGE_TAG, message.getTag());
        KVRecords kvRecords = new KVRecords();
        kvRecords.addRecord(kvRecord);
        return kvRecords;
    }
}
