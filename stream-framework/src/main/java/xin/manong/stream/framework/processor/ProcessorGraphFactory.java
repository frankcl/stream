package xin.manong.stream.framework.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.UnacceptableException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * processor拓扑图工厂
 *
 * @author frankcl
 * @date 2022-07-28 23:31:29
 */
public class ProcessorGraphFactory {

    private final static Logger logger = LoggerFactory.getLogger(ProcessorGraphFactory.class);

    private static Map<String, ProcessorGraph> processorGraphMap = new ConcurrentHashMap<>();

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
        processorGraphMap.put(processorGraph.getId(), processorGraph);
        return processorGraph;
    }

    /**
     * 清理指定ID插件图
     *
     * @param processorGraphId 清理插件图ID
     */
    public static void clean(String processorGraphId) {
        if (!processorGraphMap.containsKey(processorGraphId)) {
            logger.warn("processor graph[{}] is not found", processorGraphId);
            return;
        }
        ProcessorGraph processorGraph = processorGraphMap.remove(processorGraphId);
        if (processorGraph != null) processorGraph.closeGraph();
        logger.info("clean processor graph[{}] success", processorGraphId);
    }

    /**
     * 清理工厂：清除所有processorGraph
     */
    public static void sweep() {
        Iterator<ProcessorGraph> iterator = processorGraphMap.values().iterator();
        while (iterator.hasNext()) {
            ProcessorGraph processorGraph = iterator.next();
            iterator.remove();
            if (processorGraph == null) continue;
            processorGraph.closeGraph();
        }
        processorGraphMap = new ConcurrentHashMap<>();
    }
}
