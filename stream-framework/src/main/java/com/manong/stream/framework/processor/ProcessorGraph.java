package com.manong.stream.framework.processor;

import com.manong.stream.sdk.common.UnacceptableException;
import com.manong.weapon.base.record.KVRecords;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private List<ProcessorConfig> processorGraphConfig;
    private Map<String, Processor> processors;

    ProcessorGraph(List<ProcessorConfig> processorGraphConfig) {
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
     * @param processorName 处理processor名称
     * @param kvRecords 数据
     * @throws UnacceptableException 不可接受异常
     */
    public final void process(String processorName, KVRecords kvRecords) throws UnacceptableException {
        if (StringUtils.isEmpty(processorName)) {
            logger.warn("processor name is empty");
            throw new UnacceptableException("processor name is empty");
        }
        Processor processor = processors.getOrDefault(processorName, null);
        if (processor == null) {
            logger.warn("processor is not found for name[{}]", processorName);
            throw new UnacceptableException(String.format("processor is not found for name[%s]", processorName));
        }
        processor.process(kvRecords);
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
     * @return 合法返回true，否则返回false
     */
    private void checkGraph() throws UnacceptableException {
        if (processorGraphConfig == null || processorGraphConfig.isEmpty()) {
            logger.error("processor graph config is empty");
            throw new UnacceptableException("processor graph config is empty");
        }
        Map<String, ProcessorConfig> processorConfigMap = processorGraphConfig.stream().collect(
                Collectors.toMap(processorConfig -> processorConfig.name, processorConfig -> processorConfig));
        if (processorConfigMap.size() != processorGraphConfig.size()) {
            logger.error("the same processor exists");
            throw new UnacceptableException("the same processor exists");
        }
        Set<String> processorNames = new HashSet<>();
        for (String processorName : processorConfigMap.keySet()) {
            processorNames.addAll(checkGraph(processorName, processorConfigMap));
            if (processorNames.size() == processorGraphConfig.size()) break;
        }
    }

    /**
     * 检测图的合法性，如果检测不通过抛出异常
     * 图是否完整
     * 图是否存在环
     *
     * @param processorName 检测入口processor名称
     * @param processorConfigMap processor配置
     * @return 合法返回true，否则返回false
     */
    private Set<String> checkGraph(String processorName, Map<String, ProcessorConfig> processorConfigMap)
            throws UnacceptableException {
        Set<String> processorNames = new HashSet<>();
        List<String> processorQueue = new LinkedList<>();
        processorNames.add(processorName);
        processorQueue.add(processorName);
        while (!processorQueue.isEmpty()) {
            ProcessorConfig processorConfig = processorConfigMap.get(processorQueue.remove(0));
            for (String name : processorConfig.processors.values()) {
                if (!processorConfigMap.containsKey(name)) {
                    logger.error("processor[{}] is not found for building graph", name);
                    throw new UnacceptableException(String.format(
                            "processor[%s] is not found for building graph", name));
                }
                if (processorNames.contains(name)) {
                    logger.error("check graph failed, find cycle in graph for processor[{}]", name);
                    throw new UnacceptableException(String.format(
                            "check graph failed, find cycle in graph for processor[%s]", name));
                }
                processorQueue.add(name);
            }
        }
        return processorNames;
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
