package xin.manong.stream.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源注解
 * 框架解析注解实现Resource注入
 *
 * @author frankcl
 * @date 2019-06-13 11:14
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {

    /* 是否必须 默认为必须true */
    boolean required() default true;
    /* 资源名称 */
    String name() default "";
}
