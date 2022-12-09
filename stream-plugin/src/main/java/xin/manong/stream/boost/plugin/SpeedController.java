package xin.manong.stream.boost.plugin;

import com.google.common.util.concurrent.RateLimiter;
import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.weapon.base.record.KVRecord;

import java.util.Map;

/**
 * 控制数据处理速度
 *
 * @author frankcl
 * @create 2019-07-30 15:08:57
 */
public class SpeedController extends Plugin {

    @Resource(name = "${rateLimiter}")
    protected RateLimiter rateLimiter = null;

    public SpeedController(Map<String, Object> configMap) {
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
