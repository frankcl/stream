package xin.manong.stream.boost.receiver.mns;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.model.Message;
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
 * JSON格式MNS消息转换器
 *
 * @author frankcl
 * @date 2024-01-12 15:39:18
 */
public class JSONMessageConverter extends ReceiveConverter {

    private final static Logger logger = LoggerFactory.getLogger(JSONMessageConverter.class);

    public JSONMessageConverter(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public KVRecords convert(Context context, Object object) throws Exception {
        if (!(object instanceof Message)) {
            logger.error("convert record is null or not MNS message");
            return null;
        }
        Message message = (Message) object;
        context.put(StreamConstants.STREAM_MESSAGE_ID, message.getMessageId());
        context.put(StreamConstants.STREAM_MESSAGE_TIMESTAMP, message.getEnqueueTime().getTime());
        String content = new String(message.getMessageBodyAsBytes(), StandardCharsets.UTF_8);
        JSONObject jsonMessage = JSON.parseObject(content);
        KVRecord kvRecord = new KVRecord();
        for (Map.Entry<String, Object> entry : jsonMessage.entrySet()) {
            kvRecord.put(entry.getKey(), entry.getValue());
        }
        kvRecord.put(StreamConstants.STREAM_MESSAGE_ID, message.getMessageId());
        kvRecord.put(StreamConstants.STREAM_MESSAGE_TIMESTAMP, message.getEnqueueTime().getTime());
        KVRecords kvRecords = new KVRecords();
        kvRecords.addRecord(kvRecord);
        return kvRecords;
    }
}
