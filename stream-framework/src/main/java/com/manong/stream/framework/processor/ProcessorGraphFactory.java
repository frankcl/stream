package com.manong.stream.framework.processor;

import com.manong.stream.sdk.common.UnacceptableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * processor拓扑图工厂
 *
 * @author frankcl
 * @date 2022-07-28 23:31:29
 */
public class ProcessorGraphFactory {

    private final static Logger logger = LoggerFactory.getLogger(ProcessorGraphFactory.class);

    private static Queue<ProcessorGraph> processorGraphs = new ConcurrentLinkedQueue<>();

    private ProcessorGraphFactory() {
    }

    /**
     * 构建processor拓扑图
     *
     * @param graphConfig processor图配置
     * @throws UnacceptableException 创建失败抛出异常
     * @return 成功返回processorGraph，否则返回null
     */
    public static ProcessorGraph make(List<ProcessorConfig> graphConfig) throws UnacceptableException {
        if (graphConfig == null || graphConfig.isEmpty()) {
            logger.error("processor graph config is empty");
            throw new UnacceptableException("processor graph config is empty");
        }
        ProcessorGraph processorGraph = new ProcessorGraph(graphConfig);
        processorGraph.buildGraph();
        processorGraphs.add(processorGraph);
        return processorGraph;
    }

    /**
     * 清理工厂：清除所有processorGraph
     */
    public static void sweep() {
        while (!processorGraphs.isEmpty()) {
            ProcessorGraph processorGraph = processorGraphs.poll();
            if (processorGraph == null) continue;
            processorGraph.closeGraph();
        }
        processorGraphs = new ConcurrentLinkedQueue<>();
    }
}
