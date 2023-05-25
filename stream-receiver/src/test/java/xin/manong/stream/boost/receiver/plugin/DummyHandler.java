package xin.manong.stream.boost.receiver.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.weapon.base.record.KVRecord;

import java.util.Map;

/**
 * dummy plugin
 * 简单打印数据
 *
 * @author frankcl
 * @date 2023-05-25 11:57:36
 */
public class DummyHandler extends Plugin {

    private static final Logger logger = LoggerFactory.getLogger(DummyHandler.class);

    public DummyHandler(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public ProcessResult handle(KVRecord kvRecord) throws Exception {
        ProcessResult processResult = new ProcessResult();
        logger.info("record[{}] is processed in plugin", kvRecord.toString());
        return processResult;
    }
}
