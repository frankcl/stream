package xin.manong.stream.sdk.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 管理开销较大资源的生命周期，达到全局资源共享的目的
 * 用户自定义资源需要继承此类，实现create和destroy方法，定义资源创建和销毁逻辑
 *
 * @author frankcl
 * @date 2019-06-01 10:36
 */
public abstract class Resource<T> {

    private final static Logger logger = LoggerFactory.getLogger(Resource.class);

    /* 资源名称 */
    protected String name;
    /* 资源对象实例 */
    protected T object;

    public Resource(String name) {
        this.name = name;
    }

    /**
     * 获取资源名
     *
     * @return 资源名
     */
    public final String getName() {
        return name;
    }

    /**
     * 获取资源对象
     *
     * @return 资源对象实例
     */
    public final T get() {
        return object;
    }

    /**
     * 验证资源有效性，默认实现返回true
     * 自定义资源可以复写该方法，无效资源可触发框架进行销毁
     *
     * @return 如果有效返回true，否则返回false
     */
    public boolean validate() {
        return true;
    }

    /**
     * 根据配置创建资源
     *
     * @param configMap 配置信息
     */
    public abstract T create(Map<String, Object> configMap);

    /**
     * 销毁资源
     */
    public abstract void destroy();

    /**
     * 根据配置信息构建资源
     * ResourceFactory负责调用该方法，不可复写
     *
     * @param configMap 配置信息
     */
    public final void build(Map<String, Object> configMap) {
        if (object != null) return;
        try {
            object = create(configMap);
        } catch (Exception e) {
            logger.error("Build resource:{} failed", name);
            logger.error(e.getMessage(), e);
        }
    }
}
