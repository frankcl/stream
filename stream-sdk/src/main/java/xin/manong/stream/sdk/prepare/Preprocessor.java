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
     * 应用注解：引入Import注解的外层注解
     * 用户可通过应用注解获取其他信息
     */
    protected Annotation appAnnotation;

    public Preprocessor(Annotation appAnnotation) {
        this.appAnnotation = appAnnotation;
    }

    /**
     * 预处理
     */
    public abstract void process();

    /**
     * 销毁资源
     */
    public void destroy() {}
}
