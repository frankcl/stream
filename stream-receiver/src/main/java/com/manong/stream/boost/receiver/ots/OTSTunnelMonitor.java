package com.manong.stream.boost.receiver.ots;

import com.alicloud.openservices.tablestore.TunnelClient;
import com.alicloud.openservices.tablestore.model.tunnel.ChannelInfo;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelRequest;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * OTS通道消费监控器
 *
 * @author frankcl
 * @create 2019-06-19 16:30
 */
public class OTSTunnelMonitor implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelMonitor.class);

    private final static long DEFAULT_CHECK_TIME_INTERVAL_MS = 600000;

    private boolean running = false;
    private long checkTimeIntervalMs = DEFAULT_CHECK_TIME_INTERVAL_MS;
    private OTSTunnelConfig tunnelConfig;
    private TunnelClient tunnelClient;
    private Thread workThread;

    public OTSTunnelMonitor(OTSTunnelConfig tunnelConfig, TunnelClient tunnelClient) {
        this.tunnelConfig = tunnelConfig;
        this.tunnelClient = tunnelClient;
    }

    /**
     * 启动监控
     */
    public void start() {
        logger.info("OTSTunnel monitor[{}] is starting ...");
        running = true;
        workThread = new Thread(this, "TunnelMonitor");
        workThread.start();
        logger.info("tunnel monitor[{}] has been started");
    }

    /**
     * 停止监控
     */
    public void stop() {
        logger.info("tunnel monitor[{}] is stopping ...");
        running = false;
        if (workThread.isAlive()) workThread.interrupt();
        try {
            workThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("tunnel monitor[{}] has been stopped");
    }

    @Override
    public void run() {
        while (running) {
            DescribeTunnelRequest request = new DescribeTunnelRequest(tunnelConfig.table, tunnelConfig.tunnel);
            DescribeTunnelResponse response = tunnelClient.describeTunnel(request);
            List<ChannelInfo> channels = response.getChannelInfos();
            int delayChannelNum = 0;
            long currentTimestamp = System.currentTimeMillis();
            for (ChannelInfo channel : channels) {
                long consumeTimestamp = channel.getChannelConsumePoint().getTime();
                if (consumeTimestamp <= 0) continue;
                long timeInterval = currentTimestamp - consumeTimestamp;
                if (timeInterval < tunnelConfig.maxConsumeDelayMs) continue;
                logger.warn("consume delay[{}] for channel[{}] in tunnel[{}] of table[{}]", timeInterval,
                        channel.getChannelId(), tunnelConfig.tunnel, tunnelConfig.table, timeInterval);
                delayChannelNum++;
            }
            if (delayChannelNum > 0) {
                //TODO 添加延迟报警逻辑
            }
            logger.info("tunnel monitor is running, sleep {} ms", checkTimeIntervalMs);
            try {
                Thread.sleep(checkTimeIntervalMs);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
