package xin.manong.stream.framework.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.framework.common.StreamManager;
import xin.manong.stream.framework.prepare.PreprocessManager;
import xin.manong.stream.framework.prepare.PreprocessParser;
import xin.manong.stream.framework.processor.ProcessorConfig;
import xin.manong.stream.framework.processor.ProcessorGraph;
import xin.manong.stream.framework.processor.ProcessorGraphFactory;
import xin.manong.stream.framework.receiver.ReceiveControllerConfig;
import xin.manong.stream.framework.receiver.ReceiveManager;
import xin.manong.stream.framework.resource.ResourceConfig;
import xin.manong.stream.framework.resource.ResourceManager;
import xin.manong.stream.sdk.annotation.StreamApplication;
import xin.manong.stream.sdk.common.UnacceptableException;
import xin.manong.weapon.alarm.Alarm;
import xin.manong.weapon.alarm.AlarmConfig;
import xin.manong.weapon.alarm.AlarmLevel;
import xin.manong.weapon.alarm.AlarmProducer;
import xin.manong.weapon.aliyun.secret.ListenerScanner;
import xin.manong.weapon.base.util.FileUtil;
import xin.manong.weapon.base.util.ReflectArgs;
import xin.manong.weapon.base.util.ReflectUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * 数据流运行器
 *
 * @author frankcl
 * @date 2022-08-03 14:36:39
 */
public class StreamRunner {

    private final static Logger logger = LoggerFactory.getLogger(StreamRunner.class);

    private final static String CLASS_PATH_PREFIX = "classpath:";

    private final StreamRunnerConfig config;
    private ReceiveManager receiveManager;
    private AlarmProducer alarmProducer;

    public StreamRunner(StreamRunnerConfig config) {
        if (config == null || !config.check()) throw new IllegalArgumentException("check stream config failed");
        this.config = config;
    }

    /**
     * 启动调度器
     *
     * @return 成功返回true，否则返回false
     */
    public boolean start() throws Exception {
        logger.info("stream[{}] is starting ...", config.name);
        PreprocessManager.preprocess();
        ListenerScanner.scanRegister();
        if (!startAlarmProducer()) return false;
        StreamManager.buildStreamLogger(config.loggerFile, config.loggerKeys);
        if (config.resources != null) {
            for (ResourceConfig resourceConfig : config.resources) {
                ResourceManager.registerResource(resourceConfig);
            }
        }
        for (ProcessorConfig processorConfig : config.processors) {
            processorConfig.pythonEnv = config.pythonEnv;
        }
        if (!checkProcessorGraph()) return false;
        receiveManager = new ReceiveManager(config.receivers, config.processors);
        receiveManager.setAppName(config.name);
        receiveManager.setAlarmProducer(alarmProducer);
        if (!receiveManager.init()) return false;
        if (!receiveManager.start()) return false;
        if (alarmProducer != null) {
            alarmProducer.send(new Alarm(String.format("stream app[%s] has been started",
                    config.name), AlarmLevel.INFO).setAppName(config.name).setTitle("stream应用启动通知"));
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
        if (alarmProducer != null) {
            alarmProducer.send(new Alarm(String.format("stream app[%s] has been stopped",
                    config.name), AlarmLevel.INFO).setAppName(config.name).setTitle("stream应用停止通知"));
            alarmProducer.stop();
        }
        ListenerScanner.unregister();
        PreprocessManager.destroy();
        logger.info("stream[{}] has been stopped", config.name);
    }

    /**
     * 启动报警发送器
     *
     * @return 启动成功返回true，否则返回false
     */
    private boolean startAlarmProducer() {
        if (config.alarmConfig == null) {
            logger.info("alarm config is null");
            return true;
        }
        if (!config.alarmConfig.check()) {
            logger.error("invalid alarm config");
            return false;
        }
        try {
            ReflectArgs args = new ReflectArgs(
                    new Class[]{ AlarmConfig.class }, new Object[]{ config.alarmConfig });
            alarmProducer = (AlarmProducer) ReflectUtil.newInstance(config.alarmConfig.producerClass, args);
            if (!alarmProducer.start()) {
                logger.error("start alarm producer[{}] failed", config.alarmConfig.producerClass);
                return false;
            }
            logger.info("start alarm producer[{}] success", config.alarmConfig.producerClass);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("start alarm producer[{}] error", config.alarmConfig.producerClass);
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
     * @throws ParseException 解析异常
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
     * 检测StreamApplication注解有效性
     * 无效则抛出异常
     *
     * @param streamApplication 注解
     * @param resourceClass 应用资源类
     */
    private static void checkStreamApplication(StreamApplication streamApplication, Class<?> resourceClass) {
        if (streamApplication == null) {
            logger.error("resource class[{}] is not stream application resource", resourceClass.getName());
            throw new IllegalArgumentException(String.format("resource class[%s] is not stream application resource",
                    resourceClass.getName()));
        }
        if (StringUtils.isEmpty(streamApplication.name())) {
            logger.error("stream application name is empty");
            throw new IllegalArgumentException("stream application name is empty");
        }
        if (StringUtils.isEmpty(streamApplication.configFile()) ||
                !streamApplication.configFile().startsWith(CLASS_PATH_PREFIX)) {
            logger.error("invalid stream config file[{}], must start with prefix[{}]",
                    streamApplication.configFile(), CLASS_PATH_PREFIX);
            throw new IllegalArgumentException(String.format("invalid stream config file[%s], must start with prefix[%s]",
                    streamApplication.configFile(), CLASS_PATH_PREFIX));
        }
    }

    /**
     * 解析stream配置信息
     * 1. 首先根据命令行参数解析，如果解析失败执行第2步
     * 2. 根据资源加载配置信息解析，如果解析失败则抛出异常
     *
     * @param resourceClass 加载资源类
     * @param args 参数
     * @return 解析成功返回配置信息，否则抛出异常
     * @throws Exception 异常
     */
    private static StreamRunnerConfig parseStreamConfig(Class<?> resourceClass, String[] args) throws Exception {
        try {
            String configFile = parseCommands(args);
            String content = FileUtil.read(configFile, StandardCharsets.UTF_8);
            return JSON.toJavaObject(JSON.parseObject(content), StreamRunnerConfig.class);
        } catch (ParseException e) {
            if (resourceClass == null) throw e;
            StreamApplication streamApplication = resourceClass.getAnnotation(StreamApplication.class);
            checkStreamApplication(streamApplication, resourceClass);
            String configFile = streamApplication.configFile();
            if (configFile.startsWith(CLASS_PATH_PREFIX)) configFile = configFile.substring(CLASS_PATH_PREFIX.length());
            configFile = configFile.startsWith("/") ? configFile : String.format("/%s", configFile);
            InputStream inputStream = resourceClass.getResourceAsStream(configFile);
            if (inputStream == null) {
                logger.error("stream application config is not found for path[{}]", configFile);
                throw new IllegalArgumentException(String.format("stream application config is not found for path[%s]",
                        configFile));
            }
            int n, bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bufferSize);
            while ((n = inputStream.read(buffer, 0, buffer.length)) != -1) outputStream.write(buffer, 0, n);
            String content = outputStream.toString(StandardCharsets.UTF_8);
            outputStream.close();
            inputStream.close();
            StreamRunnerConfig config = JSON.toJavaObject(JSON.parseObject(content), StreamRunnerConfig.class);
            config.name = streamApplication.name();
            return config;
        }
    }

    /**
     * 数据流启动入口
     *
     * @param appClass 应用入口类
     * @param args 命令行参数
     * @throws Exception 异常
     */
    public static void run(Class<?> appClass, String[] args) throws Exception {
        JSON.DEFAULT_PARSER_FEATURE &= ~Feature.UseBigDecimal.getMask();
        StreamRunnerConfig config = parseStreamConfig(appClass, args);
        PreprocessParser.parse(appClass);
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
