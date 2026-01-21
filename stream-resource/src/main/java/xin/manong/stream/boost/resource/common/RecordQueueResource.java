package xin.manong.stream.boost.resource.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.base.util.MapUtil;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 数据队列资源
 *
 * @author frankcl
 * @date 2019-10-26 22:07:08
 */
public class RecordQueueResource extends Resource<BlockingQueue<KVRecords>> {

    private final static Logger logger = LoggerFactory.getLogger(RecordQueueResource.class);

    private final static String KEY_QUEUE_SIZE = "queueSize";

    public RecordQueueResource(String name) {
        super(name);
    }

    @Override
    public BlockingQueue<KVRecords> create(Map<String, Object> configMap) {
        Integer queueSize = MapUtil.getValue(configMap, KEY_QUEUE_SIZE, Integer.class);
        if (queueSize == null || queueSize <= 0) {
            logger.error("Invalid queue size[{}]", queueSize);
            return null;
        }
        logger.info("Create record queue success");
        return new ArrayBlockingQueue<>(queueSize);
    }

    @Override
    public void destroy() {
        if (object != null) object.clear();
        logger.info("Clear record queue success");
        object = null;
    }
}
