package com.manong.stream.framework.receiver;

import com.manong.stream.sdk.receiver.Receiver;
import com.manong.weapon.base.common.Context;
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
public class DummyReceiver extends Receiver {

    private final static Logger logger = LoggerFactory.getLogger(DummyReceiver.class);

    private boolean running = false;
    private Thread dummyThread;

    public DummyReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        running = true;
        dummyThread = new Thread(() -> {
            while (running) {
                KVRecords kvRecords = new KVRecords();
                KVRecord kvRecord = new KVRecord();
                kvRecord.put("key", new Random().nextInt(1000));
                kvRecords.addRecord(kvRecord);
                try {
                    logger.info("produce record[{}]", kvRecord);
                    receiveProcessor.process(new Context(), kvRecords);
                    Thread.sleep(1000);
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        dummyThread.start();
        return true;
    }

    @Override
    public void stop() {
        running = false;
        dummyThread.interrupt();
        try {
            dummyThread.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
