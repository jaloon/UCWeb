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
import com.tipray.service.AlarmRecordService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.VehicleAlarmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报警记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Transactional(rollbackForClassName = "Exception")
@Service("alarmRecordService")
public class AlarmRecordServiceImpl implements AlarmRecordService {
    private static final Logger logger = LoggerFactory.getLogger(AlarmRecordServiceImpl.class);
    @Resource
    private AlarmRecordDao alarmRecordDao;
    @Resource
    private LockDao lockDao;
    @Resource
    private TrackDao trackDao;

    @Override
    public AlarmRecord getRecordById(Long id) {
        if (id != null) {
            AlarmRecord alarmRecord = alarmRecordDao.getById(id);
            if (alarmRecord == null) {
                return null;
            }
            alarmRecord.setRecordTime(alarmRecord.getAlarmTime());
            TrackInfo trackInfo = trackDao.getTrackByTrackId(alarmRecord.getTrackId().toString());
            if (trackInfo == null) {
                logger.warn("轨迹数据异常！");
            } else {
                StringBuffer lockStatusBuf = new StringBuffer();
                if (alarmRecord.getDeviceType() == 1) {
                    Long carId = alarmRecord.getVehicleId();
                    List<Lock> locks = lockDao.findLocksByCarId(carId);
                    Integer maxLockIndex = lockDao.getMaxLockIndexByCarId(carId);
                    if (EmptyObjectUtil.isEmptyList(locks)) {
                        lockStatusBuf.append("锁绑定状态异常！");
                    } else {
                        byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
                        if (lockStatusInfo.length < locks.size() || lockStatusInfo.length != maxLockIndex) {
                            lockStatusBuf.append("锁绑定状态异常！");
                        } else {
                            locks.forEach(lock -> lockStatusBuf.append('仓').append(lock.getStoreId()).append('-')
                                        .append(lock.getSeatName()).append('-').append(lock.getSeatIndex()).append('-')
                                        .append(VehicleAlarmUtil.getLockStatusByLockIndex(lockStatusInfo, lock.getIndex()))
                                        .append("<br>"));
                        }
                    }
                } else {
                    Lock lock = lockDao.getByLockId(alarmRecord.getVehicleId(), alarmRecord.getDeviceId());
                    if (lock == null) {
                        lockStatusBuf.append("锁绑定状态异常！");
                    } else {
                        byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
                        int lockIndex = lock.getIndex();
                        if (lockStatusInfo.length < lockIndex) {
                            lockStatusBuf.append("锁绑定状态异常！");
                        } else {
                            lockStatusBuf.append('仓').append(lock.getStoreId()).append('-')
                                    .append(lock.getSeatName()).append('-').append(lock.getSeatIndex()).append('-')
                                    .append(VehicleAlarmUtil.getLockStatusByLockIndex(lockStatusInfo, lock.getIndex()));
                        }
                    }
                }
                alarmRecord.setLockStatus(lockStatusBuf.toString());
                setTrackForRecord(alarmRecord, trackInfo);
            }
            return alarmRecord;
        }
        return null;
    }

    /**
     * 设置轨迹信息
     *
     * @param alarmRecord 报警记录
     * @param trackInfo   轨迹信息
     * @return 报警记录
     */
    private AlarmRecord setTrackForRecord(AlarmRecord alarmRecord, TrackInfo trackInfo) {
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
    public List<AlarmRecord> findAllRecords() {
        List<AlarmRecord> list = alarmRecordDao.findAll();
        return list;
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
                        setTrackForRecord(alarmRecord, trackInfo);
                    }
                });
            } catch (Exception e) {
                logger.error("轨迹数据异常！", e);
            }
        }
        return list;
    }

    @Override
    public GridPage<AlarmRecord> findRecordsForPage(AlarmRecord record, Page page) {
        long records = countRecord(record);
        List<AlarmRecord> list = findByPage(record, page);
        return new GridPage<AlarmRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
    }

    @Override
    public List<Long> findSameAlarmIdsById(Long id) {
        return id == null ? null : alarmRecordDao.findSameAlarmIdsById(id);
    }

    @Override
    public void addEliminateAlarm(Map<String, Object> eAlarmMap) {
        alarmRecordDao.addEliminateAlarm(eAlarmMap);
    }

    @Override
    public void updateEliminateAlarm(Integer eliminateId, Integer eliminateStatus, String alarmIds, List<Long> alarmIdList) {
        alarmRecordDao.updateEliminateStatus(eliminateId, eliminateStatus);
        if (eliminateStatus == 2) {
            alarmRecordDao.updateAlarmEliminated(eliminateId, alarmIdList);
            alarmRecordDao.updateEliminateAlarmDone(alarmIds);
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
                logger.error("轨迹数据异常！", e);
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
                AlarmInfo alarmInfo = alarmRecordDao.getAlarmInfoByAlarmDevcie(alarmDevice);
                if (alarmInfo != null) {
                    alarmInfo.setAlarmTag(VehicleAlarmUtil.buildAlarmTag(alarmDevice));
                    alarmInfos.add(alarmInfo);
                }
            }
            alarmInfos.sort(null);
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
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(alarmMap -> trackIds.append(',').append(alarmMap.get("vehicle_track_id")));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(alarmMap -> {
                    Map<String, Integer> lockinfo = lockDao.getByIdForAppAlarm((Long) alarmMap.get("lock_id"));
                    TrackInfo trackInfo = trackInfoMap.get(((BigInteger) alarmMap.get("vehicle_track_id")).longValue());
                    if (trackInfo == null) {
                        alarmMap.put("is_lnglat_valid", 0);
                        alarmMap.put("longitude", 0);
                        alarmMap.put("latitude", 0);
                        if (!EmptyObjectUtil.isEmptyMap(lockinfo)) {
                            lockinfo.put("switch_status", 0);
                        }
                    } else {
                        alarmMap.put("is_lnglat_valid", trackInfo.getCoorValid() ? 1 : 0);
                        alarmMap.put("longitude", trackInfo.getLongitude());
                        alarmMap.put("latitude", trackInfo.getLatitude());
                        if (!EmptyObjectUtil.isEmptyMap(lockinfo)) {
                            byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
                            Integer lockIndex = lockinfo.get("lock_index");
                            if (lockStatusInfo == null || lockIndex == null || lockStatusInfo.length < lockIndex) {
                                lockinfo.put("switch_status", 0);
                            } else {
                                int switchStatus = lockStatusInfo[lockIndex - 1] & AlarmBitMarkConst.LOCK_ALARM_BIT_8_ON_OFF;
                                lockinfo.put("switch_status", switchStatus > 0 ? 2 : 1);
                            }
                        }
                    }
                    if (!EmptyObjectUtil.isEmptyMap(lockinfo)) {
                        alarmMap.put("lockinfo", lockinfo);
                    }
                });
            } catch (Exception e) {
                logger.error("轨迹数据异常：{}", e.toString());
                list.forEach(alarmMap -> {
                    alarmMap.put("is_lnglat_valid", 0);
                    alarmMap.put("longitude", 0);
                    alarmMap.put("latitude", 0);
                    Map<String, Integer> lockinfo = lockDao.getByIdForAppAlarm((Long) alarmMap.get("lock_id"));
                    if (!EmptyObjectUtil.isEmptyMap(lockinfo)) {
                        lockinfo.put("switch_status", 0);
                        alarmMap.put("lockinfo", lockinfo);
                    }
                });
            }
        }
        return list;
    }
}
