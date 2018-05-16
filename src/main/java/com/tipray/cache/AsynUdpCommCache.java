package com.tipray.cache;

import com.tipray.bean.ResponseMsg;
import com.tipray.net.Heartbeat;
import com.tipray.websocket.MonitorWebSocketHandler;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.AsyncContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 异步UDP通讯任务缓存
 *
 * @author chenlong
 * @version 1.0 2018-04-08
 */
public class AsynUdpCommCache {
    /**
     * 心跳缓存
     */
    private static final Heartbeat LAST_HEARTBEAT = new Heartbeat();
    /**
     * 任务日志
     */
    private static final Map<Integer, Long> LOG_MAP = new ConcurrentHashMap<>();
    /**
     * 任务描述
     */
    private static final Map<Integer, String> DESCRIPTION_MAP = new ConcurrentHashMap<>();
    /**
     * 任务结果
     */
    private static final Map<Integer, ResponseMsg> RESULT_MAP = new ConcurrentHashMap<>();
    /**
     * 任务参数
     */
    private static final Map<Integer, Map<String, Object>> PARAM_MAP = new ConcurrentHashMap<>();
    /**
     * AsyncContext异步请求上下文（异步请求容器）缓存
     */
    private static final Map<Integer, AsyncContext> ASYNC_CONTEXT_MAP = new ConcurrentHashMap<>();

    public static final Map<Integer, DeferredResult<ResponseMsg>> DEFERRED_RESULT_MAP = new ConcurrentHashMap<>();

    /**
     * 添加任务日志缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @param logId   {@link Long} 日志ID
     * @return {@link Long} 日志ID
     */
    public static synchronized Long putLogCache(Integer cacheId, Long logId) {
        return LOG_MAP.put(cacheId, logId);
    }

    /**
     * 移除任务日志缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link Long} 日志ID
     */
    public static synchronized Long removeLogCache(Integer cacheId) {
        return LOG_MAP.remove(cacheId);
    }

    /**
     * 获取任务日志缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link Long} 日志ID
     */
    public static synchronized Long getLogCache(Integer cacheId) {
        return LOG_MAP.get(cacheId);
    }

    /**
     * 添加任务描述缓存
     *
     * @param cacheId     {@link Integer} 缓存ID
     * @param description {@link String} 任务描述
     * @return {@link String} 任务描述
     */
    public static synchronized String putTaskCache(Integer cacheId, String description) {
        return DESCRIPTION_MAP.put(cacheId, description);
    }

    /**
     * 移除任务描述缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link String} 任务描述
     */
    public static synchronized String removeTaskCache(Integer cacheId) {
        return DESCRIPTION_MAP.remove(cacheId);
    }

    /**
     * 获取任务描述缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link String} 任务描述
     */
    public static synchronized String getTaskCache(Integer cacheId) {
        return DESCRIPTION_MAP.get(cacheId);
    }

    /**
     * 添加任务结果缓存
     *
     * @param cacheId     {@link Integer} 缓存ID
     * @param responseMsg {@link ResponseMsg} 任务结果
     * @return {@link ResponseMsg} 任务结果
     */
    public static synchronized ResponseMsg putResultCache(Integer cacheId, ResponseMsg responseMsg) {
        return RESULT_MAP.put(cacheId, responseMsg);
    }

    /**
     * 移除任务结果缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link ResponseMsg} 任务结果
     */
    public static synchronized ResponseMsg removeResultCache(Integer cacheId) {
        return RESULT_MAP.remove(cacheId);
    }

    /**
     * 获取任务结果缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link ResponseMsg} 任务结果
     */
    public static synchronized ResponseMsg getResultCache(Integer cacheId) {
        return RESULT_MAP.get(cacheId);
    }

    /**
     * 添加任务参数缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @param params  {@link Map} 任务参数
     * @return {@link Map} 任务参数
     */
    public static synchronized Map<String, Object> putParamCache(Integer cacheId, Map<String, Object> params) {
        return PARAM_MAP.put(cacheId, params);
    }

    /**
     * 移除任务参数缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link Map} 任务参数
     */
    public static synchronized Map<String, Object> removeParamCache(Integer cacheId) {
        return PARAM_MAP.remove(cacheId);
    }

    /**
     * 获取任务参数缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link Map} 任务参数
     */
    public static synchronized Map<String, Object> getParamCache(Integer cacheId) {
        return PARAM_MAP.get(cacheId);
    }

    /**
     * 添加AsyncContext缓存
     *
     * @param cacheId      {@link Integer} 缓存ID
     * @param asyncContext {@link AsyncContext} 异步请求上下文
     * @return {@link AsyncContext} 异步请求上下文
     */
    public static synchronized AsyncContext putAsyncContext(Integer cacheId, AsyncContext asyncContext) {
        return ASYNC_CONTEXT_MAP.put(cacheId, asyncContext);
    }

    /**
     * 获取并移除AsyncContext缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link AsyncContext} 异步请求上下文
     */
    public static synchronized AsyncContext getAndRemoveAsyncContext(Integer cacheId) {
        return ASYNC_CONTEXT_MAP.remove(cacheId);
    }

    /**
     * 获取AsyncContext缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @return {@link AsyncContext} 异步请求上下文
     */
    public static synchronized AsyncContext getAsyncContext(Integer cacheId) {
        return ASYNC_CONTEXT_MAP.get(cacheId);
    }

    /**
     * 构建缓存ID
     *
     * @param bizId    {@link Short} 业务ID
     * @param serialNo {@link Short} 序列号
     * @return {@link Integer} 缓存ID
     */
    public static int buildCacheId(int bizId, short serialNo) {
        return bizId << 16 | serialNo;
    }

    private AsynUdpCommCache() {
    }

    /**
     * 获取最后心跳时间
     *
     * @return {@link Long} 最后心跳时间毫秒数
     */
    public static long getLastHeartbeat() {
        return LAST_HEARTBEAT.getLastHeartbeat();
    }

    /**
     * 更新心跳时间为当前时间
     */
    public static void updateHeartbeat() {
        updateHeartbeat(System.currentTimeMillis());
    }

    /**
     * 监控业务WebSocket处理器
     */
    private static final MonitorWebSocketHandler MONITOR_WEB_SOCKET_HANDLER = new MonitorWebSocketHandler();

    /**
     * 更新心跳时间
     *
     * @param updatedHeartbeat {@link Long} 心跳时间
     */
    public static void updateHeartbeat(long updatedHeartbeat) {
        synchronized (LAST_HEARTBEAT) {
            LAST_HEARTBEAT.updateHeartbeat(updatedHeartbeat);
            if (LAST_HEARTBEAT.isAlarm()) {
                LAST_HEARTBEAT.setAlarm(false);
                MONITOR_WEB_SOCKET_HANDLER.removeUdpCommAlarm();
            }
            heartbeatDetection(updatedHeartbeat);
        }
    }

    /**
     * UDP通信异常检测(心跳检测)
     *
     * @param lastHeartbeat {@link Long} 最后心跳时间毫秒数
     */
    public static void heartbeatDetection(long lastHeartbeat) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            synchronized (LAST_HEARTBEAT) {
                if (lastHeartbeat == LAST_HEARTBEAT.getLastHeartbeat()) {
                    // 3分钟后心跳时间未更新，UDP通信异常报警
                    LAST_HEARTBEAT.setAlarm(true);
                    MONITOR_WEB_SOCKET_HANDLER.dealUdpCommAlarm();
                }
                service.shutdown();
            }
        };
        // 参数：1、任务体 2、从现在开始延迟执行的时间 3、时间单位
        service.schedule(task, 3, TimeUnit.MINUTES);
    }
}
