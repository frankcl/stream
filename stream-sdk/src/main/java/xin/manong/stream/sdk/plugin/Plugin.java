package xin.manong.stream.sdk.plugin;

import xin.manong.stream.sdk.common.ProcessResult;
import xin.manong.weapon.base.record.KVRecord;

import java.util.Map;

/**
 * 插件：处理数据，满足功能需求
 * 插件抛出普通异常框架捕获处理消化
 * 插件抛出UnacceptableException或Error框架认为处理失败，重新处理数据
 * 用户定义插件继承此类，实现以下方法
 * 1. init: 初始化插件
 * 2. destroy: 销毁插件
 * 3. handle: 处理数据
 * 用户插件可覆盖以下方法（默认实现为空）
 * 1. flush: 冲刷插件内容，如业务需要定期落地效果，框架可保证数据定期落地（此方法框架定期调用）
 *
 * @author frankcl
 * @date 2019-05-27 13:45
 */
public abstract class Plugin {

    protected final static String FORK_NEXT = "next";
    protected final static String FORK_SUCCESS = "success";
    protected final static String FORK_FAIL = "fail";

    /* 插件配置 */
    protected Map<String, Object> configMap;

    public Plugin(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    /**
     * flush插件内部数据，保证数据落地
     * 默认实现为空，用户可覆写此方法
     */
    public void flush() {
    }

    /**
     * 初始化插件
     *
     * @return 如果成功返回true，否则返回false
     */
    public boolean init() {
        return true;
    }

    /**
     * 销毁插件
     *
     */
    public void destroy() {
    }

    /**
     * 处理数据
     *
     * @param kvRecord 数据
     * @return 处理结果
     * @throws Exception 异常
     */
    public abstract ProcessResult handle(KVRecord kvRecord) throws Exception;
}
