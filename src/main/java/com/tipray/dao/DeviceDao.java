package com.tipray.dao;

import com.tipray.bean.baseinfo.Device;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * 更新设备使用状态
     * @param deviceId 设备ID
     * @param inUSe 0 未使用，1 使用中
     */
	void updateDeviceUse(@Param("deviceId") Integer deviceId, @Param("inUSe") Integer inUSe);

    /**
     * 更新设备使用状态
     * @param deviceIds 设备ID，英文逗号“,”分隔
     * @param inUSe 0 未使用，1 使用中
     */
	void updateDevicesUse(@Param("deviceIds") String deviceIds, @Param("inUSe") Integer inUSe);

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
	 * @param deviceId 设备ID
	 */
    void deleteByDeviceId(Integer deviceId);

    /**
     * 获取所有设备ID
     * @return
     */
    List<Integer> findAllDeviceIds();

    /**
     * 根据设备id批量删除设备
     * @param deviceIds 待删除的设备ID
     */
    void deleteByDeviceIds(String deviceIds);

    /**
     * 根据设备id批量删除设备
     * @param deviceIds 待删除的设备ID
     */
    void deleteByDeviceIdList(List<Integer> deviceIds);

    /**
     * 批量更新设备
     * @param devices
     */
    void updateDevices(List<Device> devices);
}
