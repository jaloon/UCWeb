package com.tipray.service;

import java.util.List;
import java.util.Map;

/**
 * MobileAppService
 *
 * @author chenlong
 * @version 1.0 2018-10-11
 */
public interface MobileAppService {
    /**
     * 按轨迹id查询轨迹信息
     */
    Map<String, Object> getTrackById(String trackId, String carNumber);

    /**
     * 按车辆查询最新n条施解封记录
     */
    List<Map<String, Object>> findSealRecords(Long carId, String carNumber, String beginTime);

    /**
     * 按车辆查询最新n条开关锁纪录
     */
    List<Map<String, Object>> findLockRecords(Long carId, String carNumber, String beginTime);
}
