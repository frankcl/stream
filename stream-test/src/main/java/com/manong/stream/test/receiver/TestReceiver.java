package com.manong.stream.test.receiver;

import com.manong.stream.sdk.receiver.Receiver;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

/**
 * @author frankcl
 * @date 2022-08-04 17:05:41
 */
public class TestReceiver extends Receiver {

    private final static Logger logger = LoggerFactory.getLogger(TestReceiver.class);

    private boolean running = false;
    private Thread workThread;

    public TestReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        running = true;
        workThread = new Thread(() -> {
            while (running) {
                KVRecords kvRecords = new KVRecords();
                KVRecord kvRecord = new KVRecord();
                int key = new Random().nextInt(1000);
                kvRecord.put("key", key);
                kvRecord.put("fork", key % 2 == 0 ? "success" : "fail");
                kvRecords.addRecord(kvRecord);
                try {
                    logger.info("produce record[{}]", kvRecord);
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
