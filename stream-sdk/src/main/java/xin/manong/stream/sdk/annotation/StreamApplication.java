package xin.manong.stream.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * stream应用配置注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StreamApplication {

    /**
     * 缺省配置文件路径
     */
    final String DEFAULT_CONFIG_FILE = "classpath:application.json";

    /* 应用名 */
    String name();
    /* 配置文件路径，默认使用application.json */
    String configFile() default DEFAULT_CONFIG_FILE;
}
