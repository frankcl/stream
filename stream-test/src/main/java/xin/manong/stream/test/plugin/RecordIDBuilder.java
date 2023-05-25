package xin.manong.stream.test.plugin;

import xin.manong.stream.sdk.annotation.Resource;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.stream.test.common.Constants;
import xin.manong.stream.test.resource.AutoIncreasedIDBuilder;
import xin.manong.weapon.base.record.KVRecord;

import java.util.Map;

/**
 * 数据ID生成
 *
 * @author frankcl
 * @date 2023-05-25 11:56:35
 */
public class RecordIDBuilder extends Plugin {

    @Resource(name = "${idBuilder}")
    private AutoIncreasedIDBuilder builder;

    public RecordIDBuilder(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public ProcessResult handle(KVRecord kvRecord) throws Exception {
        ProcessResult processResult = new ProcessResult();
        Long id = builder.getNewID();
        kvRecord.put(Constants.ID, id);
        processResult.addRecord(id % 2 == 0 ? FORK_SUCCESS : FORK_FAIL, kvRecord);
        return processResult;
    }
}
