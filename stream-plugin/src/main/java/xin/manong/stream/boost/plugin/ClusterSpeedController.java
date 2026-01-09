package xin.manong.stream.boost.plugin;

import org.redisson.api.RRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.util.MapUtil;

import java.util.Map;

/**
 * 集群级别数据处理速度控制器
 *
 * @author frankcl
 * @date 2023-07-19 16:00:23
 */
public class ClusterSpeedController extends Plugin {

    private static final Logger logger = LoggerFactory.getLogger(ClusterSpeedController.class);

    private static final String KEY_BLOCK = "block";

    private Boolean block = true;
    @Resource(name = "${rateLimiter}")
    protected RRateLimiter rateLimiter = null;

    public ClusterSpeedController(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean init() {
        block = MapUtil.getValue(configMap, KEY_BLOCK, Boolean.class);
        if (block == null) {
            block = true;
            logger.warn("Parameter {} is not config, use default value as true", KEY_BLOCK);
        }
        return true;
    }

    @Override
    public ProcessResult handle(KVRecord kvRecord) throws Exception {
        ProcessResult processResult = new ProcessResult();
        if (block != null && block) rateLimiter.acquire();
        else if (!rateLimiter.tryAcquire()) return processResult;
        processResult.addRecord(FORK_NEXT, kvRecord);
        return processResult;
    }
}
