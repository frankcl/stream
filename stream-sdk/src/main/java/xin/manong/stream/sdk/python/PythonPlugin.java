package xin.manong.stream.sdk.python;

import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.python.embedding.GraalPyResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.stream.sdk.common.UnacceptableException;
import xin.manong.stream.sdk.plugin.Plugin;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * python插件实现
 *
 * @author frankcl
 * @date 2025-09-05 21:02:16
 */
public class PythonPlugin extends Plugin {

    private static final Logger logger = LoggerFactory.getLogger(PythonPlugin.class);

    private static final String PYTHON_LANGUAGE = "python";

    private static final List<Class<?>> ALLOW_LOOKUP_CLASSES = new ArrayList<>() {
        {
            add(KVRecord.class);
            add(KVRecords.class);
            add(ProcessResult.class);
        }
    };

    private Context context;
    private Value pluginInstance;

    public PythonPlugin(Map<String, Object> configMap) {
        super(configMap);
    }

    /**
     * 初始化插件
     *
     * @return 如果成功返回true，否则返回false
     */
    @Override
    public boolean init() {
        String pythonEnv = (String) configMap.get(StreamConstants.PYTHON_ENV);
        if (StringUtils.isEmpty(pythonEnv)) {
            logger.error("Python env is empty");
            return false;
        }
        String pythonFile = (String) configMap.get(StreamConstants.PYTHON_FILE);
        if (StringUtils.isEmpty(pythonFile)) {
            logger.error("Python plugin file is empty");
            return false;
        }
        pythonFile = pythonFile.endsWith(".py") ? pythonFile.substring(0, pythonFile.length() - 3) : pythonFile;
        pythonFile = pythonFile.replace("/", ".");
        String pythonClass = (String) configMap.get(StreamConstants.PYTHON_CLASS);
        if (StringUtils.isEmpty(pythonClass)) {
            logger.error("Python plugin class is empty");
            return false;
        }
        Context.Builder builder = GraalPyResources.contextBuilder(Paths.get(pythonEnv));
        builder.allowHostClassLookup(className -> {
            for (Class<?> clazz : ALLOW_LOOKUP_CLASSES) {
                if (className.equals(clazz.getName())) return true;
            }
            return false;
        });
        context = builder.build();
        context.eval(PYTHON_LANGUAGE, String.format("from %s import %s", pythonFile, pythonClass));
        pluginInstance = context.getBindings(PYTHON_LANGUAGE).getMember(pythonClass).newInstance(configMap);
        return pluginInstance.getMember("init").execute().asBoolean();
    }

    /**
     * 销毁插件
     *
     */
    @Override
    public void destroy() {
        if (pluginInstance != null) pluginInstance.getMember("destroy").execute();
        if (context != null) context.close();
    }

    /**
     * flush插件内部数据
     */
    @Override
    public void flush() {
        if (pluginInstance != null) pluginInstance.getMember("flush").execute();
    }

    /**
     * 处理数据
     *
     * @param kvRecord 数据
     * @return 处理结果
     * @throws Exception 异常
     */
    @Override
    public ProcessResult handle(KVRecord kvRecord) throws Exception {
        if (pluginInstance != null) {
            return pluginInstance.getMember("handle").execute(kvRecord).as(ProcessResult.class);
        }
        logger.error("Python plugin instance is not initialized");
        throw new UnacceptableException("Python plugin instance is not initialized");
    }
}
