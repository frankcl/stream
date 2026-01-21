package xin.manong.stream.configmap.prepare;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.boost.resource.etcd.EtcdClientResource;
import xin.manong.stream.configmap.core.EtcdConfigMap;
import xin.manong.stream.framework.resource.ResourceConfig;
import xin.manong.stream.framework.runner.StreamRunnerConfig;
import xin.manong.stream.sdk.prepare.Preprocessor;
import xin.manong.stream.configmap.annotation.EnableEtcdConfigMap;
import xin.manong.stream.sdk.resource.Resource;
import xin.manong.weapon.base.etcd.EtcdClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ETCD配置中心预处理器
 * 1. 初始化EtcdClient
 * 2. 初始化ETCD配置中心
 *
 * @author frankcl
 * @date 2025-10-20 11:52:55
 */
public class EtcdConfigMapPreprocessor extends Preprocessor {

    private static final Logger logger = LoggerFactory.getLogger(EtcdConfigMapPreprocessor.class);

    private static final int BUFFER_SIZE = 4096;
    private final static String CLASS_PATH_PREFIX = "classpath:";

    public EtcdConfigMapPreprocessor(Annotation appAnnotation) {
        super(appAnnotation);
    }

    @Override
    public void process() {
        if (!(appAnnotation instanceof EnableEtcdConfigMap annotation)) {
            logger.error("App annotation:{} is not {}", appAnnotation.annotationType().getName(),
                    EnableEtcdConfigMap.class.getName());
            throw new IllegalStateException(String.format("App annotation:%s is not %s",
                    appAnnotation.annotationType().getName(), EnableEtcdConfigMap.class.getName()));
        }
        if (StringUtils.isEmpty(annotation.name())) {
            logger.error("Etcd client resource name is empty");
            throw new IllegalStateException("Etcd client resource name is empty");
        }
        String configFile = annotation.configFile();
        if (configFile.startsWith(CLASS_PATH_PREFIX)) configFile = configFile.substring(CLASS_PATH_PREFIX.length());
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceURL = classLoader.getResource(configFile);
        if (resourceURL == null) {
            logger.error("Config file is not found:{}", configFile);
            throw new IllegalArgumentException(String.format("Config file is not found:%s", configFile));
        }
        try (InputStream input = resourceURL.openStream();
             ByteArrayOutputStream output = new ByteArrayOutputStream(BUFFER_SIZE)) {
            input.transferTo(output);
            StreamRunnerConfig config = JSON.toJavaObject(JSON.parseObject(
                    output.toString(StandardCharsets.UTF_8)), StreamRunnerConfig.class);
            List<ResourceConfig> configs = config.resources.stream().filter(resourceConfig ->
                    resourceConfig.name.equals(annotation.name())).toList();
            if (configs.size() != 1) {
                throw new IllegalStateException(String.format(
                        "Can not find Etcd client resource for name:%s", annotation.name()));
            }
            ResourceConfig resourceConfig = configs.get(0);
            if (!resourceConfig.className.equals(EtcdClientResource.class.getName())) {
                throw new IllegalStateException(String.format("Unexpected resource class:%s", resourceConfig.className));
            }
            Resource<EtcdClient> resource = new EtcdClientResource(resourceConfig.name);
            resource.build(resourceConfig.configMap);
            EtcdClient etcdClient = resource.get();
            if (etcdClient == null) throw new IllegalStateException("Create Etcd client failed");
            EtcdConfigMap.init(etcdClient);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void destroy() {
        EtcdConfigMap.destroy();
    }
}
