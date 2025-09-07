package xin.manong.stream.framework.processor;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.UnacceptableException;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecords;

import java.util.*;
import java.util.stream.Collectors;

/**
 * processor拓扑图
 *
 * @author frankcl
 * @date 2022-07-28 23:27:29
 */
public class ProcessorGraph {

    private final static Logger logger = LoggerFactory.getLogger(ProcessorGraph.class);

    @Getter
    private final String id;
    private final List<ProcessorConfig> processorGraphConfig;
    private Map<String, Processor> processors;

    public ProcessorGraph(List<ProcessorConfig> processorGraphConfig) {
        this.id = UUID.randomUUID().toString();
        this.processorGraphConfig = processorGraphConfig;
    }

    /**
     * 构建processor图，构建失败抛出异常
     *
     * @throws UnacceptableException 不可接受异常
     */
    public final void buildGraph() throws UnacceptableException {
        checkGraph();
        processors = new HashMap<>();
        for (ProcessorConfig processorConfig : processorGraphConfig) {
            Processor processor = new Processor();
            if (!processor.init(processorConfig)) {
                throw new UnacceptableException(String.format(
                        "init processor[%s] failed", processorConfig.name));
            }
            processors.put(processor.name, processor);
        }
        for (ProcessorConfig processorConfig : processorGraphConfig) {
            Processor processor = processors.get(processorConfig.name);
            for (Map.Entry<String, String> entry : processorConfig.processors.entrySet()) {
                String name = entry.getValue();
                if (!processors.containsKey(name)) {
                    logger.error("processor[{}] is not found for building graph", name);
                    throw new UnacceptableException(String.format(
                            "processor[%s] is not found for building graph", name));
                }
                processor.setProcessor(entry.getKey(), processors.get(name));
            }
        }
        logger.info("build processor graph success");
    }

    /**
     * 关闭processor
     * 根据拓扑图结构依次关闭processor
     */
    public final void closeGraph() {
        if (processors == null) return;
        List<Processor> closeProcessors = findStartProcessors();
        while (!closeProcessors.isEmpty()) {
            Processor closeProcessor = closeProcessors.remove(0);
            for (Processor processor : closeProcessor.processors.values()) {
                if (closeProcessors.contains(processor)) continue;
                closeProcessors.add(processor);
            }
            closeProcessor.destroy();
        }
        logger.info("close processor graph success");
    }

    /**
     * 指定processor处理数据
     *
     * @param name 处理processor名称
     * @param kvRecords 数据
     * @param context 上下文
     * @throws UnacceptableException 不可接受异常
     */
    public final void process(String name, KVRecords kvRecords, Context context) throws Exception {
        if (StringUtils.isEmpty(name)) {
            logger.warn("processor name is empty");
            throw new UnacceptableException("processor name is empty");
        }
        Processor processor = processors.getOrDefault(name, null);
        if (processor == null) {
            logger.warn("processor is not found for name[{}]", name);
            throw new UnacceptableException(String.format("processor is not found for name[%s]", name));
        }
        processor.process(kvRecords, context);
    }

    /**
     * 是否包含processor
     *
     * @param processorName processor名称
     * @return 包含返回true，否则返回false
     */
    public final boolean containsProcessor(String processorName) {
        return processors != null && processors.containsKey(processorName);
    }

    /**
     * 检测图的合法性，不合法抛出异常
     * 1. 图是否完整
     * 2. 图是否存在环
     *
     * @throws UnacceptableException 不可接受异常
     */
    private void checkGraph() throws UnacceptableException {
        if (processorGraphConfig == null || processorGraphConfig.isEmpty()) {
            logger.error("processor graph config is empty");
            throw new UnacceptableException("processor graph config is empty");
        }
        Map<String, ProcessorConfig> processorConfigMap = processorGraphConfig.stream().collect(
                Collectors.toMap(processorConfig -> processorConfig.name, processorConfig -> processorConfig));
        if (processorConfigMap.size() != processorGraphConfig.size()) {
            logger.error("duplicated processor exists");
            throw new UnacceptableException("duplicated processor exists");
        }
        for (String processorName : processorConfigMap.keySet()) {
            checkGraph(processorName, new HashSet<>(), processorConfigMap);
        }
    }

    /**
     * 检测图的合法性，如果检测不通过抛出异常
     * 1. 图是否完整
     * 2. 图是否存在环
     *
     * @param processorName 检测入口processor名称
     * @param visitedProcessors 访问过processor名称
     * @param processorConfigMap processor配置
     * @throws UnacceptableException 检测失败抛出异常
     */
    private void checkGraph(String processorName, Set<String> visitedProcessors,
                            Map<String, ProcessorConfig> processorConfigMap)
            throws UnacceptableException {
        if (visitedProcessors.contains(processorName)) {
            logger.error("check graph failed, find cycle in graph for processor[{}]", processorName);
            throw new UnacceptableException(String.format(
                    "check graph failed, find cycle in graph for processor[%s]", processorName));
        }
        visitedProcessors.add(processorName);
        ProcessorConfig processorConfig = processorConfigMap.get(processorName);
        for (String name : processorConfig.processors.values()) {
            if (!processorConfigMap.containsKey(name)) {
                logger.error("processor[{}] is not found for checking graph", name);
                throw new UnacceptableException(String.format(
                        "processor[%s] is not found for building graph", name));
            }
            checkGraph(name, visitedProcessors, processorConfigMap);
        }
        visitedProcessors.remove(processorName);
    }

    /**
     * 找到起点processor集合
     *
     * @return 起点processor集合
     */
    private List<Processor> findStartProcessors() {
        Map<String, Processor> processorMap = new HashMap<>(processors);
        for (Processor processor : processors.values()) {
            for (Processor forkProcessor : processor.processors.values()) {
                if (!processorMap.containsKey(forkProcessor.name)) continue;
                processorMap.remove(forkProcessor.name);
            }
        }
        return new LinkedList<>(processorMap.values());
    }
}
