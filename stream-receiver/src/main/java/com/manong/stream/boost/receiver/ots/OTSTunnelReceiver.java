package com.manong.stream.boost.receiver.ots;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import com.manong.stream.sdk.receiver.Receiver;
import com.manong.weapon.aliyun.common.RebuildListener;
import com.manong.weapon.aliyun.common.Rebuildable;
import com.manong.weapon.aliyun.ots.OTSTunnelConfig;
import com.manong.weapon.aliyun.ots.OTSTunnelWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * OTS通道数据接收器
 *
 * @author frankcl
 * @date 2022-08-03 19:11:02
 */
public class OTSTunnelReceiver extends Receiver implements RebuildListener {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelReceiver.class);

    private IChannelProcessor channelProcessor;
    private OTSTunnelWorker tunnelWorker;

    public OTSTunnelReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean start() {
        logger.info("OTSTunnel receiver is starting ...");
        OTSTunnelConfig tunnelConfig = JSON.toJavaObject(new JSONObject(configMap), OTSTunnelConfig.class);
        if (tunnelConfig == null || !tunnelConfig.check()) return false;
        if (receiveProcessor == null) {
            logger.error("receive processor is null");
            return false;
        }
        channelProcessor = new OTSChannelProcessor(receiveProcessor);
        tunnelWorker = new OTSTunnelWorker(tunnelConfig, channelProcessor);
        if (!tunnelWorker.start()) return false;
        tunnelWorker.addRebuildListener(this);
        logger.info("OTSTunnel receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("OTSTunnel receiver is stopping ...");
        if (tunnelWorker != null) tunnelWorker.stop();
        logger.info("OTSTunnel receiver has been stopped");
    }

    @Override
    public void notifyRebuildEvent(Rebuildable rebuildObject) {
        if (rebuildObject == null || rebuildObject != tunnelWorker) return;
        if (receiveProcessor == null) return;
        receiveProcessor.sweep();
    }
}
