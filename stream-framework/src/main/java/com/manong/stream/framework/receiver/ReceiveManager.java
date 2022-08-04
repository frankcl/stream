package com.manong.stream.framework.receiver;

import com.manong.stream.framework.processor.ProcessorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 数据接收器管理
 *
 * @author frankcl
 * @date 2022-08-03 14:22:51
 */
public class ReceiveManager {

    private final static Logger logger = LoggerFactory.getLogger(ReceiveManager.class);

    private List<ReceiveControllerConfig> configList;
    private List<ProcessorConfig> processorGraphConfig;
    private List<ReceiveController> receiveControllers;

    public ReceiveManager(List<ReceiveControllerConfig> configList, List<ProcessorConfig> processorGraphConfig) {
        this.configList = configList;
        this.processorGraphConfig = processorGraphConfig;
        this.receiveControllers = new ArrayList<>();
    }

    /**
     * 初始化
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
        logger.info("receive manager init ...");
        if (configList == null || configList.isEmpty()) {
            logger.error("receiver config list is empty");
            return false;
        }
        Set<String> receiverNames = new HashSet<>();
        for (ReceiveControllerConfig config : configList) {
            ReceiveController receiveController = new ReceiveController();
            if (!receiveController.init(config, processorGraphConfig)) {
                logger.error("init receiver[{}] failed", config.name);
                return false;
            }
            if (receiverNames.contains(config.name)) {
                logger.error("the same receiver[{}] exists", config.name);
                return false;
            }
            receiverNames.add(config.name);
            receiveControllers.add(receiveController);
        }
        logger.info("init receive manager success");
        return true;
    }

    /**
     * 销毁
     */
    public void destroy() {
        logger.info("receive manager is destroying ...");
        for (ReceiveController receiveController : receiveControllers) {
            receiveController.destroy();
        }
        logger.info("receive manager has been destroyed");
    }

    /**
     * 启动
     *
     * @return 成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("receive manager is starting ...");
        for (ReceiveController receiveController : receiveControllers) {
            if (!receiveController.start()) return false;
        }
        logger.info("receive manager has been started");
        return true;
    }
}
