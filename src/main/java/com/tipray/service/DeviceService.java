package com.tipray.service;

import java.util.List;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Device;
import com.tipray.core.exception.ServiceException;

/**
 * DeviceService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface DeviceService {
	/**
	 * 新增设备
	 * 
	 * @param device
	 * @throws ServiceException
	 */
    Device addDevice(Device device) throws ServiceException;

	/**
	 * 修改设备信息
	 * 
	 * @param device
	 */
    Device updateDevice(Device device) throws ServiceException;

	/**
	 * 根据Id删除设备
	 * 
	 * @param id
	 */
    void deleteDeviceById(Long id) throws ServiceException;

	/**
	 * 根据Id获取设备信息
	 * 
	 * @param id
	 * @return
	 */
    Device getDeviceById(Long id);

	/**
	 * 获取所有的设备信息
	 * 
	 * @param
	 * @return
	 */
    List<Device> findAllDevices();

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
	 * @param type
	 * @return
	 */
    List<Device> findByType(Integer deviceType);

	/**
	 * 获取设备数量
	 * 
	 * @return
	 */
    long countDevice(Device device);

	/**
	 * 分页查询设备列表
	 * 
	 * @param device
	 * @param page
	 * @return
	 */
    List<Device> findByPage(Device device, Page page);

	/**
	 * 分页查询设备列表
	 * 
	 * @param device
	 * @param page
	 * @return
	 */
    GridPage<Device> findDeviceForPage(Device device, Page page);

	/**
	 * 根据设备ID获取设备信息
	 * 
	 * @param deviceId
	 * @return
	 */
    Device getDeviceByDeviceId(Integer deviceId);

	/**
	 * 从普利通中心同步设备信息
	 * 
	 * @param Devices
	 * @return
	 */
    List<Device> sync(List<Device> devices) throws ServiceException;

}
