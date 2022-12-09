package xin.manong.stream.framework.receiver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据接收控制器配置
 *
 * @author frankcl
 * @create 2019-05-29 14:56
 */
public class ReceiveControllerConfig {

    private final static Logger logger = LoggerFactory.getLogger(ReceiveControllerConfig.class);

    /* 接收器名称 */
    public String name;
    /* 接收器全限定类名 */
    public String receiverClass;
    /* 接收数据转换器全限定类名 */
    public String converterClass;
    /* 数据接收器配置 */
    public Map<String, Object> receiverConfigMap = new HashMap<>();
    /* 接收转换器配置 */
    public Map<String, Object> converterConfigMap = new HashMap<>();
    /* 下游插件 */
    public List<String> processors = new ArrayList<>();

    /**
     * 检测接收器配置合法性
     * 接收器名称、接收器类名、接收转换器类名及下游插件配置不允许为空
     *
     * @return 合法返回true，否则返回false
     */
    public boolean check() {
        if (StringUtils.isEmpty(name)) {
            logger.error("receiver name is empty");
            return false;
        }
        if (StringUtils.isEmpty(receiverClass)) {
            logger.error("receiver class name is empty");
            return false;
        }
        if (processors == null || processors.isEmpty()) {
            logger.error("processors are empty");
            return false;
        }
        return true;
    }
}
