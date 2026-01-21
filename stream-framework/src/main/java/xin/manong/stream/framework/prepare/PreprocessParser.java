package xin.manong.stream.framework.prepare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.annotation.Import;
import xin.manong.stream.sdk.prepare.Preprocessor;
import xin.manong.weapon.base.util.ReflectArgs;
import xin.manong.weapon.base.util.ReflectUtil;

import java.lang.annotation.Annotation;

/**
 * 预处理解析器
 * 从入口类解析预处理器导入信息
 *
 * @author frankcl
 * @date 2023-06-05 13:42:55
 */
public class PreprocessParser {

    private static final Logger logger = LoggerFactory.getLogger(PreprocessParser.class);

    /**
     * 从入口类定义解析导入预处理器
     *
     * @param appClass 应用入口类
     */
    public static void parse(Class<?> appClass) {
        if (appClass == null) return;
        Annotation[] appAnnotations = appClass.getAnnotations();
        for (Annotation appAnnotation : appAnnotations) {
            if (appAnnotation.annotationType() == Import.class) {
                registerPreprocessor((Import) appAnnotation, null);
                continue;
            }
            Annotation[] annotations = appAnnotation.annotationType().getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() != Import.class) continue;
                registerPreprocessor((Import) annotation, appAnnotation);
            }
        }
    }

    /**
     * 注册预处理器
     *
     * @param importAnnotation 导入注解
     * @param appAnnotation 应用注解
     */
    private static void registerPreprocessor(Import importAnnotation, Annotation appAnnotation) {
        Class<?>[] classes = importAnnotation.value();
        if (classes == null || classes.length == 0) {
            logger.warn("Missing classes for {}", importAnnotation.getClass().getName());
            return;
        }
        for (Class<?> preprocessorClass : classes) {
            if (!Preprocessor.class.isAssignableFrom(preprocessorClass)) {
                logger.warn("Import class:{} is not an implementation of {}",
                        preprocessorClass.getName(), Preprocessor.class.getName());
                return;
            }
            ReflectArgs args = new ReflectArgs(new Class[] { Annotation.class }, new Object[] { appAnnotation });
            Preprocessor preprocessor = (Preprocessor) ReflectUtil.newInstance(preprocessorClass, args);
            PreprocessManager.register(preprocessor);
        }
    }
}
