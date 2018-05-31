package com.tipray.dao;

import com.tipray.bean.baseinfo.Lock;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * LockDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface LockDao extends BaseDao<Lock> {
	/**
	 * 根据车辆ID获取锁信息
	 * 
	 * @param carId
	 * @return
	 */
    List<Lock> findLocksByCarId(Long carId);

	/**
	 * 获取未使用的锁设备ID列表
	 * 
	 * @return
	 */
    List<Integer> findUnusedLocks();

	/**
	 * 根据锁ID获取锁信息
	 * 
	 * @param lockId
	 * @return
	 */
    Lock getByLockId(Integer lockId);

	/**
	 * 锁绑定
	 * 
	 * @param locks
	 *            {@link List} {@link Lock}
	 */
    void bindLocks(List<Lock> locks);

	/**
	 * 根据车辆ID获取待绑定锁设备ID列表
	 * 
	 * @param carId
	 * @return
	 */
    List<Integer> findBindingLockDeviceIds(Long carId);

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
     * @param carNumber
     * @return 车牌号
     */
    List<Map<String,Object>> findlocksByCarNo(String carNumber);
}
