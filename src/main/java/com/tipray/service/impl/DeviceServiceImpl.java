package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Device;
import com.tipray.dao.DeviceDao;
import com.tipray.service.DeviceService;
import com.tipray.util.DateUtil;
import com.tipray.util.EmptyObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

	private Device setTime(Device device) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		device.setProduceTime(DateUtil.convertDateStrToDate(device.getProduce(), pattern));
		device.setDeliveryTime(DateUtil.convertDateStrToDate(device.getDelivery(), pattern));
		return device;
	}

	@Override
	public List<Device> sync(List<Device> devices) {
		if (!EmptyObjectUtil.isEmptyList(devices)) {
            List<Integer> dbDeviceIds = deviceDao.findAllDeviceIds();
            if (EmptyObjectUtil.isEmptyList(dbDeviceIds)) {
                deviceDao.addDevices(devices);
            } else {
                List<Device> adds = new ArrayList<>();
                List<Device> upds = new ArrayList<>();
                for (Device device : devices) {
                    Integer deviceId = device.getDeviceId();
                    if (dbDeviceIds.contains(deviceId)) {
                        upds.add(device);
                        dbDeviceIds.remove(deviceId);
                    } else {
                        adds.add(device);
                    }
                }
                if (adds.size() > 0) {
                    deviceDao.addDevices(adds);
                }
                if (upds.size() > 0) {
                    deviceDao.updateDevices(upds);
                }
                if (dbDeviceIds.size() > 0) {
                    deviceDao.deleteByDeviceIdList(dbDeviceIds);
                }
            }
		}
		return devices;
	}

}
