package com.tipray.test;

import com.tipray.bean.ChangeInfo;
import com.tipray.bean.VehicleTerminalConfig;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.baseinfo.TransportCard;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.record.AlarmRecord;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.AlarmRecordDao;
import com.tipray.dao.DistributionRecordDao;
import com.tipray.dao.LockDao;
import com.tipray.dao.VehicleManageLogDao;
import com.tipray.service.VehicleService;
import com.tipray.util.JSONUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class CarTest {
	@Resource
	private VehicleService carService;
	@Resource
	private DistributionRecordDao distributionRecordDao;
	@Resource
	private VehicleManageLogDao vehicleManageLogDao;
	@Resource
	private LockDao lockDao;
	@Resource
	private AlarmRecordDao alarmRecordDao;

	@Test
    public void alarm(){
	    List<AlarmRecord> list = alarmRecordDao.getAlarmRecordsByIdsAndCar("1,2,3",1L);
      int i=  alarmRecordDao.countAlarmDeviceByIds("1,2,3");
        System.out.println(i);
    }

	@Test
    public void updateEli(){
	    alarmRecordDao.updateEliminateStatus(30,0);
    }
	
	@Test
	public void checkLock() {
		List<Lock> list = new ArrayList<>();
		Lock lock1 = new Lock();
		lock1.setLockId(33554436);
		lock1.setStoreId(1);
		lock1.setSeat(1);
		lock1.setSeatIndex(1);
		lock1.setAllowOpen(2);
		Lock lock2 = new Lock();
		lock2.setLockId(33554437);
		lock2.setStoreId(2);
		lock2.setSeat(1);
		lock2.setSeatIndex(1);
		lock2.setAllowOpen(1);
		list.add(lock1);
		list.add(lock2);
		List<Lock> list2 = lockDao.findVehicleIdByLocks(list);
		
	}
	
	@Test
	public void testTerminal() throws Exception {
		String cs = JSONUtil.stringify(carService.findBindedVehicleTree());
		System.out.println(cs);
//		try {
//			int id = carService.terminalEnable(0, 1);
//			System.out.println(id);
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	@Test
	public void testlog() {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", 1);
		map.put("isApp", 0);
		map.put("udpBizId", 4610);
//		map.put("requestTime", "2018-04-23 15:35:39");
		map.put("token", "fbd097fa-be69-4fa2-b604-59c8e50f36e3");
		Map<String, Object> list =  vehicleManageLogDao.findUdpResult(map);
		System.out.println(list);
	}
	
	@Test
	public void change() {
		ChangeInfo changeInfo = new ChangeInfo();
		changeInfo.setInvoice("3G00-1804111132");
		changeInfo.setCarId(1L);
		changeInfo.setStoreId(1);
		changeInfo.setOildepotId(1L);
		changeInfo.setChangedGasstationId(120L);
		distributionRecordDao.addByChangeInfo(changeInfo);
		System.out.println(changeInfo);
	}
	
	@Test
	public void lockRest() {
		Map<String, Object> map = new HashMap<>();
		map.put("carId", 1);
		map.put("lockId", 33554437);
		map.put("userId", 5);
		map.put("isApp", 1);
		map.put("isLocationValid", 0);
		map.put("longitude", 0);
		map.put("latitude", 0);
		Map<String, Object> map2 = new HashMap<>();
		map2.put("carId", 1);
		map2.put("lockId", 33554437);
		map2.put("userId", 6);
		map2.put("isApp", 0);
		map2.put("isLocationValid", 0);
		map2.put("longitude", 0);
		map2.put("latitude", 0);
		try {
			Long resetId = carService.addLockResetRecord(map);
			Long resetId2 = carService.addLockResetRecord(map2);
			System.out.println(resetId);
			System.out.println(resetId2);
			int id = ((Long) map.get("id")).intValue();
			System.out.println(id);
			System.out.println(map2.get("id"));
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void lock() throws Exception {
		List<Lock> list = carService.findLocksByCarId(1l);
		String json = JSONUtil.stringify(list);
		System.out.println(json);
	}
	@Test
	public void checkDistribute() {
		distributionRecordDao.checkDistribute(1l, 1);
	}

	@Test
	public void addRemoteControlRecord() {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", 4l);
		map.put("carId", 6l);
		map.put("type", 2);
		map.put("stationId", 0);
		map.put("isApp", 0);
		map.put("longitude", 0);
		map.put("latitude", 0);
		try {
			System.out.println(carService.addRemoteControlRecord(map));
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void add() {
		Vehicle car = new Vehicle();
		car.setCarNumber("闽D 12345");
		TransCompany transCompany = new TransCompany();
		transCompany.setId(1L);
		car.setTransCompany(transCompany);
		car.setType(1);
		TransportCard transportCard = new TransportCard();
		transportCard.setTransportCardId(123L);
		car.setTransportCard(transportCard);
		car.setStoreNum(3);
		String driverIds = "2,3";
		try {
			carService.addCar(car, driverIds);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void config() {
		VehicleTerminalConfig config = new VehicleTerminalConfig();
		config.setScope(0);
		config.setCarNumber("");
		config.setScanInterval(5);
		config.setUploadInterval(30);
		try {
			carService.terminalConfig(config);
			System.out.println(config.getCarState()+"\n"+config.getIsUpdate());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
}
