package xin.manong.stream.sdk.prepare;

import java.lang.annotation.Annotation;

/**
 * 预处理器
 * stream流程启动前调用
 *
 * @author frankcl
 * @date 2022-12-13 20:36:53
 */
public abstract class Preprocessor {

    /**
     * Import注解外层引用注解
     * 用户可通过outerAnnotation获取信息
     */
    protected Annotation outerAnnotation;

    public Preprocessor(Annotation outerAnnotation) {
        this.outerAnnotation = outerAnnotation;
    }

    /**
     * 预处理
     */
    public abstract void process();
}
