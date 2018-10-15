package com.tipray.webservice.job;

import com.tipray.bean.ResponseMsg;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.net.NioUdpServer;
import com.tipray.net.constant.UdpReplyWorkErrorEnum;
import com.tipray.util.DateUtil;
import com.tipray.util.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 物流配送单UDP下发任务
 *
 * @author chenlong
 * @version 1.0 2018-09-28
 */
public class DistUdpTask {
    private static final Logger logger = LoggerFactory.getLogger(DistUdpTask.class);
    private static final NioUdpServer UDP_SERVER = (NioUdpServer) SpringBeanUtil.getBean("udpServer");
    private static final long TIMEOUT = 5 * 60L;
    private ScheduledExecutorService service;
    private String invoice;
    private int type;
    private long taskKey;
    private int cacheId;
    private long distTime;
    private ByteBuffer src;
    private Map<String, Object> params;
    private boolean interrupt;

    /**
     * 任务构造器
     *
     * @param invoice 配送单号
     * @param type    配送类型：1 换站，2 新增
     * @param taskKey 任务key
     * @param cacheId 缓存ID
     * @param src     UDP通信数据
     * @param params  换站参数
     */
    private DistUdpTask(String invoice, int type, long taskKey, int cacheId, ByteBuffer src, Map<String, Object> params) {
        this.invoice = invoice;
        this.type = type;
        this.taskKey = taskKey;
        this.cacheId = cacheId;
        this.distTime = System.currentTimeMillis();
        this.src = src;
        this.params = params;
    }

    /**
     * 设置中断
     *
     * @param interrupt 是否中断
     */
    void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }

    /**
     * 构建物流新增任务
     *
     * @param invoice 配送单号
     * @param taskKey 任务key
     * @param cacheId 缓存ID
     * @param src     UDP通信数据
     * @param params  换站参数
     */
    public static DistUdpTask buildNewDistTask(String invoice, long taskKey, int cacheId, ByteBuffer src,
                                               Map<String, Object> params) {
        return new DistUdpTask(invoice, 2, taskKey, cacheId, src, params).execute();
    }

    /**
     * 构建物流换站任务
     *
     * @param invoice 配送单号
     * @param taskKey 任务key
     * @param cacheId 缓存ID
     * @param src     UDP通信数据
     * @param params  换站参数
     */
    public static DistUdpTask buildAlterDistTask(String invoice, long taskKey, int cacheId, ByteBuffer src,
                                                 Map<String, Object> params) {
        return new DistUdpTask(invoice, 1, taskKey, cacheId, src, params).execute();
    }

    /**
     * 执行任务
     *
     * @return this DistUdpTask
     */
    private DistUdpTask execute() {
        service = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            synchronized (AsynUdpCommCache.class) {
                ResponseMsg reply = AsynUdpCommCache.getResultCache(cacheId);
                // 已经收到UDP应答
                if (reply != null) {
                    AsynUdpCommCache.removeResultCache(cacheId);
                    byte errorTag = reply.getTag();
                    if (errorTag == ErrorTagConst.NO_ERROR_TAG) {
                        logger.info("配送单【{}】{}下发成功！", invoice, type == 1 ? "换站" : "新增");
                        stop();
                        return;
                    }
                    if (errorTag == ErrorTagConst.UDP_REPLY_ERROR_TAG) {
                        int errorId = reply.getCode();
                        // int commonError = errorId >>> 16;
                        int workError = errorId & 0xFFFF;
                        if (workError == UdpReplyWorkErrorEnum.OPERATE_FAIL_105_OPERATION_NOT_SUPPORTED.code() && type == 2) {
                            logger.info("配送单【{}】新增下发无效：车台主动获取配送单，停止重发！", invoice);
                            stop();
                            return;
                        }
                        if (workError == UdpReplyWorkErrorEnum.OPERATE_FAIL_102_ID_ERROR.code() && type == 1) {
                            logger.info("配送单【{}】换站下发无效：ID不匹配，停止重发！", invoice);
                            stop();
                            return;
                        }
                    }
                    if (isIssueTimeout()) {
                        stop();
                    }
                    // 重新添加换站参数缓存
                    AsynUdpCommCache.putParamCache(cacheId, params);
                }

                if (interrupt) {
                    logger.warn("配送单【{}】{}下发超时：新的配送信息覆盖，停止重发！", invoice, type == 1 ? "换站" : "新增");
                    AsynUdpCommCache.removeParamCache(cacheId);
                    stop();
                    return;
                }
                if (System.currentTimeMillis() - distTime > DateUtil.DAY_DIFF) {
                    logger.warn("配送单【{}】{}下发超时：超过24小时，停止重发！", invoice, type == 1 ? "换站" : "新增");
                    AsynUdpCommCache.removeParamCache(cacheId);
                    stop();
                    return;
                }

                UDP_SERVER.send(src);
            }
        };
        // 参数：1、任务体 2、首次执行的延时时间 3、任务执行间隔 4、间隔时间单位
        service.scheduleAtFixedRate(task, TIMEOUT, TIMEOUT, TimeUnit.SECONDS);
        return this;
    }

    private boolean isIssueTimeout() {
        if (interrupt) {
            logger.warn("配送单【{}】{}下发超时：新的配送信息覆盖，停止重发！", invoice, type == 1 ? "换站" : "新增");
            if (type == 1) {
                AsynUdpCommCache.removeParamCache(cacheId);
            }
            return true;
        }
        if (System.currentTimeMillis() - distTime > DateUtil.DAY_DIFF) {
            logger.warn("配送单【{}】{}下发超时：超过24小时，停止重发！", invoice, type == 1 ? "换站" : "新增");
            if (type == 1) {
                AsynUdpCommCache.removeParamCache(cacheId);
            }
            return true;
        }
        return false;
    }


    /**
     * 结束任务
     */
    private void stop() {
        DistUdpTaskCache.remove(taskKey);
        service.shutdown();
    }
}