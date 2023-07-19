package xin.manong.stream.boost.plugin;

import org.redisson.api.RRateLimiter;
import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.weapon.base.record.KVRecord;

import java.util.Map;

/**
 * 集群级别数据处理速度控制器
 *
 * @author frankcl
 * @date 2023-07-19 16:00:23
 */
public class ClusterSpeedController extends Plugin {

    @Resource(name = "${rateLimiter}")
    protected RRateLimiter rateLimiter = null;

    public ClusterSpeedController(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public ProcessResult handle(KVRecord kvRecord) throws Exception {
        ProcessResult processResult = new ProcessResult();
        processResult.addRecord(FORK_NEXT, kvRecord);
        rateLimiter.acquire();
        return processResult;
    }
}
