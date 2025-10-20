package xin.manong.stream.configmap.annotation;

import xin.manong.stream.configmap.prepare.EtcdConfigMapPreprocessor;
import xin.manong.stream.sdk.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动ETCD配置中心
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EtcdConfigMapPreprocessor.class)
public @interface EnableEtcdConfigMap {

    /**
     * 缺省配置文件路径
     */
    String DEFAULT_CONFIG_FILE = "classpath:application.json";

    /* Etcd客户端资源名称 */
    String name();
    /* 配置文件路径，默认使用application.json */
    String configFile() default DEFAULT_CONFIG_FILE;
}
