package xin.manong.stream.boost.receiver.ots;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.stream.sdk.receiver.Receiver;
import xin.manong.weapon.aliyun.ots.OTSTunnel;
import xin.manong.weapon.aliyun.ots.OTSTunnelConfig;
import xin.manong.weapon.aliyun.ots.OTSTunnelWorkerConfig;
import xin.manong.weapon.base.listen.Listener;
import xin.manong.weapon.base.listen.RebuildEvent;

import java.util.Map;

/**
 * OTS通道数据接收器
 *
 * @author frankcl
 * @date 2022-08-03 19:11:02
 */
public class OTSTunnelReceiver extends Receiver implements Listener {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelReceiver.class);

    private IChannelProcessor channelProcessor;
    private OTSTunnel tunnel;

    public OTSTunnelReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        logger.info("OTSTunnel receiver is starting ...");
        if (receiveProcessor == null) {
            logger.error("receive processor is null");
            return false;
        }
        channelProcessor = new OTSChannelProcessor(receiveProcessor);
        OTSTunnelConfig tunnelConfig = JSON.toJavaObject(new JSONObject(configMap), OTSTunnelConfig.class);
        if (tunnelConfig == null) {
            logger.error("parse OTS tunnel config failed");
            return false;
        }
        if (tunnelConfig.workerConfigs == null || tunnelConfig.workerConfigs.isEmpty()) {
            logger.error("miss OTS tunnel worker config");
            return false;
        }
        for (OTSTunnelWorkerConfig workerConfig : tunnelConfig.workerConfigs) {
            workerConfig.channelProcessor = channelProcessor;
        }
        tunnel = new OTSTunnel(tunnelConfig);
        tunnel.setAppName(appName);
        tunnel.setAlarmProducer(alarmProducer);
        if (!tunnel.start()) return false;
        tunnel.addRebuildListener(this);
        logger.info("OTSTunnel receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("OTSTunnel receiver is stopping ...");
        if (tunnel != null) tunnel.stop();
        if (channelProcessor != null) channelProcessor.shutdown();
        logger.info("OTSTunnel receiver has been stopped");
    }

    @Override
    public void onRebuild(@NotNull RebuildEvent event) {
        if (event.target == null || event.target != tunnel) return;
        if (receiveProcessor == null) return;
        receiveProcessor.sweep();
    }
}
