package xin.manong.stream.boost.receiver.fake;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;

/**
 * fake数据生产
 *
 * @author frankcl
 * @date 2023-01-05 14:43:44
 */
public class FakeRecordProducer implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(FakeRecordProducer.class);

    private volatile boolean running;
    @Setter
    private Long timeIntervalMs;
    private final String name;
    private Thread workThread;
    private final ReceiveProcessor receiveProcessor;

    public FakeRecordProducer(String name, ReceiveProcessor receiveProcessor) {
        this.running = false;
        this.name = name;
        this.receiveProcessor = receiveProcessor;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (running) {
            try {
                KVRecords kvRecords = new KVRecords();
                kvRecords.addRecord(new KVRecord());
                receiveProcessor.process(kvRecords);
                if (timeIntervalMs != null && timeIntervalMs > 0) Thread.sleep(timeIntervalMs);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(), e);
            } catch (Throwable e) {
                logger.error("Process fake record failed");
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 启动
     */
    public void start() {
        logger.info("Fake record producer:{} is starting ...", name);
        running = true;
        workThread = new Thread(this, name);
        workThread.start();
        logger.info("Fake record producer:{} has been started", name);
    }

    /**
     * 停止
     */
    public void stop() {
        logger.info("Fake record producer:{} is stopping ...", name);
        running = false;
        if (workThread.isAlive()) workThread.interrupt();
        try {
            workThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Fake record producer:{} has been stopped", name);
    }
}
