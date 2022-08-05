package com.manong.stream.boost.receiver.ots;

import com.alicloud.openservices.tablestore.model.StreamRecord;
import com.manong.stream.sdk.receiver.ReceiveConverter;
import com.manong.weapon.aliyun.ots.OTSConverter;
import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * OTS通道流数据转换器
 *
 * @author frankcl
 * @date 2022-08-05 16:43:23
 */
public class StreamRecordConverter extends ReceiveConverter {

    private final static Logger logger = LoggerFactory.getLogger(StreamRecordConverter.class);

    public final static String TUNNEL_RECORD_TYPE = "__TUNNEL_RECORD_TYPE__";

    public StreamRecordConverter(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public KVRecords convert(Context context, Object object) throws Exception {
        StreamRecord streamRecord = (StreamRecord) object;
        if (streamRecord == null) {
            logger.error("convert record is null or not stream record");
            return null;
        }
        context.put(TUNNEL_RECORD_TYPE, streamRecord.getRecordType().name());
        KVRecord kvRecord = OTSConverter.convertStreamRecord(streamRecord);
        if (kvRecord == null) {
            logger.error("convert stream record failed");
            return null;
        }
        kvRecord.put(TUNNEL_RECORD_TYPE, streamRecord.getRecordType().name());
        KVRecords kvRecords = new KVRecords();
        kvRecords.addRecord(kvRecord);
        return kvRecords;
    }
}
