package com.manong.stream.framework.plugin;

import com.manong.stream.sdk.annotation.Resource;
import com.manong.stream.sdk.plugin.Plugin;
import com.manong.stream.sdk.common.ProcessResult;
import com.manong.weapon.base.record.KVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author frankcl
 * @date 2022-07-31 09:50:14
 */
public class DummyPlugin extends Plugin {

    private final static Logger logger = LoggerFactory.getLogger(DummyPlugin.class);

    private final static String KEY_FORK = "fork";

    @Resource(name = "${counter}", required = false)
    private AtomicInteger counter;
    public DummyPlugin(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public ProcessResult handle(KVRecord kvRecord) throws Exception {
        String fork = kvRecord.has(KEY_FORK) ? (String) kvRecord.get(KEY_FORK) : FORK_FAIL;
        logger.info("handle record[{}]", kvRecord);
        ProcessResult processResult = new ProcessResult();
        processResult.addRecord(fork, kvRecord);
        return processResult;
    }
}
