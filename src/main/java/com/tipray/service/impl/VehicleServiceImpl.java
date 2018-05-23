package com.tipray.service.impl;

import com.tipray.bean.*;
import com.tipray.bean.baseinfo.*;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.dao.*;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.VehicleService;
import com.tipray.util.DateUtil;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.StringUtil;
import com.tipray.util.VehicleAlarmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * 车辆管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("carService")
public class VehicleServiceImpl implements VehicleService {
	private final Logger logger = LoggerFactory.getLogger(VehicleServiceImpl.class);
	@Resource
	private VehicleDao vehicleDao;
	@Resource
	private DriverDao driverDao;
	@Resource
	private LockDao lockDao;
	@Resource
	private DeviceDao deviceDao;
	@Resource
	private DistributionRecordDao distributionRecordDao;
	@Resource
	private NioUdpServer udpServer;

	@Override
	public Vehicle addCar(Vehicle car, String driverIds) {
		if (car != null) {
			vehicleDao.add(car);
			updateDrivers(driverIds, car.getCarNumber());
		}
		return car;
	}

	@Override
	public Vehicle updateCar(Vehicle car, String driverIds) {
		if (car != null) {
			Long id = car.getId();
			Integer terminalId = vehicleDao.getTerminalIdById(id);
			Long oldTransportCardId = vehicleDao.getTransportCardIdById(id);
			Long newTransportCardId = car.getTransportCard().getTransportCardId();
			vehicleDao.update(car);
			updateDrivers(driverIds, car.getCarNumber());
			if (terminalId != null && terminalId != 0 && !oldTransportCardId.equals(newTransportCardId)) {
				ByteBuffer src = SendPacketBuilder.buildProtocol0x1203(terminalId, newTransportCardId);
				boolean isSend = udpServer.send(src);
				if (!isSend) {
					logger.error("UDP发送数据异常！");
				}
				addTimeoutTask(src);
			}
		}
		return car;
	}
	
	/**
	 * 添加超时任务
	 * 
	 * @param src
	 *            {@link ByteBuffer} 待发送数据包
	 * @param cacheId
	 *            {@link Integer} 缓存ID
	 */
	private void addTimeoutTask(ByteBuffer src) {
		short serialNo = SerialNumberCache.getSerialNumber(UdpBizId.TRANSPORT_CARD_UPDATE_REQUEST);
		int cacheId = AsynUdpCommCache.buildCacheId(UdpBizId.TRANSPORT_CARD_UPDATE_REQUEST, serialNo);
		new TimeOutTask(src, cacheId).executeInfoIssueTask();
	}
	@Override
	public void deleteCar(Long id, String carNumber) {
		vehicleDao.delete(id);
		driverDao.clearCar(carNumber);
	}

	@Override
	public Map<String, Object> getCarById(Long id) {
		if (id != null) {
			Vehicle car = vehicleDao.getById(id);
			List<Driver> drivers = driverDao.findByCarNo(car.getCarNumber());
			List<Lock> locks = lockDao.findLocksByCarId(id);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("car", car);
			map.put("drivers", drivers);
			map.put("locks", locks);
			return map;
		}
		return null;
	}

	@Override
	public Vehicle getByCarNo(String carNo) {
		return vehicleDao.getByCarNo(carNo);
	}

	@Override
	public Vehicle getCarByTerminalId(Integer terminalId) {
		return vehicleDao.getCarByTerminalId(terminalId);
	}

	@Override
	public List<Vehicle> findByTransCompany(TransCompany transCompany) {
		return vehicleDao.findByTransCompany(transCompany);
	}

	@Override
	public List<Vehicle> findAllCars() {
		return vehicleDao.findAll();
	}

	@Override
	public long countCar(Vehicle car) {
		return vehicleDao.count(car);
	}

	@Override
	public List<Vehicle> findByPage(Vehicle car, Page page) {
		return vehicleDao.findByPage(car, page);
	}

	@Override
	public GridPage<Vehicle> findCarsForPage(Vehicle car, Page page) {
		long records = countCar(car);
		List<Vehicle> list = findByPage(car, page);
		return new GridPage<Vehicle>(list, records, page.getPageId(), page.getRows(), list.size(), car);
	}

	@Override
	public List<DropdownData> selectCars() {
		return vehicleDao.selectCars();
	}

	@Override
	public List<Device> findUnusedTerminal() {
		return vehicleDao.findUnusedTerminal();
	}

	@Override
	public void terminalBind(String carNumber, Integer terminalId) {
		vehicleDao.terminalBind(carNumber, terminalId);
	}

	@Override
	public void terminalUnbind(String carNumber, Integer terminalId) {
		vehicleDao.terminalUnbind(carNumber);
		deviceDao.deleteByDeviceId(terminalId);
	}

	@Override
	public List<Vehicle> findBindedCars() {
		return vehicleDao.findBindedCars();
	}

	@Override
	public List<Vehicle> findUnbindedCars() {
		return vehicleDao.findUnbindedCars();
	}

	@Override
	public List<Integer> findUnusedLocks() {
		return lockDao.findUnusedLocks();
	}

	@Override
	public VehicleStatus getCarStatus(String carNumber) {
		return vehicleDao.getCarStatus(carNumber);
	}

	@Override
	public List<ChangeInfo> getDistributionsByCarNumber(String carNumber) {
		return vehicleDao.getDistributionsByCarNumber(carNumber);
	}

	@Override
	public ChangeInfo getDistributionByTransportId(Long transportId, Long changedStationId) {
		return vehicleDao.getDistributionByTransportId(transportId, changedStationId);
	}

	@Override
	public List<VehicleTrack> findTracks(VehicleTrack carTrack) {
	    long start =System.currentTimeMillis();
		List<VehicleTrack> carTracks = vehicleDao.findTracks(carTrack);
		long end =System.currentTimeMillis();
        long d = (end - start)/DateUtil.SECOND_DIFF;
        System.out.println("track cost ==> " + d);
		// carTracks.parallelStream().forEach(track -> setAlarm(track));
		return carTracks;
	}

	private void updateDrivers(String driverIds, String carNumber) {
		driverDao.clearCar(carNumber);
		if (StringUtil.isNotEmpty(driverIds)) {
			driverDao.setCar(driverIds, carNumber);
		}
	}

	private void setAlarm(VehicleTrack carTrack) {
		byte terminalAlarm = carTrack.getTerminalAlarmStatus().byteValue();
		byte[] lockStatusInfo = carTrack.getLockStatusInfo();
		carTrack.setAlarm(VehicleAlarmUtil.isAlarm(terminalAlarm, lockStatusInfo));
		carTrack.setAlarmMap(VehicleAlarmUtil.getLockAlarmMap(lockStatusInfo));
	}

	@Override
	public Integer getTerminalIdById(long id) {
		return vehicleDao.getTerminalIdById(id);
	}

	@Override
	public String getCarNumberById(long id) {
		return vehicleDao.getCarNumberById(id);
	}

	@Override
	public void addCars(List<Vehicle> vehicles) {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminalConfig(VehicleTerminalConfig terminalConfig) {
		vehicleDao.terminalConfig(terminalConfig);
	}

	@Override
	public Integer terminalEnable(Integer deviceId, Integer functionEnable) {
		Map<String, Integer> map = new HashMap<>();
		map.put("deviceId", deviceId);
		map.put("functionEnable", functionEnable);
		vehicleDao.terminalEnable(map);
		// mybatis 插入数据后获取的自增主键为Long类型
		Object id = map.get("id");
		return ((Long) id).intValue();
	}

	@Override
	public List<Lock> findLocksByCarId(Long carId) {
		return lockDao.findLocksByCarId(carId);
	}

	@Override
	public void bindLocks(List<Lock> locks) {
		lockDao.bindLocks(locks);
	}

	@Override
	public List<Integer> findBindingLockDeviceIds(Long carId) {
		return lockDao.findBindingLockDeviceIds(carId);
	}

	@Override
	public List<Lock> findVehicleIdByLocks(List<Lock> locks) {
		return lockDao.findVehicleIdByLocks(locks);
	}
	
	@Override
	public Integer addRemoteControlRecord(Map<String, Object> map) {
		vehicleDao.addRemoteControlRecord(map);
		// Integer type = (Integer) map.get("type");
		// Long carId = (Long) map.get("carId");
		// Integer remoteControlId =
		// vehicleDao.getRemoteControlIdByCarIdAndControlType(carId, type);
		Integer remoteControlId = ((Long) map.get("id")).intValue();
		return remoteControlId;
	}

	@Override
	public void updateRemoteControlStatus(Integer remoteControlStatus, Integer remoteControlId) {
		vehicleDao.updateRemoteControlStatus(remoteControlStatus, remoteControlId);
	}

	@Override
	public void updateRemoteAlterStatusResulte(Integer remoteControlStatus, Integer carStatus, Integer remoteControlId,
			Long carId) {
		vehicleDao.updateRemoteControlStatus(remoteControlStatus, remoteControlId);
		vehicleDao.remoteAlterCarStatus(carStatus, remoteControlId, carId);
	}

	@Override
	public Map<String, Object> getCarAndLockByCarNo(String carNumber) {
		Vehicle vehicle = vehicleDao.getByCarNo(carNumber);
		List<Driver> drivers = new ArrayList<>();
		List<Lock> locks = new ArrayList<>();
		List<Map<String, Object>> lockStatus = new ArrayList<>();
		if (vehicle != null) {
			locks = lockDao.findLocksByCarId(vehicle.getId());
			drivers = driverDao.findByCarNo(vehicle.getCarNumber());
		}
		if (!EmptyObjectUtil.isEmptyList(locks)) {
			lockStatus = lockDao.findLockStatusByLocks(locks);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("vehicle", vehicle);
		map.put("drivers", drivers);
		map.put("locks", locks);
		map.put("lockStatus", lockStatus);
		return map;
	}

	@Override
	public Long addLockResetRecord(Map<String, Object> map) {
		return vehicleDao.addLockResetRecord(map);
	}

	@Override
	public void batchUpdateResetRecord(String resetIds, Integer resetState) {
		vehicleDao.batchUpdateResetRecord(resetIds, resetState);
	}

	@Override
	public List<Map<String, Object>> findTracksByCarNumbers(String carNumbers, String beginTime) {
		// 替换大部分空白字符， 不限于空格 \s 可以匹配空格、制表符、换页符等空白字符的其中任意一个
		carNumbers = carNumbers.replaceAll("\\s*", "");
		if (carNumbers.endsWith(",")) {
			// 去除末尾多余逗号
			carNumbers = carNumbers.substring(0, carNumbers.length() - 1);
		}
		carNumbers = carNumbers.replaceAll(",", "\',\'");
		StringBuffer carNumberBuf = new StringBuffer().append('\'').append(carNumbers).append('\'');
		return vehicleDao.findTracksByCarNumbers(carNumberBuf.toString(), beginTime);
	}

	@Override
	public List<Map<String, Object>> findBindedVehicleTree() {
		return vehicleDao.findBindedVehicleTree();
	}

    @Override
    public VehicleRealtimeStatus getVehicleRealtimeStatus(Long vehicleId) {
        return vehicleId == null ? null : vehicleDao.getVehicleRealtimeStatus(vehicleId);
    }

}
