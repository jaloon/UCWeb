package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.alarm.AlarmDevice;
import com.tipray.bean.alarm.AlarmInfo;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.record.AlarmRecord;
import com.tipray.bean.track.TrackInfo;
import com.tipray.constant.AlarmBitMarkConst;
import com.tipray.dao.AlarmRecordDao;
import com.tipray.dao.LockDao;
import com.tipray.dao.TrackDao;
import com.tipray.dao.VehicleDao;
import com.tipray.service.AlarmRecordService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.VehicleAlarmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 报警记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Service("alarmRecordService")
public class AlarmRecordServiceImpl implements AlarmRecordService {
    private static final Logger logger = LoggerFactory.getLogger(AlarmRecordServiceImpl.class);
    @Resource
    private AlarmRecordDao alarmRecordDao;
    @Resource
    private LockDao lockDao;
    @Resource
    private TrackDao trackDao;
    @Resource
    private VehicleDao vehicleDao;

    @Override
    public AlarmRecord getRecordById(Long id) {
        if (id == null) {
            return null;
        }
        AlarmRecord alarmRecord = alarmRecordDao.getById(id);
        if (alarmRecord == null) {
            return null;
        }
        alarmRecord.setRecordTime(alarmRecord.getAlarmTime());
        TrackInfo trackInfo = trackDao.getTrackByTrackId(alarmRecord.getTrackId().toString());
        if (trackInfo == null) {
            logger.warn("轨迹数据异常！");
            return alarmRecord;
        }
        alarmRecord = setTrackForRecord(alarmRecord, trackInfo);

        // 车辆ID
        Long carId = alarmRecord.getVehicleId();
        // 锁状态信息流
        byte[] lockStatusInfo = trackInfo.getLockStatusInfo();

        StringBuffer lockStatusBuf = new StringBuffer();
        if (alarmRecord.getDeviceType() == 1) {
            // 车台报警，显示所有锁
            if (lockStatusInfo != null) {
                for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
                    byte lockInfo = lockStatusInfo[i];
                    if ((lockInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_7_ENABLE) > 0) {
                        Lock lock = lockDao.getLockByCarIdAndLockIndex(carId, i + 1);
                        if (lock != null) {
                            lockStatusBuf.append('仓').append(lock.getStoreId()).append('-')
                                    .append(lock.getSeatName()).append('-')
                                    .append(lock.getSeatIndex()).append('-')
                                    .append(VehicleAlarmUtil.getLockStatus(lockInfo))
                                    .append("<br>");
                        }
                    }
                }
            }
        } else {
            // 锁报警，显示单个锁
            Lock lock = lockDao.getLockByCarIdAndLockId(carId, alarmRecord.getLockId());
            if (lock != null) {
                lockStatusBuf.append('仓').append(lock.getStoreId()).append('-')
                        .append(lock.getSeatName()).append('-')
                        .append(lock.getSeatIndex()).append('-')
                        .append(VehicleAlarmUtil.getLockStatusByLockIndex(lockStatusInfo, lock.getIndex()));
            }
        }
        alarmRecord.setLockStatus(lockStatusBuf.toString());
        return alarmRecord;
    }

    /**
     * 设置轨迹信息
     *
     * @param alarmRecord 报警记录
     * @param trackInfo   轨迹信息
     * @return 报警记录
     */
    private AlarmRecord setTrackForRecord(AlarmRecord alarmRecord, TrackInfo trackInfo) {
        alarmRecord.setCoorValid(trackInfo.getCoorValid());
        alarmRecord.setLongitude(trackInfo.getLongitude());
        alarmRecord.setLatitude(trackInfo.getLatitude());
        alarmRecord.setAngle(trackInfo.getAngle());
        alarmRecord.setVelocity(trackInfo.getSpeed());
        return alarmRecord;
    }

    @Override
    public List<AlarmRecord> getAlarmRecordsByIdsAndCar(String ids, Long vehicleId) {
        return ids != null ? alarmRecordDao.getAlarmRecordsByIdsAndCar(ids, vehicleId) : null;
    }

    @Override
    public Integer countAlarmDeviceByIds(String ids) {
        return ids != null ? alarmRecordDao.countAlarmDeviceByIds(ids) : null;
    }

    @Override
    public AlarmRecord getAlarmForEliById(Long id) {
        return null == id ? null : alarmRecordDao.getAlarmForEliById(id);
    }

    @Override
    public List<AlarmRecord> findAllRecords() {
        return alarmRecordDao.findAll();
    }

    @Override
    public long countRecord(AlarmRecord record) {
        return alarmRecordDao.count(record);
    }

    @Override
    public List<AlarmRecord> findByPage(AlarmRecord record, Page page) {
        List<AlarmRecord> list = alarmRecordDao.findByPage(record, page);
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(alarmRecord -> trackIds.append(',').append(alarmRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(alarmRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(alarmRecord.getTrackId());
                    if (trackInfo != null) {
                        alarmRecord = setTrackForRecord(alarmRecord, trackInfo);
                    }
                });
            } catch (Exception e) {
                logger.warn("轨迹数据异常！{}", e.toString());
            }
        }
        return list;
    }

    @Override
    public GridPage<AlarmRecord> findRecordsForPage(AlarmRecord record, Page page) {
        long records = countRecord(record);
        List<AlarmRecord> list = findByPage(record, page);
        return new GridPage<>(list, records, page, record);
    }

    @Override
    public List<Long> findSameAlarmIdsById(Long id) {
        return id == null ? null : alarmRecordDao.findSameAlarmIdsById(id);
    }

    @Transactional
    @Override
    public void addEliminateAlarm(Map<String, Object> eAlarmMap) {
        alarmRecordDao.addEliminateAlarm(eAlarmMap);
    }

    @Transactional
    @Override
    public void updateEliminateAlarm(Integer eliminateId, Integer eliminateStatus, String alarmIds, List<Long> alarmIdList) {
        alarmRecordDao.updateEliminateStatus(eliminateId, eliminateStatus);
        if (eliminateStatus == 2) {
            List<Long> alarmIdListForSame = new ArrayList<>();
            for (Long alarmId : alarmIdList) {
                alarmIdListForSame.addAll(alarmRecordDao.findSameAlarmIdsById(alarmId));
            }
            alarmRecordDao.updateAlarmEliminated(eliminateId, alarmIdListForSame);
            alarmRecordDao.updateEliminateAlarmDone(eliminateId, alarmIds);
        }
    }

    @Override
    public List<AlarmRecord> findNotElimited() {
        List<AlarmRecord> list = alarmRecordDao.findNotElimited();
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(alarmRecord -> trackIds.append(',').append(alarmRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(alarmRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(alarmRecord.getTrackId());
                    if (trackInfo != null) {
                        alarmRecord.setLongitude(trackInfo.getLongitude());
                        alarmRecord.setLatitude(trackInfo.getLatitude());
                    }
                });
            } catch (Exception e) {
                logger.warn("轨迹数据异常！{}", e.toString());
            }
        }
        return list;
    }

    @Override
    public List<AlarmInfo> findNotElimitedAlarmInfo() {
        List<AlarmInfo> alarmInfos = new ArrayList<>();
        List<AlarmDevice> alarmDevices = alarmRecordDao.findNotElimitedAlarmDevice();
        if (!EmptyObjectUtil.isEmptyList(alarmDevices)) {
            for (AlarmDevice alarmDevice : alarmDevices) {
                if (alarmDevice.getDeviceType() == 1) {
                    Integer terminalId = vehicleDao.getTerminalIdById(alarmDevice.getVehicleId());
                    if (terminalId == null || terminalId == 0) {
                        // 车或车台已不存在，报警无效
                        continue;
                    }
                    if (!terminalId.equals(alarmDevice.getDeviceId())) {
                        // 车台已换新，报警无效
                        continue;
                    }
                } else {
                    Integer lockDeviceId = lockDao.getLockDeviceIdById(alarmDevice.getLockId());
                    if (lockDeviceId == null || lockDeviceId == 0) {
                        // 该位置已无锁，报警无效
                        continue;
                    }
                    if (!lockDeviceId.equals(alarmDevice.getDeviceId())) {
                        // 已换新锁，报警无效
                        continue;
                    }
                }
                AlarmInfo alarmInfo = alarmRecordDao.getAlarmInfoByAlarmDevcie(alarmDevice);
                if (alarmInfo != null) {
                    alarmInfo.setAlarmTag(VehicleAlarmUtil.buildAlarmTag(alarmDevice));
                    alarmInfos.add(alarmInfo);
                }
            }
            if (alarmInfos.size() > 1) {
                alarmInfos.sort(null);
            }
        }
        return alarmInfos;
    }

    @Override
    public AlarmInfo getAlarmInfoByAlarmId(Long alarmId) {
        if (alarmId == null) {
            return null;
        }
        AlarmInfo alarmInfo = alarmRecordDao.getAlarmInfoByAlarmId(alarmId);
        if (alarmInfo != null) {
            alarmInfo.setAlarmTag(VehicleAlarmUtil.buildAlarmTag(alarmInfo));
        }
        return alarmInfo;
    }

    @Override
    public List<Map<String, Object>> findNotElimitedForApp() {
        List<Map<String, Object>> list = alarmRecordDao.findNotElimitedForApp();
        if (EmptyObjectUtil.isEmptyList(list)) {
            return Collections.emptyList();
        }
        list.removeIf(this::isInvalidAlarm);
        // if (EmptyObjectUtil.isEmptyList(list)) {
        //     return Collections.emptyList();
        // }
        // StringBuffer trackIds = new StringBuffer();
        // list.forEach(alarmMap -> trackIds.append(',').append(alarmMap.get("vehicle_track_id")));
        // trackIds.deleteCharAt(0);
        // try {
        //     Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
        //     list.forEach(alarmMap -> {
        //         Map<String, Integer> lockinfo = lockDao.getByIdForAppAlarm((Long) alarmMap.get("lock_id"));
        //         TrackInfo trackInfo = trackInfoMap.get(((BigInteger) alarmMap.get("vehicle_track_id")).longValue());
        //         if (trackInfo == null) {
        //             alarmMap.put("is_lnglat_valid", 0);
        //             alarmMap.put("longitude", 0);
        //             alarmMap.put("latitude", 0);
        //             if (!EmptyObjectUtil.isEmptyMap(lockinfo)) {
        //                 lockinfo.put("switch_status", 0);
        //             }
        //         } else {
        //             alarmMap.put("is_lnglat_valid", trackInfo.getCoorValid() ? 1 : 0);
        //             alarmMap.put("longitude", trackInfo.getLongitude());
        //             alarmMap.put("latitude", trackInfo.getLatitude());
        //             if (!EmptyObjectUtil.isEmptyMap(lockinfo)) {
        //                 byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
        //                 Integer lockIndex = lockinfo.get("lock_index");
        //                 if (lockStatusInfo == null || lockIndex == null || lockStatusInfo.length < lockIndex) {
        //                     lockinfo.put("switch_status", 0);
        //                 } else {
        //                     int switchStatus = lockStatusInfo[lockIndex - 1] & AlarmBitMarkConst.LOCK_ALARM_BIT_8_ON_OFF;
        //                     lockinfo.put("switch_status", switchStatus == 0 ? 1 : 2);
        //                 }
        //             }
        //         }
        //         if (!EmptyObjectUtil.isEmptyMap(lockinfo)) {
        //             alarmMap.put("lockinfo", lockinfo);
        //         }
        //     });
        // } catch (Exception e) {
        //     logger.warn("轨迹数据异常！{}", e.toString());
        //     list.forEach(alarmMap -> {
        //         alarmMap.put("is_lnglat_valid", 0);
        //         alarmMap.put("longitude", 0);
        //         alarmMap.put("latitude", 0);
        //         Map<String, Integer> lockinfo = lockDao.getByIdForAppAlarm((Long) alarmMap.get("lock_id"));
        //         if (!EmptyObjectUtil.isEmptyMap(lockinfo)) {
        //             lockinfo.put("switch_status", 0);
        //             alarmMap.put("lockinfo", lockinfo);
        //         }
        //     });
        // }
        return list;
    }

    @Override
    public boolean isInvalidAlarm(Map<String, Object> alarm) {
        Integer deviceType = (Integer) alarm.get("device_type");
        if (deviceType == 1) {
            Integer terminalId = vehicleDao.getTerminalIdById((Long) alarm.get("vehicle_id"));
            if (terminalId == null || terminalId == 0) {
                // 车或车台已不存在，报警无效
                return true;
            }
            if (!terminalId.equals(((Long) alarm.get("device_id")).intValue())) {
                // 车台已换新，报警无效
                return true;
            }
        } else if (deviceType == 2) {
            int lockId = ((Long) alarm.get("lock_id")).intValue();
            if (lockId == 0) {
                // 该位置已无锁，报警无效
                return true;
            }
            Integer lockDeviceId = lockDao.getLockDeviceIdById(lockId);
            if (lockDeviceId == null || lockDeviceId == 0) {
                // 该位置已无锁，报警无效
                return true;
            }
            if (!lockDeviceId.equals(((Long) alarm.get("device_id")).intValue())) {
                // 已换新锁，报警无效
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public Long updateAlarmStateForInvalidDevice() {
        return alarmRecordDao.updateAlarmStateForInvalidDevice();
    }
}