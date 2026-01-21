package xin.manong.stream.boost.receiver.ots;

import com.alicloud.openservices.tablestore.model.StreamRecord;
import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import com.alicloud.openservices.tablestore.tunnel.worker.ProcessRecordsInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;

import java.util.List;

/**
 * OTS通道数据处理器
 *
 * @author frankcl
 * @date 2022-11-02 11:31:26
 */
public class OTSChannelProcessor implements IChannelProcessor {

    private final static Logger logger = LoggerFactory.getLogger(OTSChannelProcessor.class);

    private final ReceiveProcessor receiveProcessor;

    public OTSChannelProcessor(ReceiveProcessor receiveProcessor) {
        this.receiveProcessor = receiveProcessor;
    }

    @Override
    public void process(ProcessRecordsInput input) {
        List<StreamRecord> records = input.getRecords();
        for (StreamRecord record : records) {
            try {
                receiveProcessor.process(record);
            } catch (Throwable e) {
                logger.error("Process stream record failed for trace:{} and token:{}",
                        input.getTraceId(), input.getNextToken());
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void shutdown() {
        logger.info("Channel processor has been shutdown");
    }
}
