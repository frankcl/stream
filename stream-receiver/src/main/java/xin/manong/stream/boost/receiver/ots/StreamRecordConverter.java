package xin.manong.stream.boost.receiver.ots;

import com.alicloud.openservices.tablestore.model.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.stream.sdk.receiver.ReceiveConverter;
import xin.manong.weapon.aliyun.ots.OTSConverter;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;

import java.util.Map;

/**
 * OTS通道流数据转换器
 *
 * @author frankcl
 * @date 2022-08-05 16:43:23
 */
public class StreamRecordConverter extends ReceiveConverter {

    private final static Logger logger = LoggerFactory.getLogger(StreamRecordConverter.class);

    public StreamRecordConverter(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public KVRecords convert(Context context, Object object) throws Exception {
        if (!(object instanceof StreamRecord)) {
            logger.error("convert record is null or not stream record");
            return null;
        }
        StreamRecord streamRecord = (StreamRecord) object;
        context.put(StreamConstants.STREAM_RECORD_TYPE, streamRecord.getRecordType().name());
        KVRecord kvRecord = OTSConverter.convertStreamRecord(streamRecord);
        if (kvRecord == null) {
            logger.error("convert stream record failed");
            return null;
        }
        KVRecords kvRecords = new KVRecords();
        kvRecords.addRecord(kvRecord);
        return kvRecords;
    }
}
