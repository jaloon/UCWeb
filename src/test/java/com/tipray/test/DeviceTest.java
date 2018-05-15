package com.tipray.test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tipray.bean.baseinfo.Device;
import com.tipray.core.GridProperties;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.DeviceService;

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

	@Test
	public void getCenterByProperties() {
		try {
			Properties properties = new Properties();
			properties.load(GridProperties.class.getClassLoader().getResourceAsStream("center.properties"));
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
		device.setProduceTime(new Date());
		device.setDeliveryTime(new Date());

		Device device1 = new Device();
		device1.setDeviceId(2);
		device1.setType(1);
		device1.setCenterId(1);
		device1.setProduceTime(new Date());
		device1.setDeliveryTime(new Date());

		Device device2 = new Device();
		device2.setDeviceId(3);
		device2.setType(3);
		device2.setCenterId(3);
		device2.setProduceTime(new Date());
		device2.setDeliveryTime(new Date());

		Device device3 = new Device();
		device3.setDeviceId(4);
		device3.setType(4);
		device3.setCenterId(3);
		device3.setProduceTime(new Date());
		device3.setDeliveryTime(new Date());

		Device device4 = new Device();
		device4.setDeviceId(5);
		device4.setType(5);
		device4.setCenterId(4);
		device4.setProduceTime(new Date());
		device4.setDeliveryTime(new Date());

		Device device5 = new Device();
		device5.setDeviceId(6);
		device5.setType(2);
		device5.setCenterId(1);
		device5.setProduceTime(new Date());
		device5.setDeliveryTime(new Date());

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
		device.setProduceTime(new Date());
		device.setDeliveryTime(new Date());
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