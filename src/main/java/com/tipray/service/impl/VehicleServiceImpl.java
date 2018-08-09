package com.tipray.service.impl;

import com.tipray.bean.*;
import com.tipray.bean.baseinfo.*;
import com.tipray.bean.track.LastCarStatus;
import com.tipray.bean.track.LastTrack;
import com.tipray.bean.track.ReTrack;
import com.tipray.bean.track.TrackInfo;
import com.tipray.bean.upgrade.TerminalUpgradeInfo;
import com.tipray.bean.upgrade.TerminalUpgradeRecord;
import com.tipray.bean.upgrade.UpgradeCancelVehicle;
import com.tipray.bean.upgrade.VehicleTree;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.dao.*;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.VehicleService;
import com.tipray.util.*;
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
 */
@Transactional(rollbackForClassName = {"ServiceException", "Exception"})
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
    private TrackDao trackDao;
    @Resource
    private TerminalUpgradeDao terminalUpgradeDao;
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
    public Vehicle updateCar(Vehicle car, String driverIds, List<Lock> locks) {
        if (car != null) {
            Long id = car.getId();
            Integer terminalId = vehicleDao.getTerminalIdById(id);
            Long oldTransportCardId = vehicleDao.getTransportCardIdById(id);
            Long newTransportCardId = car.getTransportCard().getTransportCardId();
            vehicleDao.update(car);
            updateDrivers(driverIds, car.getCarNumber());
            lockDao.updateLockRemarks(locks);
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
     * @param src     {@link ByteBuffer} 待发送数据包
     * @param cacheId {@link Integer} 缓存ID
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
    public Long getIdByCarNo(String carNo) {
        return vehicleDao.getIdByCarNo(carNo);
    }

    @Override
    public Integer getStoreNumByCarNo(String carNo) {
        return vehicleDao.getStoreNumByCarNo(carNo);
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
    public Map<String, Object> selectCars(int scope, long comId) {
        List<Map<String, Object>> coms;
        List<Map<String, Object>> carNumbers;
        switch (scope) {
            case 0:
                coms = vehicleDao.findComsWithCar(comId);
                carNumbers = vehicleDao.findCarNumbers(comId);
                break;
            case 1:
                coms = vehicleDao.findComsWithBindedCar(comId);
                carNumbers = vehicleDao.findBindedCarNumbers(comId);
                break;
            case 2:
                coms = vehicleDao.findComsWithOnlineCar(comId);
                carNumbers = vehicleDao.findOnlineCarNumbers(comId);
                break;
            default:
                return null;
        }
        StringBuffer comBuf = new StringBuffer();
        for (Map<String, Object> com : coms) {
            Long id = (Long) com.get("id");
            String name = (String) com.get("name");
            if (id == 0) {
                name = "未指定运输公司";
            }
            comBuf.append("<optgroup id=\"com_").append(id).append("\" label=\"").append(name).append("\"></optgroup>");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("com", comBuf.toString());
        map.put("car", carNumbers);
        return map;
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
    public Map<String, Object> findTracks(ReTrack carTrack) {
        String carNo = carTrack.getCarNumber();
        Long carId = vehicleDao.getIdByCarNo(carNo);
        if (carId == null) {
            return null;
        }
        List<TrackInfo> trackInfos = trackDao.findTracksByCarIdAndTimeRange(carId, carTrack.getBegin(), carTrack.getEnd());
        if (EmptyObjectUtil.isEmptyList(trackInfos)) {
            return null;
        }
        List<ReTrack> carTracks = new ArrayList<>();
        Set<Point> doubtablePoints = new HashSet<>();
        long preTrackTime = 0;
        Point prePoint = null;
        for (int i = 0, len = trackInfos.size(); i < len; i++) {
            TrackInfo trackInfo = trackInfos.get(i);
            ReTrack reTrack = new ReTrack();
            reTrack.setTrackId(trackInfo.getId());
            reTrack.setCoorValid(trackInfo.getCoorValid());
            reTrack.setLongitude(trackInfo.getLongitude());
            reTrack.setLatitude(trackInfo.getLatitude());
            reTrack.setVelocity(trackInfo.getSpeed());
            reTrack.setAngle(trackInfo.getAngle());
            reTrack.setAlarm(VehicleAlarmUtil.isAlarm(trackInfo.getTerminalAlarm().byteValue(), trackInfo.getLockStatusInfo()));
            reTrack.setCarStatus(trackInfo.getCarStatus());
            reTrack.setCreateDate(trackInfo.getTrackTime());

            long curTrackTime = trackInfo.getTrackTime().getTime();
            Point curPoint = new Point();
            curPoint.setLongitude(trackInfo.getLongitude());
            curPoint.setLatitude(trackInfo.getLatitude());
            if (i > 0) {
                long duration = curTrackTime - preTrackTime;
                // 两点间隔时间超过5分钟
                if (duration > DateUtil.MINUTE_DIFF * 5) {
                    preTrackTime = curTrackTime;
                    prePoint = curPoint;
                    reTrack.setDoubtable(true);
                    carTracks.add(reTrack);
                    doubtablePoints.add(curPoint);
                    continue;
                }
                // 两点坐标相同
                if (curPoint.equals(prePoint)) {
                    preTrackTime = curTrackTime;
                    prePoint = curPoint;
                    reTrack.setDoubtable(true);
                    carTracks.add(reTrack);
                    doubtablePoints.add(curPoint);
                    continue;
                }
            }
            preTrackTime = curTrackTime;
            prePoint = curPoint;

            // gps速度小于1m/s（即 3.6km/h）时，认为没有移动
            if (trackInfo.getSpeed() < 4) {
                reTrack.setDoubtable(true);
                doubtablePoints.add(curPoint);
            }

            carTracks.add(reTrack);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("tracks", carTracks);
        map.put("doubtable", doubtablePoints);
        return map;
    }

    /**
     * 更新司机信息
     *
     * @param driverIds 司机ID，逗号分隔
     * @param carNumber 车牌号
     */
    private void updateDrivers(String driverIds, String carNumber) {
        driverDao.clearCar(carNumber);
        if (StringUtil.isNotEmpty(driverIds)) {
            driverDao.setCar(driverIds, carNumber);
        }
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
    public VehicleTerminalConfig getGpsConfByTerminalId(Integer terminalId) {
        if (terminalId == null) {
            terminalId = 0;
        }
        return vehicleDao.getGpsConfByTerminalId(terminalId);
    }

    @Override
    public VehicleTerminalConfig getGpsConfByCarNumber(String carNumber) {
        if (StringUtil.isEmpty(carNumber)) {
            return vehicleDao.getGpsConfByTerminalId(0);
        }
        return vehicleDao.getGpsConfByCarNumber(carNumber);
    }

    @Override
    public Integer getFuncEnable() {
        Integer func = vehicleDao.getTerminalEnable();
        return func == null ? 0 : func;
    }

    @Override
    public void terminalEnable(Integer functionEnable) {
        Integer count = vehicleDao.countTerminalEnable();
        if (count == null || count == 0) {
            vehicleDao.addTerminalEnable(functionEnable);
        } else {
            vehicleDao.updateTerminalEnable(functionEnable);
        }
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
    public List<Integer> findBindingLockDeviceIds(String carNumber) {
        return lockDao.findBindingLockDeviceIds(carNumber);
    }

    @Override
    public List<Lock> findVehicleIdByLocks(List<Lock> locks) {
        return lockDao.findVehicleIdByLocks(locks);
    }

    @Override
    public Integer addRemoteControlRecord(Map<String, Object> map) {
        vehicleDao.addRemoteControlRecord(map);
        Integer remoteControlId = ((Long) map.get("id")).intValue();
        return remoteControlId;
    }

    @Override
    public Integer addRemoteStatusAlterRecord(Map<String, Object> map) {
        vehicleDao.addRemoteStatusAlterRecord(map);
        Integer remoteControlId = ((Long) map.get("id")).intValue();
        return remoteControlId;
    }

    @Override
    public void updateRemoteControlStatus(Integer remoteControlStatus, Integer remoteControlId) {
        vehicleDao.updateRemoteControlStatus(remoteControlStatus, remoteControlId);
    }

    @Override
    public Map<String, Object> getCarAndDriverByCarNo(String carNumber) {
        if (StringUtil.isEmpty(carNumber)) {
            return null;
        }
        Map<String, Object> vehicle = vehicleDao.getByCarNoForApp(carNumber);
        if (vehicle == null) {
            return null;
        }
        List<Map<String, Object>> drivers = driverDao.findByCarNoForApp(carNumber);
        List<Map<String, Object>> locks = findlocksByCarNo(carNumber);
        Map<String, Object> map = new HashMap<>();
        map.put("vehicle", vehicle);
        map.put("drivers", drivers);
        map.put("locks", locks);
        return map;
    }

    @Override
    public List<Map<String, Object>> findlocksByCarNo(String carNumber) {
        if (StringUtil.isEmpty(carNumber)) {
            return null;
        }
        List<Map<String, Object>> locks = lockDao.findlocksByCarNo(carNumber);
        locks.forEach(lock -> {
            Integer status = lockDao.getLockStatus(lock);
            lock.put("switch_status", status == null ? 0 : status);
        });
        return locks;
    }

    @Override
    public List<Map<String, Object>> findOnlineCarsForApp() {
        return vehicleDao.findOnlineCarsForApp();
    }

    @Override
    public List<Map<String, Object>> findAllCarsForApp() {
        return vehicleDao.findAllCarsForApp();
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
        Map<Long, String> carIdsMap = vehicleDao.findCarIdsByCarNumbers(carNumberBuf.toString());
        if (EmptyObjectUtil.isEmptyMap(carIdsMap)) {
            return null;
        }
        List<Map<String, Object>> tracks = new ArrayList<>();
        StringBuffer carIds = new StringBuffer();
        carIdsMap.keySet().forEach(carId -> carIds.append(',').append(carId));
        carIds.deleteCharAt(0);
        List<TrackInfo> trackInfoList = trackDao.findTracksByCarIdsAndBeginTime(carIds.toString(), beginTime);
        trackInfoList.parallelStream().forEach(trackInfo -> {
            Map<String, Object> trackMap = new HashMap<>();
            Long carId = trackInfo.getCarId();
            trackMap.put("vehicle_id", carId);
            trackMap.put("vehicle_number", carIdsMap.get(carId));
            trackMap.put("track_id", trackInfo.getId());
            trackMap.put("is_lnglat_valid", trackInfo.getCoorValid() ? 1 : 0);
            trackMap.put("longitude", trackInfo.getLongitude());
            trackMap.put("latitude", trackInfo.getLatitude());
            trackMap.put("vehicle_status", trackInfo.getCarStatus());
            trackMap.put("vehicle_alarm_status", trackInfo.getTerminalAlarm());
            trackMap.put("angle", trackInfo.getAngle());
            trackMap.put("speed", trackInfo.getSpeed());
            trackMap.put("lock_status_info", BytesUtil.bytesToHex(trackInfo.getLockStatusInfo(), false));
            trackMap.put("track_time", trackInfo.getTrackTime());
            tracks.add(trackMap);
        });
        return tracks;
    }

    @Override
    public Map<String, Object> findTracksByCarNumber(String carNumber, Long carId, Long beginMillis) {
        Map<String, Object> map = new LinkedHashMap<>();
        Integer isOnline = vehicleDao.getOnline(carId);
        isOnline = isOnline == null ? 0 : isOnline;
        map.put("is_online", isOnline);
        List<Map<String, Object>> tracks = new ArrayList<>();
        if (beginMillis == null) {
            Map<String, Object> trackMap = trackDao.getLastTrackForApp(carNumber, carId);
            if (!EmptyObjectUtil.isEmptyMap(trackMap)) {
                tracks.add(trackMap);
            }
        } else {
            Date lastTtrackTime = trackDao.getLastTrackTime(carId);
            beginMillis = Long.max(beginMillis, lastTtrackTime.getTime() - DateUtil.MINUTE_DIFF * 10);
            String beginTime = DateUtil.formatDate(new Date(beginMillis), DateUtil.FORMAT_DATETIME);
            List<TrackInfo> trackInfoList = trackDao.findTracksByCarIdsAndBeginTime(carId.toString(), beginTime);

            trackInfoList.parallelStream().forEach(trackInfo -> {
                Map<String, Object> trackMap = new HashMap<>();
                trackMap.put("vehicle_id", carId);
                trackMap.put("vehicle_number", carNumber);
                trackMap.put("track_id", trackInfo.getId());
                trackMap.put("is_lnglat_valid", trackInfo.getCoorValid() ? 1 : 0);
                trackMap.put("longitude", trackInfo.getLongitude());
                trackMap.put("latitude", trackInfo.getLatitude());
                trackMap.put("vehicle_status", trackInfo.getCarStatus());
                trackMap.put("vehicle_alarm_status", trackInfo.getTerminalAlarm());
                trackMap.put("angle", trackInfo.getAngle());
                trackMap.put("speed", trackInfo.getSpeed());
                trackMap.put("lock_status_info", BytesUtil.bytesToHex(trackInfo.getLockStatusInfo(), false));
                trackMap.put("track_time", trackInfo.getTrackTime());
                tracks.add(trackMap);
            });
        }
        map.put("tracks", tracks);
        return map;
    }

    @Override
    public List<VehicleTree> findBindedVehicleTree() {
        List<VehicleTree> list = vehicleDao.findBindedVehicleTree();
        list.parallelStream().forEach(vehicleTree -> {
            Integer ver = vehicleTree.getVer();
            if (ver != null && ver != 0) {
                String name = vehicleTree.getName();
                name = new StringBuffer(name).append('（').append(VersionUtil.stringifyVer(ver)).append('）').toString();
                vehicleTree.setName(name);
            }
        });
        return list;
    }

    @Override
    public VehicleRealtimeStatus getVehicleRealtimeStatus(Long vehicleId) {
        return vehicleId == null ? null : vehicleDao.getVehicleRealtimeStatus(vehicleId);
    }

    @Override
    public List<String> findCarNumbersByTerminalIds(String terminalIds) {
        return StringUtil.isEmpty(terminalIds) ? null : vehicleDao.findCarNumbersByTerminalIds(terminalIds);
    }

    @Override
    public Map<Long, String> monitorVehicleOnline() {
        Map<Long, String> onlineCars = vehicleDao.findOnlineCarIds();
        vehicleDao.updateTimeoutOfflineCars();
        return onlineCars;
    }

    @Override
    public List<LastTrack> findLastTracks() {
        List<LastTrack> lastTracks = trackDao.findLastTracks();
        if (!EmptyObjectUtil.isEmptyList(lastTracks)) {
            Date begin = lastTracks.get(0).getTrackTime();
            List<LastCarStatus> list = vehicleDao.findCarStatusAfterTime(begin);
            Date trackTime;
            Date stateTime;
            for (LastTrack lastTrack : lastTracks) {
                trackTime = lastTrack.getTrackTime();
                for (LastCarStatus lastCarStatus : list) {
                    if (lastTrack.getCarId().equals(lastCarStatus.getId())) {
                        stateTime = lastCarStatus.getTime();
                        if (trackTime.before(stateTime)) {
                            lastTrack.setCarStatus(lastCarStatus.getStatus());
                            lastTrack.setTrackTime(stateTime);
                        }
                    }
                }
            }
        }
        return lastTracks;
    }

    @Override
    public Long terminalUpgrade(TerminalUpgradeInfo terminalUpgradeInfo, List<Integer> terminalIdList) {
        terminalUpgradeDao.addTerminalUpgradeInfo(terminalUpgradeInfo);
        Long upgradeId = terminalUpgradeInfo.getId();
        Integer ver = terminalUpgradeInfo.getVer();
        TerminalUpgradeRecord record;
        List<Long> ids;
        for (Integer terminalId : terminalIdList) {
            record = new TerminalUpgradeRecord();
            record.setUpgradeId(upgradeId);
            record.setVer(ver);
            ids = terminalUpgradeDao.findUnfinishedUpgradeByTerminalId(terminalId);
            if (EmptyObjectUtil.isEmptyList(ids)) {
                record.setDeviceId(terminalId);
                terminalUpgradeDao.addTerminalUpgradeRecord(record);
                continue;
            }
            int recordNum = ids.size();
            if (recordNum == 1) {
                record.setId(ids.get(0));
                terminalUpgradeDao.updateTerminalUpgradeRecord(record);
                continue;
            }
            if (recordNum > 1) {
                terminalUpgradeDao.deleteTerminalUpgradeRecord(ids);
                record.setDeviceId(terminalId);
                terminalUpgradeDao.addTerminalUpgradeRecord(record);
            }
        }
        return upgradeId;
    }

    @Override
    public List<UpgradeCancelVehicle> findUnfinishUpgradeVehicles(String carNumber) {
        return terminalUpgradeDao.findUnfinishUpgradeVehicles(carNumber);
    }

    @Override
    public void deleteUpgradeRecord(String ids) {
        terminalUpgradeDao.batchDeleteUpgradeRecord(ids);
    }

    @Override
    public Integer getUpgradeStatusById(Long upgradeRecordId) {
        return terminalUpgradeDao.getUpgradeStatusById(upgradeRecordId);
    }

    @Override
    public Map<String, Object> getTrackAndLockInfoByTrackId(String trackId) {
        if (StringUtil.isEmpty(trackId)) {
            return null;
        }
        TrackInfo trackInfo = trackDao.getTrackByTrackId(trackId);
        if (trackInfo == null) {
            return null;
        }
        trackInfo.getTerminalAlarm();
        long carId = trackInfo.getCarId();
        List<Lock> locks = lockDao.findLocksByCarId(carId);
        Integer maxLockIndex = lockDao.getMaxLockIndexByCarId(carId);
        StringBuffer lockStatusBuf = new StringBuffer();
        if (EmptyObjectUtil.isEmptyList(locks)) {
            lockStatusBuf.append("锁绑定状态异常！");
        } else {
            byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
            if (lockStatusInfo.length < locks.size() || lockStatusInfo.length != maxLockIndex) {
                lockStatusBuf.append("锁绑定状态异常！");
            } else {
                locks.forEach(lock -> {
                    int lockIndex = lock.getIndex();
                    lockStatusBuf.append('仓').append(lock.getStoreId()).append('-').append(lock.getSeatName())
                            .append('-').append(lock.getSeatIndex()).append('：');
                    lockStatusBuf.append(VehicleAlarmUtil.getLockStatusByLockIndex(lockStatusInfo, lockIndex)).append('。');
                    lockStatusBuf.append(VehicleAlarmUtil.getLockAlarmByLockIndex(lockStatusInfo, lockIndex)).append("。<br>");
                });
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("track", trackInfo);
        map.put("locks", lockStatusBuf.toString());
        return map;
    }
}
