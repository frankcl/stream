package xin.manong.stream.framework.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.weapon.alarm.AlarmSender;

import java.util.*;

/**
 * 数据接收器管理
 *
 * @author frankcl
 * @date 2022-08-03 14:22:51
 */
public class ReceiveManager {

    private final static Logger logger = LoggerFactory.getLogger(ReceiveManager.class);

    private String appName;
    private List<ReceiveControllerConfig> configList;
    private List<ProcessorConfig> processorGraphConfig;
    private List<ReceiveController> receiveControllers;
    private AlarmSender alarmSender;

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
            if (receiverNames.contains(config.name)) {
                logger.error("the same receiver[{}] exists", config.name);
                return false;
            }
            ReceiveController receiveController = new ReceiveController();
            receiveController.setAppName(appName);
            receiveController.setAlarmSender(alarmSender);
            if (!receiveController.init(config, processorGraphConfig)) {
                logger.error("init receiver[{}] failed", config.name);
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

    /**
     * 设置报警发送器
     *
     * @param alarmSender 报警发送器
     */
    public void setAlarmSender(AlarmSender alarmSender) {
        this.alarmSender = alarmSender;
    }

    /**
     * 设置所属应用名
     *
     * @param appName 所属应用名
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }
}
