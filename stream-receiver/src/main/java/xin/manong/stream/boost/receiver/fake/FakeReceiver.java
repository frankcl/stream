package xin.manong.stream.boost.receiver.fake;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.Receiver;

import java.util.Map;

/**
 * @author frankcl
 * @date 2023-01-05 14:36:50
 */
public class FakeReceiver extends Receiver {

    private final static Logger logger = LoggerFactory.getLogger(FakeReceiver.class);

    private FakeRecordProducer[] producers;

    public FakeReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        logger.info("Fake receiver is starting ...");
        FakeReceiverConfig receiveConfig = JSON.toJavaObject(
                new JSONObject(configMap), FakeReceiverConfig.class);
        if (!receiveConfig.check()) return false;
        String name = "fake-receive-handler";
        producers = new FakeRecordProducer[receiveConfig.threadNum];
        for (int i = 0; i < receiveConfig.threadNum; i++) {
            producers[i] = new FakeRecordProducer(String.format("%s-%d", name, i), receiveProcessor);
            producers[i].setTimeIntervalMs(receiveConfig.timeIntervalMs);
            producers[i].start();
        }
        logger.info("Fake receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("Fake receiver is stopping ...");
        for (FakeRecordProducer producer : producers) producer.stop();
        logger.info("Fake receiver has been stopped");
    }
}
