package com.manong.stream.boost.receiver.ons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.manong.stream.sdk.receiver.ReceiveConverter;
import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * JSON格式MQ消息转换器
 *
 * @author frankcl
 * @date 2022-08-04 15:39:18
 */
public class JSONMessageConverter extends ReceiveConverter {

    private final static Logger logger = LoggerFactory.getLogger(JSONMessageConverter.class);

    private final static String DEFAULT_MESSAGE_ID = "message_id";
    private final static String DEFAULT_MESSAGE_KEY = "message_key";

    private final static String KEY_MESSAGE_ID = "messageId";
    private final static String KEY_MESSAGE_KEY = "messageKey";

    private String messageId = DEFAULT_MESSAGE_ID;
    private String messageKey = DEFAULT_MESSAGE_KEY;

    public JSONMessageConverter(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean init() {
        messageId = configMap.containsKey(KEY_MESSAGE_ID) ?
                (String) configMap.get(KEY_MESSAGE_ID) : DEFAULT_MESSAGE_ID;
        messageKey = configMap.containsKey(KEY_MESSAGE_KEY) ?
                (String) configMap.get(KEY_MESSAGE_KEY) : DEFAULT_MESSAGE_KEY;
        return true;
    }

    @Override
    public KVRecords convert(Context context, Object object) throws Exception {
        if (object == null || !(object instanceof Message)) {
            logger.error("convert record is null or not ONS message");
            return null;
        }
        Message message = (Message) object;
        context.put(messageId, message.getMsgID());
        if (!StringUtils.isEmpty(message.getKey())) context.put(messageKey, message.getKey());
        String content = new String(message.getBody(), Charset.forName("UTF-8"));
        JSONObject jsonMessage = JSON.parseObject(content);
        KVRecord kvRecord = new KVRecord();
        for (Map.Entry<String, Object> entry : jsonMessage.entrySet()) {
            kvRecord.put(entry.getKey(), entry.getValue());
        }
        kvRecord.put(messageId, message.getMsgID());
        if (!StringUtils.isEmpty(message.getKey())) kvRecord.put(messageKey, message.getKey());
        KVRecords kvRecords = new KVRecords();
        kvRecords.addRecord(kvRecord);
        return kvRecords;
    }
}
