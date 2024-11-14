package xin.manong.stream.boost.receiver.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.common.StreamManager;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 内存接收数据处理器
 *
 * @author frankcl
 * @date 2022-08-04 23:06:43
 */
public class MemoryReceiveHandler implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(MemoryReceiveHandler.class);

    private volatile boolean running;
    private final String name;
    private Thread workThread;
    private final BlockingQueue<KVRecords> recordQueue;
    private final ReceiveProcessor receiveProcessor;

    public MemoryReceiveHandler(String name, BlockingQueue<KVRecords> recordQueue,
                                ReceiveProcessor receiveProcessor) {
        this.running = false;
        this.name = name;
        this.recordQueue = recordQueue;
        this.receiveProcessor = receiveProcessor;
    }

    @Override
    public void run() {
        while (running) {
            try {
                KVRecords kvRecords = recordQueue.poll(1, TimeUnit.SECONDS);
                if (kvRecords == null) continue;
                for (int i = 0; i < kvRecords.getRecordCount(); i++) {
                    KVRecord kvRecord = kvRecords.getRecord(i);
                    StreamManager.removeStreamHistory(kvRecord);
                }
                receiveProcessor.process(kvRecords);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(), e);
            } catch (Throwable e) {
                logger.error("process memory record failed");
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 启动
     */
    public void start() {
        logger.info("memory receive handler[{}] is starting ...", name);
        running = true;
        workThread = new Thread(this, name);
        workThread.start();
        logger.info("memory receive handler[{}] has been started", name);
    }

    /**
     * 停止
     */
    public void stop() {
        logger.info("memory receive handler[{}] is stopping ...", name);
        running = false;
        if (workThread.isAlive()) workThread.interrupt();
        try {
            workThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("memory receive handler[{}] has been stopped", name);
    }
}
