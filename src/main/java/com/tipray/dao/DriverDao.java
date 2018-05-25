package com.tipray.dao;

import com.tipray.bean.baseinfo.Driver;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * DriverDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface DriverDao extends BaseDao<Driver> {
	/**
	 * 根据车牌号获取司机信息
	 * 
	 * @param carNo
	 *            {@link String} 车牌号
	 * @return {@link List} 司机信息
	 */
    List<Driver> findByCarNo(String carNo);

    /**
     * 根据车牌号获取司机信息
     * @param carNo   {@link String} 车牌号
     * @return {@link Map} [{name, phone}, {name2, phone2}]
     */
    List<Map<String, Object>> findByCarNoForApp(String carNo);

	/**
	 * 获取空闲司机列表
	 * 
	 * @return {@link List} 空闲司机列表
	 */
    List<Driver> findFreeDrivers();

	/**
	 * 批量设置司机所属车辆
	 * 
	 * @param driverIds
	 *            {@link String} 司机ID集合，例： 1,2
	 * @param carNumber
	 *            {@link String} 车牌号
	 */
    void setCar(@Param("driverIds") String driverIds, @Param("carNumber") String carNumber);

	/**
	 * 批量设置司机所属车辆
	 * 
	 * @param drivers
	 *            {@link Driver} 司机信息列表
	 */
    void setCarByList(List<Driver> drivers);

	/**
	 * 根据车牌号清除车辆信息
	 * 
	 * @param carNumber
	 *            {@link String} 车牌号
	 */
    void clearCar(String carNumber);
}
