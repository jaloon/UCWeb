package com.tipray.webservice.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 物流配送单UDP下发任务缓存
 *
 * @author chenlong
 * @version 1.0 2018-09-28
 */
public class DistUdpTaskCache {
    private static final Map<Long, DistUdpTask> DIST_UDP_TASK_CACHE = new ConcurrentHashMap<>();

    /**
     * 添加任务缓存
     *
     * @param taskKey 任务Key
     * @param task    任务
     */
    public static void add(long taskKey, DistUdpTask task) {
        DIST_UDP_TASK_CACHE.put(taskKey, task);
    }

    /**
     * 添加任务缓存
     *
     * @param carId   车辆ID
     * @param storeId 仓号
     * @param task    任务
     */
    public static void add(long carId, int storeId, DistUdpTask task) {
        DIST_UDP_TASK_CACHE.put((carId << 32) | storeId, task);
    }

    /**
     * 中断任务
     *
     * @param taskKey 任务Key
     */
    public static void interrupt(long taskKey) {
        DIST_UDP_TASK_CACHE.forEach((key, value) -> {
            if (key.equals(taskKey)) {
                value.setInterrupt(true);
            }
        });
    }

    /**
     * 中断任务
     *
     * @param carId   车辆ID
     * @param storeId 仓号
     */
    public static void interrupt(long carId, int storeId) {
        interrupt((carId << 32) | storeId);
    }

    /**
     * 移除任务
     *
     * @param taskKey 任务Key
     */
    public static void remove(long taskKey) {
        DIST_UDP_TASK_CACHE.remove(taskKey);
    }

    /**
     * 构建任务Key
     *
     * @param carId   车辆ID
     * @param storeId 仓号
     * @return 任务Key
     */
    public static long buildTaskKey(long carId, int storeId) {
        return (carId << 32) | storeId;
    }
}
