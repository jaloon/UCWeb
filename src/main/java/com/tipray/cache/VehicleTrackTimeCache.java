package com.tipray.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 车辆GPS轨迹时间缓存
 *
 * @author chenlong
 * @version 1.0 2018-08-29
 */
public final class VehicleTrackTimeCache {
    private static final Map<Integer, Integer> map = new ConcurrentHashMap<>();

    /**
     * 更新轨迹时间
     * @param carId 车辆ID
     * @param second 轨迹时间秒数
     */
    public static void updateTime(int carId, int second) {
        synchronized (map) {
            map.put(carId, second);
        }
    }

    /**
     * 获取最后轨迹时间秒数
     * @param carId 车辆ID
     * @return 轨迹时间秒数
     */
    public static Integer getLastTrackTime(int carId) {
        return map.get(carId);
    }

    /**
     * 是否更新
     * @param carId 车辆ID
     * @param second 轨迹时间秒数
     * @return true/false
     */
    public static boolean isUpdate(int carId, int second) {
        synchronized (map) {
            Integer last = map.get(carId);
            if (last != null && last > second) {
                return false;
            }
            map.put(carId, second);
            return true;
        }
    }
}