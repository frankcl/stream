package xin.manong.stream.framework.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.common.StreamManager;
import xin.manong.stream.framework.processor.ProcessorGraph;
import xin.manong.stream.framework.processor.ProcessorGraphFactory;
import xin.manong.stream.framework.receiver.ReceiveControllerConfig;
import xin.manong.stream.framework.receiver.ReceiveManager;
import xin.manong.stream.framework.resource.ResourceConfig;
import xin.manong.stream.framework.resource.ResourceManager;
import xin.manong.stream.sdk.common.UnacceptableException;
import xin.manong.weapon.alarm.Alarm;
import xin.manong.weapon.alarm.AlarmConfig;
import xin.manong.weapon.alarm.AlarmSender;
import xin.manong.weapon.alarm.AlarmStatus;
import xin.manong.weapon.base.secret.DynamicSecretListener;
import xin.manong.weapon.base.util.FileUtil;
import xin.manong.weapon.base.util.ReflectParams;
import xin.manong.weapon.base.util.ReflectUtil;

import java.nio.charset.Charset;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;

/**
 * 数据流运行器
 *
 * @author frankcl
 * @date 2022-08-03 14:36:39
 */
public class StreamRunner {

    private final static Logger logger = LoggerFactory.getLogger(StreamRunner.class);

    private StreamRunnerConfig config;
    private ReceiveManager receiveManager;
    private AlarmSender alarmSender;

    public StreamRunner(StreamRunnerConfig config) {
        if (config == null || !config.check()) throw new RuntimeException("check stream config failed");
        this.config = config;
    }

    /**
     * 启动调度器
     *
     * @return 成功返回true，否则返回false
     */
    public boolean start() throws Exception {
        logger.info("stream[{}] is starting ...", config.name);
        ServiceLoader<DynamicSecretListener> serviceLoader = ServiceLoader.load(DynamicSecretListener.class);
        for (DynamicSecretListener listener : serviceLoader) listener.start();
        if (!startAlarmSender()) return false;
        StreamManager.buildStreamLogger(config.loggerFile, config.loggerKeys);
        if (config.resources != null) {
            for (ResourceConfig resourceConfig : config.resources) {
                ResourceManager.registerResource(resourceConfig);
            }
        }
        if (!checkProcessorGraph()) return false;
        receiveManager = new ReceiveManager(config.receivers, config.processors);
        receiveManager.setAlarmSender(alarmSender);
        if (!receiveManager.init()) return false;
        if (!receiveManager.start()) return false;
        if (alarmSender != null) {
            alarmSender.send(new Alarm(String.format("stream app[%s] has been started",
                    config.name), AlarmStatus.INFO));
        }
        logger.info("stream[{}] has been started", config.name);
        return true;
    }

    /**
     * 停止调度器
     */
    public void stop() {
        logger.info("stream[{}] is stopping ...", config.name);
        if (receiveManager != null) receiveManager.destroy();
        ProcessorGraphFactory.sweep();
        ResourceManager.unregisterAllResources();
        if (alarmSender != null) {
            alarmSender.send(new Alarm(String.format("stream app[%s] has been stopped",
                    config.name), AlarmStatus.INFO));
            alarmSender.stop();
        }
        logger.info("stream[{}] has been stopped", config.name);
    }

    /**
     * 启动报警发送器
     *
     * @return 启动成功返回true，否则返回false
     */
    private boolean startAlarmSender() {
        if (config.alarmConfig == null) {
            logger.info("alarm config is null, ignore start alarm sender request");
            return true;
        }
        if (!config.alarmConfig.check()) {
            logger.error("invalid alarm config");
            return false;
        }
        try {
            ReflectParams reflectParams = new ReflectParams(
                    new Class[]{ AlarmConfig.class }, new Object[]{ config.alarmConfig });
            alarmSender = (AlarmSender) ReflectUtil.newInstance(config.alarmConfig.alarmSenderClass, reflectParams);
            logger.info("start alarm sender[{}] success", config.alarmConfig.alarmSenderClass);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("start alarm sender[{}] failed", config.alarmConfig.alarmSenderClass);
            return false;
        }
    }

    /**
     * 检测插件图有效性
     * 尝试构建插件图，检测图是否存在环，接收器与插件连通性
     *
     * @return 通过返回true，否则返回false;
     * @throws UnacceptableException 不可接受异常
     */
    private boolean checkProcessorGraph() throws UnacceptableException {
        ProcessorGraph processorGraph = ProcessorGraphFactory.make(config.processors);
        for (ReceiveControllerConfig receiveControllerConfig : config.receivers) {
            if (!receiveControllerConfig.check()) return false;
            for (String processor : receiveControllerConfig.processors) {
                if (processorGraph.containsProcessor(processor)) continue;
                logger.error("processor[{}] is not found for receiver[{}]", processor,
                        receiveControllerConfig.name);
                return false;
            }
        }
        ProcessorGraphFactory.sweep();
        return true;
    }

    /**
     * 解析命令行参数，获取配置文件路径
     *
     * @param args 命令行参数
     * @return 配置文件路径
     * @throws ParseException
     */
    private static String parseCommands(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(Option.builder("h").longOpt("help").
                desc("help information for stream runner").build());
        options.addOption(Option.builder("c").hasArg().required().desc("stream config file path").build());
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine command = parser.parse(options, args);
        if (command.hasOption("h")) {
            formatter.printHelp(StreamRunner.class.getName(), options);
            System.exit(0);
        }
        return command.getOptionValue("c");
    }

    /**
     * 数据流启动入口
     *
     * @param args 命令行参数
     * @throws Exception
     */
    public static void run(String[] args) throws Exception {
        JSON.DEFAULT_PARSER_FEATURE &= ~Feature.UseBigDecimal.getMask();
        String configFile = parseCommands(args);
        String content = FileUtil.read(configFile, Charset.forName("UTF-8"));
        StreamRunnerConfig config = JSON.parseObject(content, StreamRunnerConfig.class);
        StreamRunner streamRunner = new StreamRunner(config);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streamRunner.stop();
            countDownLatch.countDown();
        }));
        if (!streamRunner.start()) {
            logger.error("start stream[{}] failed", config.name);
            System.exit(1);
        }
        logger.info("stream[{}] is working ...", config.name);
        countDownLatch.await();
        logger.info("stream[{}] finished working", config.name);
    }
}