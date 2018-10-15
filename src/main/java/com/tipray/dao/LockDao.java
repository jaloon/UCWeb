package com.tipray.dao;

import com.tipray.bean.baseinfo.Lock;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * LockDao
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@MyBatisAnno
public interface LockDao extends BaseDao<Lock> {
    /**
     * 根据车辆ID获取锁信息
     *
     * @param carId 车辆ID
     * @return 锁信息
     */
    List<Lock> findLocksByCarId(Long carId);

    /**
     * 根据车辆ID获取已触发绑定的锁信息
     *
     * @param carId 车辆ID
     * @return 锁信息
     */
    List<Lock> findTriggedLocksByCarId(Long carId);

    /**
     * 根据车辆ID获取锁最大索引号
     *
     * @param carId车辆ID
     * @return 锁最大索引号
     */
    Integer getMaxLockIndexByCarId(Long carId);

    /**
     * 获取未使用的锁设备ID列表
     *
     * @return 未使用的锁设备ID列表
     */
    List<Integer> findUnusedLocks();

    /**
     * 根据车辆ID和锁设备ID获取锁信息
     *
     * @param carId  车辆ID
     * @param lockId 锁设备ID
     * @return
     */
    Lock getByLockId(@Param("carId") Long carId, @Param("lockId") Integer lockId);

    /**
     * 锁绑定
     *
     * @param locks {@link List} {@link Lock}
     */
    void bindLocks(List<Lock> locks);

    /**
     * 根据车牌号获取待绑定锁设备ID列表
     *
     * @param carNumber 车牌号
     * @return
     */
    List<Integer> findBindingLockDeviceIds(String carNumber);

    /**
     * 根据锁信息列表查询相关锁状态信息（锁信息中锁ID必须存在）
     *
     * @param locks
     * @return
     */
    List<Map<String, Object>> findLockStatusByLocks(List<Lock> locks);

    /**
     * 根据待绑定锁信息获取车辆id
     *
     * @param lock
     * @return
     */
    Long getVehicleIdByLock(Lock lock);

    /**
     * 根据待绑定锁信息集合获取车辆id集合
     *
     * @param locks
     * @return
     */
    List<Lock> findVehicleIdByLocks(List<Lock> locks);

    /**
     * 根据车牌号获取锁及其状态信息
     *
     * @param carNumber
     * @return 车牌号
     */
    List<Map<String, Object>> findlocksByCarNo(String carNumber);

    /**
     * 获取锁开关状态
     * @param map 锁信息
     * @return
     */
    Integer getLockStatus(Map<String , Object> map);

    /**
     * 根据锁自增id获取锁信息
     * @param id 锁自增id
     * @return 锁信息
     */
    Map<String, Integer> getByIdForAppAlarm(Long id);

    /**
     * 批量更新锁备注
     * @param locks
     */
    void updateLockRemarks(List<Lock> locks);

    /**
     * 根据锁记录ID获取锁设备ID
     * @param id 锁记录ID
     * @return 锁设备ID
     */
    Integer getLockDeviceIdById(Integer id);

    /**
     * 根据锁设备ID和车辆ID获取锁记录ID
     * @param carId 车辆ID
     * @param devIds 锁设备ID，逗号分隔
     * @return 锁记录ID
     */
    List<Lock> findIdsByDevIds(@Param("carId") Long carId, @Param("devIds") String devIds);

    Map<String, Object> getLockByCarIdAndLockIndex(@Param("carId") Long carId, @Param("lockIndex") Integer lockIndex);

}