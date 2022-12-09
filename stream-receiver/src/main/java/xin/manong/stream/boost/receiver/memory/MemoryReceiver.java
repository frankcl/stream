package xin.manong.stream.boost.receiver.memory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.stream.sdk.receiver.Receiver;
import xin.manong.weapon.base.record.KVRecords;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 内存数据接收器
 * 接收流程插件处理的数据，分发下游处理
 * 主要用于数据重新处理场景
 *
 * @author frankcl
 * @date 2022-08-04 23:04:57
 */
public class MemoryReceiver extends Receiver {

    private final static Logger logger = LoggerFactory.getLogger(MemoryReceiver.class);

    private MemoryReceiveHandler[] handlers;
    @Resource(name = "${recordQueue}")
    protected BlockingQueue<KVRecords> recordQueue;

    public MemoryReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        logger.info("memory receiver is starting ...");
        MemoryReceiverConfig receiveConfig = JSON.toJavaObject(
                new JSONObject(configMap), MemoryReceiverConfig.class);
        if (!receiveConfig.check()) return false;
        String name = "memory-receive-handler";
        handlers = new MemoryReceiveHandler[receiveConfig.threadNum];
        for (int i = 0; i < receiveConfig.threadNum; i++) {
            handlers[i] = new MemoryReceiveHandler(String.format("%s-%d", name, i), recordQueue, receiveProcessor);
            handlers[i].start();
        }
        logger.info("memory receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("memory receiver is stopping ...");
        for (int i = 0; i < handlers.length; i++) handlers[i].stop();
        logger.info("memory receiver has been stopped");
    }
}
