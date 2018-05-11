package com.tipray.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Device;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.DeviceDao;
import com.tipray.service.DeviceService;
import com.tipray.util.DateUtil;

/**
 * 设备管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("deviceService")
public class DeviceServiceImpl implements DeviceService {
	@Resource
	private DeviceDao deviceDao;

	@Override
	public Device addDevice(Device device) {
		if (device != null) {
			device = setTime(device);
			deviceDao.add(device);
		}
		return device;
	}

	@Override
	public Device updateDevice(Device device) {
		if (device != null) {
			device = setTime(device);
			deviceDao.update(device);
		}
		return device;
	}

	@Override
	public void deleteDeviceById(Long id) {
		deviceDao.delete(id);
	}

	@Override
	public Device getDeviceById(Long id) {
		Device device = id == null ? null : deviceDao.getById(id);
		return device;
	}

	@Override
	public List<Device> findAllDevices() {
		List<Device> devices = deviceDao.findAll();
		return devices;
	}

	@Override
	public List<Device> findByCenterId(Long centerId) {
		List<Device> devices = centerId == null ? null : deviceDao.findByCenterId(centerId);
		return devices;
	}

	@Override
	public List<Device> findByType(Integer deviceType) {
		List<Device> devices = deviceType == null ? null : deviceDao.findByType(deviceType);
		return devices;
	}

	@Override
	public long countDevice(Device device) {
		return deviceDao.count(device);
	}

	@Override
	public List<Device> findByPage(Device device, Page page) {
		List<Device> devices = deviceDao.findByPage(device, page);
		return devices;
	}

	@Override
	public GridPage<Device> findDeviceForPage(Device device, Page page) {
		long records = countDevice(device);
		List<Device> list = findByPage(device, page);
		return new GridPage<Device>(list, records, page.getPageId(), page.getRows(), list.size(), device);
	}

	@Override
	public Device getDeviceByDeviceId(Integer deviceId) {
		return deviceDao.getByDeviceId(deviceId);
	}

	public Device setTime(Device device) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		device.setProduceTime(DateUtil.convertDateStrToDate(device.getProduce(), pattern));
		device.setDeliveryTime(DateUtil.convertDateStrToDate(device.getDelivery(), pattern));
		return device;
	}

	@Override
	public List<Device> sync(List<Device> devices) {
		if (!devices.isEmpty()) {
			deviceDao.deleteAll();
			deviceDao.addDevices(devices);
		}
		return devices;
	}

}
