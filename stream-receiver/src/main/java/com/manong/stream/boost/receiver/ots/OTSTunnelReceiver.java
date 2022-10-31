package com.manong.stream.boost.receiver.ots;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.TunnelClient;
import com.alicloud.openservices.tablestore.model.StreamRecord;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelRequest;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelResponse;
import com.alicloud.openservices.tablestore.model.tunnel.TunnelInfo;
import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import com.alicloud.openservices.tablestore.tunnel.worker.ProcessRecordsInput;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorker;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorkerConfig;
import com.manong.stream.sdk.receiver.Receiver;
import com.manong.weapon.aliyun.common.RebuildManager;
import com.manong.weapon.aliyun.common.Rebuildable;
import com.manong.weapon.aliyun.secret.DynamicSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OTS通道数据接收器
 *
 * @author frankcl
 * @date 2022-08-03 19:11:02
 */
public class OTSTunnelReceiver extends Receiver implements Rebuildable {

    private final static Logger logger = LoggerFactory.getLogger(OTSTunnelReceiver.class);

    private OTSTunnelConfig tunnelConfig;
    private OTSTunnelMonitor monitor;
    protected TunnelClient tunnelClient;
    protected TunnelWorker worker;

    public OTSTunnelReceiver(Map<String, Object> configMap) {
        super(configMap);
    }

    /**
     * 构建OTS通道接收器
     *
     * @return 构建成功返回true，否则返回false
     */
    private boolean build() {
        tunnelClient = new TunnelClient(tunnelConfig.endpoint, tunnelConfig.aliyunSecret.accessKey,
                tunnelConfig.aliyunSecret.secretKey, tunnelConfig.instance);
        DescribeTunnelRequest request = new DescribeTunnelRequest(tunnelConfig.table, tunnelConfig.tunnel);
        try {
            DescribeTunnelResponse response = tunnelClient.describeTunnel(request);
            TunnelInfo tunnelInfo = response.getTunnelInfo();
            TunnelWorkerConfig workerConfig = new TunnelWorkerConfig(createChannelProcessor());
            workerConfig.setMaxRetryIntervalInMillis(tunnelConfig.maxRetryIntervalMs);
            workerConfig.setHeartbeatIntervalInSec(tunnelConfig.heartBeatIntervalSec);
            if (tunnelConfig.maxChannelParallel > 0) workerConfig.setMaxChannelParallel(tunnelConfig.maxChannelParallel);
            int threadNum = tunnelConfig.consumeThreadNum;
            workerConfig.setReadRecordsExecutor(createThreadPoolExecutor("tunnel_reader", threadNum));
            workerConfig.setProcessRecordsExecutor(createThreadPoolExecutor("tunnel_processor", threadNum));
            worker = new TunnelWorker(tunnelInfo.getTunnelId(), tunnelClient, workerConfig);
            worker.connectAndWorking();
            monitor = new OTSTunnelMonitor(tunnelConfig, tunnelClient);
            monitor.start();
            logger.info("build OTSTunnel receiver success");
        } catch (Exception e) {
            logger.error("build OTSTunnel receiver failed");
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void rebuild() {
        logger.info("OTS receiver is rebuilding ...");
        if (DynamicSecret.accessKey.equals(tunnelConfig.aliyunSecret.accessKey) &&
                DynamicSecret.secretKey.equals(tunnelConfig.aliyunSecret.secretKey)) {
            logger.warn("secret is not changed, ignore OTS receiver rebuilding");
            return;
        }
        tunnelConfig.aliyunSecret.accessKey = DynamicSecret.accessKey;
        tunnelConfig.aliyunSecret.secretKey = DynamicSecret.secretKey;
        OTSTunnelMonitor prevMonitor = monitor;
        TunnelWorker prevWorker = worker;
        TunnelClient prevClient = tunnelClient;
        if (!build()) throw new RuntimeException("rebuild OTS receiver failed");
        if (prevMonitor != null) prevMonitor.stop();
        if (prevWorker != null) prevWorker.shutdown();
        if (prevClient != null) prevClient.shutdown();
        logger.info("OTS receiver rebuild success");
    }

    @Override
    public boolean start() {
        logger.info("OTSTunnel receiver is starting ...");
        tunnelConfig = JSON.toJavaObject(new JSONObject(configMap), OTSTunnelConfig.class);
        if (tunnelConfig == null || !tunnelConfig.check()) return false;
        if (!build()) return false;
        RebuildManager.register(this);
        logger.info("OTSTunnel receiver has been started");
        return true;
    }

    @Override
    public void stop() {
        logger.info("OTSTunnel receiver is stopping ...");
        RebuildManager.unregister(this);
        if (monitor != null) monitor.stop();
        if (worker != null) worker.shutdown();
        if (tunnelClient != null) tunnelClient.shutdown();
        logger.info("OTSTunnel receiver has been stopped");
    }

    /**
     * 创建Channel数据处理器
     *
     * @return Channel数据处理器
     */
    protected IChannelProcessor createChannelProcessor() {
        return new IChannelProcessor() {
            @Override
            public void process(ProcessRecordsInput input) {
                List<StreamRecord> records = input.getRecords();
                for (StreamRecord record : records) {
                    try {
                        receiveProcessor.process(record);
                    } catch (Throwable e) {
                        logger.error("process stream record failed for trace[{}] and token[{}]",
                                input.getTraceId(), input.getNextToken());
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void shutdown() {
                logger.info("channel processor has been shutdown");
            }
        };
    }

    /**
     * 创建线程池执行器
     *
     * @param name 线程池名称
     * @param threadNum 线程数
     * @return 线程池执行器实例
     */
    private ThreadPoolExecutor createThreadPoolExecutor(String name, int threadNum) {
        logger.info("create thread pool executor[{}:{}]", name, threadNum);
        return new ThreadPoolExecutor(threadNum, threadNum, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue(16), new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger();
            public Thread newThread(Runnable task) {
                String threadName = String.format("%s-%d", name, this.counter.getAndIncrement());
                logger.info("create channel receiver thread[{}] success", threadName);
                return new Thread(task, threadName);
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
