package com.manong.stream.framework.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manong.stream.sdk.common.StreamConstants;
import com.manong.stream.framework.common.StreamManager;
import com.manong.stream.framework.resource.ResourceInjector;
import com.manong.stream.sdk.common.UnacceptableException;
import com.manong.stream.sdk.plugin.Plugin;
import com.manong.stream.sdk.common.ProcessResult;
import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import com.manong.weapon.base.util.ReflectParams;
import com.manong.weapon.base.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据处理器
 * 封装用户定义插件，stream框架内部数据结构
 *
 * @author frankcl
 * @create 2019-05-27 15:23:00
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
        if (config == null || !config.check()) return false;
        name = config.name;
        ReflectParams params = new ReflectParams();
        params.types = new Class[] { Map.class };
        params.values = new Object[] { config.pluginConfig };
        try {
            plugin = (Plugin) ReflectUtil.newInstance(config.className, params);
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
     *
     * @return 成功返回true，否则返回false
     */
    public final void destroy() {
        logger.info("processor[{}] is destroying ...", name);
        if (plugin != null)  {
            plugin.flush();
            plugin.destroy();
        }
        logger.info("processor[{}] has been destroyed", name);
    }

    /**
     * 调用用户plugin处理数据，并将处理结果分发到后续processor处理
     *
     * @param kvRecords 待处理数据
     * @param context 上下文
     * @throws UnacceptableException 不可接受异常
     */
    public final void process(KVRecords kvRecords, Context context) throws UnacceptableException {
        ProcessResult processResult = new ProcessResult();
        context.put(StreamConstants.STREAM_PROCESSOR, name);
        for (int i = 0; i < kvRecords.getRecordCount(); i++) {
            long startProcessTime = System.currentTimeMillis();
            KVRecord kvRecord = kvRecords.getRecord(i);
            try {
                StreamManager.keepWatchRecord(kvRecord, context);
                commitProcessTrace(kvRecord);
                ProcessResult result = plugin.handle(kvRecord);
                if (result != null) processResult.addResult(result);
            } catch (UnacceptableException e) {
                kvRecord.put(StreamConstants.STREAM_EXCEPTION_PROCESSOR, name);
                logger.error("unacceptable exception occurred for processor[{}]", name);
                throw e;
            } catch (Exception e) {
                kvRecord.put(StreamConstants.STREAM_EXCEPTION_PROCESSOR, name);
                logger.warn("process record exception for processor[{}]", name);
            } finally {
                long processTime = System.currentTimeMillis() - startProcessTime;
                commitProcessTime(kvRecord, processTime);
            }
            if (++processCount % 1000 == 0) {
                plugin.flush();
                logger.info("process record count[{}]", processCount);
            }
        }
        for (String fork : processResult.getForks()) {
            if (processors.containsKey(fork)) processors.get(fork).process(processResult.getRecords(fork), context);
            else logger.warn("fork processor[{}] is not found for processor[{}]", fork, name);
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
    private void commitProcessTrace(KVRecord kvRecord) {
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
    private void commitProcessTime(KVRecord kvRecord, long processTime) {
        if (!kvRecord.has(StreamConstants.STREAM_PROCESSOR_TIME)) {
            kvRecord.put(StreamConstants.STREAM_PROCESSOR_TIME, new JSONObject());
        }
        JSONObject processTimeMap = (JSONObject) kvRecord.get(StreamConstants.STREAM_PROCESSOR_TIME);
        processTimeMap.put(name, processTime);
    }
}
