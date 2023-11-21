package xin.manong.stream.test.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.Receiver;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;

import java.util.Map;

/**
 * Fake数据接收器
 *
 * @author frankcl
 * @date 2022-08-04 17:05:41
 */
public class FakeReceiver extends Receiver {

    private final static Logger logger = LoggerFactory.getLogger(FakeReceiver.class);

    private volatile boolean running = false;
    private Thread workThread;

    public FakeReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        running = true;
        workThread = new Thread(() -> {
            while (running) {
                KVRecords kvRecords = new KVRecords();
                KVRecord kvRecord = new KVRecord();
                kvRecords.addRecord(kvRecord);
                try {
                    receiveProcessor.process(kvRecords);
                    Thread.sleep(1000);
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        workThread.start();
        return true;
    }

    @Override
    public void stop() {
        running = false;
        workThread.interrupt();
        try {
            if (workThread.isAlive()) workThread.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
