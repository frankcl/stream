package xin.manong.stream.test.plugin;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.stream.test.common.Constants;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.util.MapUtil;

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

    private static final String KEY_NAME = "name";

    private String name;

    public DummyHandler(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean init() {
        name = MapUtil.getValue(configMap, KEY_NAME, String.class);
        if (StringUtils.isEmpty(name)) {
            logger.error("parameter[{}] is not found", KEY_NAME);
            return false;
        }
        return true;
    }

    @Override
    public ProcessResult handle(KVRecord kvRecord) throws Exception {
        ProcessResult processResult = new ProcessResult();
        Long id = kvRecord.has(Constants.ID) ? (Long) kvRecord.get(Constants.ID) : null;
        logger.info("record[{}] is processed in plugin[{}]", id, name);
        return processResult;
    }
}
