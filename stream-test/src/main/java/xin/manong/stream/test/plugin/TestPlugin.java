package xin.manong.stream.test.plugin;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.weapon.base.record.KVRecord;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author frankcl
 * @date 2022-07-31 09:50:14
 */
public class TestPlugin extends Plugin {

    private final static Logger logger = LoggerFactory.getLogger(TestPlugin.class);

    private final static String KEY_FORK = "fork";

    @Resource(name = "${counter}", required = false)
    private AtomicInteger counter = null;
    public TestPlugin(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public ProcessResult handle(KVRecord kvRecord) throws Exception {
        String fork = kvRecord.has(KEY_FORK) ? (String) kvRecord.get(KEY_FORK) : null;
        if (counter != null) kvRecord.put("counter", counter.getAndIncrement());
        logger.info("handle record[{}]", kvRecord);
        ProcessResult processResult = new ProcessResult();
        if (!StringUtils.isEmpty(fork)) processResult.addRecord(fork, kvRecord);
        return processResult;
    }
}
