package com.tipray.test;

import com.tipray.bean.baseinfo.AppSync;
import com.tipray.bean.baseinfo.Device;
import com.tipray.core.GridProperties;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.AppService;
import com.tipray.service.DeviceService;
import com.tipray.util.JSONUtil;
import com.tipray.util.OkHttpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * 设备管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class DeviceTest {
	@Resource
	private DeviceService deviceService;
	@Resource
    private AppService appService;

	@Test
	public void appSync() {
        try {
			String json = OkHttpUtil.get("https://192.168.7.20:3003/api/appSync.do?id=1");
			// String json = OkHttpUtil.get("https://www.pltone.com:3003/api/appSync.do?id=1");
            AppSync appsync = JSONUtil.parseToObject(json, AppSync.class);
            appService.sync(appsync);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Test
	public void getCenterByProperties() {
		try {
			Properties properties = new Properties();
			properties.load(GridProperties.class.getClassLoader().getResourceAsStream("center-constant.properties"));
			Long id = Long.valueOf(properties.getProperty("center.id"));
			String name = properties.getProperty("center.name");
			System.out.println(id + name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void addDevice() {
		Device device = new Device();
		device.setDeviceId(1);
		device.setType(2);
		device.setCenterId(2);

		Device device1 = new Device();
		device1.setDeviceId(2);
		device1.setType(1);
		device1.setCenterId(1);

		Device device2 = new Device();
		device2.setDeviceId(3);
		device2.setType(3);
		device2.setCenterId(3);

		Device device3 = new Device();
		device3.setDeviceId(4);
		device3.setType(4);
		device3.setCenterId(3);

		Device device4 = new Device();
		device4.setDeviceId(5);
		device4.setType(5);
		device4.setCenterId(4);

		Device device5 = new Device();
		device5.setDeviceId(6);
		device5.setType(2);
		device5.setCenterId(1);

		try {
			deviceService.addDevice(device);
			deviceService.addDevice(device1);
			deviceService.addDevice(device2);
			deviceService.addDevice(device3);
			deviceService.addDevice(device4);
			deviceService.addDevice(device5);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void updateDevice() {
		Device device = new Device();
		device.setId(4L);
		device.setType(2);
		device.setCenterId(2);
		try {
			deviceService.updateDevice(device);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void findAllDevices() {
		List<Device> dlist = deviceService.findAllDevices();
		System.out.println();
		for (Device device : dlist) {
			System.out.println(device.getId() + "：" + device);
		}
		System.out.println();
	}

	@Test
	public void deleteUser() {
		try {
			deviceService.deleteDeviceById(1L);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getById() {
		System.out.println(deviceService.getDeviceById(3L));
	}

	@Test
	public void findByType() {
		List<Device> dlist = deviceService.findByType(2);
		System.out.println();
		for (Device device : dlist) {
			System.out.println(device.getId() + "：" + device);
		}
		System.out.println();
	}

}
