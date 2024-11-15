package xin.manong.stream.framework.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.common.StreamManager;
import xin.manong.stream.framework.resource.ResourceInjector;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.stream.sdk.common.UnacceptableException;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.base.util.ReflectArgs;
import xin.manong.weapon.base.util.ReflectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据处理器
 * 封装用户定义插件，stream框架内部数据结构
 *
 * @author frankcl
 * @date 2019-05-27 15:23:00
 */
public class Processor {

    private final static Logger logger = LoggerFactory.getLogger(Processor.class);

    /* 处理数据数量 */
    private long processCount;
    /* processor名称 */
    protected String name;
    /* 插件实例 */
    protected Plugin plugin;
    /* 后继processor */
    protected Map<String, Processor> processors;

    public Processor() {
        processCount = 0;
        processors = new HashMap<>();
    }

    /**
     * 初始化processor
     *
     * @param config processor配置
     * @return 成功返回true，否则返回false
     */
    public final boolean init(ProcessorConfig config) {
        logger.info("init processor[{}] ...", config.name);
        if (!config.check()) return false;
        name = config.name;
        ReflectArgs args = new ReflectArgs();
        args.types = new Class[] { Map.class };
        args.values = new Object[] { config.pluginConfig };
        try {
            plugin = (Plugin) ReflectUtil.newInstance(config.className, args);
            ResourceInjector.inject(plugin, config.pluginConfig);
            if (!plugin.init()) {
                logger.error("init plugin[{}] failed", name);
                return false;
            }
            logger.info("init processor[{}] success", name);
            return true;
        } catch (Exception e) {
            logger.error("init processor[{}] failed", name);
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 销毁processor
     */
    public final void destroy() {
        logger.info("processor[{}] is destroying ...", name);
        if (plugin != null)  {
            plugin.flush();
            plugin.destroy();
        }
        logger.info("process record count[{}] for processor[{}]", processCount, name);
        logger.info("processor[{}] has been destroyed", name);
    }

    /**
     * 调用用户plugin处理数据，并将处理结果分发到后续processor处理
     *
     * @param kvRecords 待处理数据
     * @param context 上下文
     * @throws UnacceptableException 不可接受异常
     */
    public final void process(KVRecords kvRecords, Context context) throws Exception {
        ProcessResult processResult = new ProcessResult();
        context.put(StreamConstants.STREAM_PROCESSOR, name);
        for (int i = 0; i < kvRecords.getRecordCount(); i++) {
            long startProcessTime = System.currentTimeMillis();
            KVRecord kvRecord = kvRecords.getRecord(i);
            try {
                StreamManager.keepWatchRecord(kvRecord, context);
                submitProcessTrace(kvRecord);
                ProcessResult result = plugin.handle(kvRecord);
                if (result != null) processResult.addResult(result);
            } catch (Exception e) {
                kvRecord.put(StreamConstants.STREAM_EXCEPTION_PROCESSOR, name);
                throw e;
            } finally {
                long processTime = System.currentTimeMillis() - startProcessTime;
                submitProcessTime(kvRecord, processTime);
            }
            if (++processCount % 1000 == 0) {
                plugin.flush();
                logger.info("process record count[{}] for processor[{}]", processCount, name);
            }
        }
        for (String fork : processResult.getForks()) {
            if (processors.containsKey(fork)) processors.get(fork).process(processResult.getRecords(fork), context);
            else logger.debug("fork processor[{}] is not found for processor[{}]", fork, name);
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!(object instanceof Processor)) return false;
        return name.equals(((Processor) object).name);
    }

    /**
     * 设置分支processor
     *
     * @param fork 分支名
     * @param processor processor
     */
    public final void setProcessor(String fork, Processor processor) {
        if (StringUtils.isEmpty(fork) || processor == null) return;
        processors.put(fork, processor);
    }

    /**
     * 提交处理轨迹
     *
     * @param kvRecord 数据
     */
    private void submitProcessTrace(KVRecord kvRecord) {
        if (!kvRecord.has(StreamConstants.STREAM_PROCESS_TRACE)) {
            kvRecord.put(StreamConstants.STREAM_PROCESS_TRACE, new JSONArray());
        }
        JSONArray processTrace = (JSONArray) kvRecord.get(StreamConstants.STREAM_PROCESS_TRACE);
        processTrace.add(name);
    }

    /**
     * 提交处理时间
     *
     * @param kvRecord 数据
     * @param processTime 处理时间
     */
    private void submitProcessTime(KVRecord kvRecord, long processTime) {
        if (!kvRecord.has(StreamConstants.STREAM_PROCESSOR_TIME)) {
            kvRecord.put(StreamConstants.STREAM_PROCESSOR_TIME, new JSONObject());
        }
        JSONObject processTimeMap = (JSONObject) kvRecord.get(StreamConstants.STREAM_PROCESSOR_TIME);
        processTimeMap.put(name, processTime);
    }
}
