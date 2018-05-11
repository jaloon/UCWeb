package com.tipray.dao;

import java.util.List;

import com.tipray.bean.baseinfo.Device;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * DeviceDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface DeviceDao extends BaseDao<Device> {

	/**
	 * 根据用户中心ID获取设备列表
	 * 
	 * @param centerId
	 * @return
	 */
    List<Device> findByCenterId(Long centerId);

	/**
	 * 根据设备类型获取设备列表
	 * 
	 * @param deviceType
	 * @return
	 */
    List<Device> findByType(Integer deviceType);

	/**
	 * 根据设备ID获取设备信息
	 * 
	 * @param deviceId
	 * @return
	 */
    Device getByDeviceId(Integer deviceId);

	/**
	 * 删除所有设备
	 */
    void deleteAll();

	/**
	 * 批量添加设备
	 * 
	 * @param devices
	 */
    void addDevices(List<Device> devices);

	/**
	 * 根据设备ID删除设备
	 * 
	 * @param deviceId
	 */
    void deleteByDeviceId(Integer deviceId);

}
