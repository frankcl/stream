package xin.manong.stream.framework.receiver;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.weapon.alarm.AlarmProducer;

import java.util.*;

/**
 * 数据接收器管理
 *
 * @author frankcl
 * @date 2022-08-03 14:22:51
 */
public class ReceiveManager {

    private final static Logger logger = LoggerFactory.getLogger(ReceiveManager.class);

    @Setter
    private String appName;
    private final List<ReceiveControllerConfig> configList;
    private final List<ProcessorConfig> processorGraphConfig;
    private final List<ReceiveController> receiveControllers;
    @Setter
    private AlarmProducer alarmProducer;

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
        logger.info("Receive manager init ...");
        if (configList == null || configList.isEmpty()) {
            logger.error("Receiver config list are empty");
            return false;
        }
        Set<String> receiverNames = new HashSet<>();
        for (ReceiveControllerConfig config : configList) {
            if (receiverNames.contains(config.name)) {
                logger.error("The same name receiver:{} exists", config.name);
                return false;
            }
            ReceiveController receiveController = new ReceiveController();
            receiveController.setAppName(appName);
            receiveController.setAlarmProducer(alarmProducer);
            if (!receiveController.init(config, processorGraphConfig)) {
                logger.error("Init receiver:{} failed", config.name);
                return false;
            }
            receiverNames.add(config.name);
            receiveControllers.add(receiveController);
        }
        logger.info("Init receive manager success");
        return true;
    }

    /**
     * 销毁
     */
    public void destroy() {
        logger.info("Receive manager is destroying ...");
        for (ReceiveController receiveController : receiveControllers) {
            receiveController.destroy();
        }
        logger.info("Receive manager has been destroyed");
    }

    /**
     * 启动
     *
     * @return 成功返回true，否则返回false
     */
    public boolean start() {
        logger.info("Receive manager is starting ...");
        for (ReceiveController receiveController : receiveControllers) {
            if (!receiveController.start()) return false;
        }
        logger.info("Receive manager has been started");
        return true;
    }
}
