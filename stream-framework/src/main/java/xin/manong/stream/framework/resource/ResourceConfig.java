package xin.manong.stream.framework.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源配置
 *
 * @author frankcl
 * @date 2019-06-01 11:26
 */
public class ResourceConfig {

    private final static Logger logger = LoggerFactory.getLogger(ResourceConfig.class);

    private final static int DEFAULT_NUM = 1;

    public int num = DEFAULT_NUM;
    public String name;
    public String className;
    public Map<String, Object> configMap = new HashMap<>();

    /**
     * 检测资源配置合法性
     * 资源名和资源类名不允许为空
     *
     * @return 合法返回true，否则返回false
     */
    boolean check() {
        if (num <= 0) num = DEFAULT_NUM;
        if (configMap == null) configMap = new HashMap<>();
        if (StringUtils.isEmpty(name)) {
            logger.error("resource name is empty");
            return false;
        }
        if (StringUtils.isEmpty(className)) {
            logger.error("resource class name is empty");
            return false;
        }
        return true;
    }
}
