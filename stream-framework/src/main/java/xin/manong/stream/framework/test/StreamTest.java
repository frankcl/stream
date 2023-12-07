package xin.manong.stream.framework.test;

import xin.manong.stream.framework.prepare.PreprocessManager;
import xin.manong.stream.framework.prepare.PreprocessParser;
import xin.manong.weapon.base.secret.Scanner;

/**
 * stream测试：负责初始化stream测试环境
 * 1. 解析和导入测试应用入口定义的预处理器
 * 2. 启动动态秘钥监听器
 *
 * @author frankcl
 * @date 2023-06-05 14:00:27
 */
public class StreamTest {

    private static boolean flag = false;

    /**
     * 初始化stream测试套件
     *
     * @param appClass 应用入口类
     */
    public static void init(Class appClass) {
        if (flag) return;
        synchronized (StreamTest.class) {
            if (flag) return;
            PreprocessParser.parse(appClass);
            PreprocessManager.preprocess();
            if (!Scanner.scan()) throw new RuntimeException("scan and load dynamic secret listener failed");
            flag = true;
        }
    }
}
