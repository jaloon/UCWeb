package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Device;
import com.tipray.dao.DeviceDao;
import com.tipray.service.DeviceService;
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
@Service("deviceService")
public class DeviceServiceImpl implements DeviceService {
	@Resource
	private DeviceDao deviceDao;

	@Transactional
	@Override
	public Device addDevice(Device device) {
		if (device != null) {
			deviceDao.add(device);
		}
		return device;
	}

    @Transactional
	@Override
	public Device updateDevice(Device device) {
		if (device != null) {
			deviceDao.update(device);
		}
		return device;
	}

    @Transactional
	@Override
	public void deleteDeviceById(Long id) {
		deviceDao.delete(id);
	}

	@Override
	public Device getDeviceById(Long id) {
		return id == null ? null : deviceDao.getById(id);
	}

	@Override
	public List<Device> findAllDevices() {
		return deviceDao.findAll();
	}

	@Override
	public List<Device> findByCenterId(Long centerId) {
		return centerId == null ? null : deviceDao.findByCenterId(centerId);
	}

	@Override
	public List<Device> findByType(Integer deviceType) {
		return deviceType == null ? null : deviceDao.findByType(deviceType);
	}

	@Override
	public long countDevice(Device device) {
		return deviceDao.count(device);
	}

	@Override
	public List<Device> findByPage(Device device, Page page) {
		return deviceDao.findByPage(device, page);
	}

	@Override
	public GridPage<Device> findDeviceForPage(Device device, Page page) {
		long records = countDevice(device);
		List<Device> list = findByPage(device, page);
		return new GridPage<>(list, records, page, device);
	}

	@Override
	public Device getDeviceByDeviceId(Integer deviceId) {
		return deviceDao.getByDeviceId(deviceId);
	}

    @Transactional
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
				if (dbDeviceIds.size() > 0) {
					deviceDao.deleteByDeviceIdList(dbDeviceIds);
				}
                if (adds.size() > 0) {
                    deviceDao.addDevices(adds);
                }
                if (upds.size() > 0) {
                    deviceDao.updateDevices(upds);
                }
            }
		}
		return devices;
	}
}
