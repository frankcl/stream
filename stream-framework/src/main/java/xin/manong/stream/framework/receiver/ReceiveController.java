package xin.manong.stream.framework.receiver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.stream.framework.resource.ResourceInjector;
import xin.manong.stream.sdk.receiver.ReceiveConverter;
import xin.manong.stream.sdk.receiver.ReceiveProcessor;
import xin.manong.stream.sdk.receiver.Receiver;
import xin.manong.weapon.alarm.AlarmSender;
import xin.manong.weapon.base.util.ReflectParams;
import xin.manong.weapon.base.util.ReflectUtil;

import java.util.List;
import java.util.Map;

/**
 * 数据接收控制器
 *
 * @author frankcl
 * @date 2022-08-01 13:04:20
 */
public class ReceiveController {

    private final static Logger logger = LoggerFactory.getLogger(ReceiveController.class);

    private String name;
    private ReceiveControllerConfig config;
    private Receiver receiver;
    private ReceiveConverter converter;
    private ReceiveProcessor receiveProcessor;
    private AlarmSender alarmSender;

    /**
     * 初始化
     *
     * @param config 配置
     * @param processorGraphConfig processor配置列表
     * @return 成功返回true，否则返回false
     */
    public final boolean init(ReceiveControllerConfig config, List<ProcessorConfig> processorGraphConfig) {
        logger.info("init receiver[{}] ...", name);
        if (config == null || !config.check()) {
            logger.error("receive controller config is invalid");
            return false;
        }
        this.config = config;
        this.name = config.name;
        if (!initReceiver() || !initConverter()) return false;
        receiveProcessor = new ReceiveProcessorImpl(name, config.processors, processorGraphConfig, converter);
        ((ReceiveProcessorImpl) receiveProcessor).setAlarmSender(alarmSender);
        ReflectUtil.setFieldValue(receiver, "receiveProcessor", receiveProcessor);
        logger.info("init receiver[{}] success", name);
        return true;
    }

    /**
     * 启动receiver
     *
     * @return 成功返回true，否则返回false
     */
    public final boolean start() {
        logger.info("receiver[{}] is starting ...", name);
        if (!receiver.start()) {
            logger.error("start receiver[{}] failed", name);
            return false;
        }
        logger.info("receiver[{}] has been started", name);
        return true;
    }

    /**
     * 销毁
     */
    public final void destroy() {
        logger.info("receiver[{}] is destroying ...", name);
        if (receiver != null) receiver.stop();
        if (converter != null) converter.destroy();
        receiveProcessor.sweep();
        logger.info("receiver[{}] has been destroyed", name);
    }

    /**
     * 初始化Receiver
     *
     * @return 成功返回true，否则返回false
     */
    private boolean initReceiver() {
        ReflectParams params = new ReflectParams();
        params.types = new Class[] { Map.class };
        params.values = new Object[] { config.receiverConfigMap };
        receiver = (Receiver) ReflectUtil.newInstance(config.receiverClass, params);
        ResourceInjector.inject(receiver, config.receiverConfigMap);
        return true;
    }

    /**
     * 初始化ReceiveConverter
     *
     * @return 成功返回true，否则返回false
     */
    private boolean initConverter() {
        if (StringUtils.isEmpty(config.converterClass)) return true;
        ReflectParams params = new ReflectParams();
        params.types = new Class[] { Map.class };
        params.values = new Object[] { config.converterConfigMap };
        converter = (ReceiveConverter) ReflectUtil.newInstance(config.converterClass, params);
        ResourceInjector.inject(converter, config.converterConfigMap);
        if (!converter.init()) {
            logger.error("init receive converter[{}] failed", name);
            return false;
        }
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
}
