package com.tipray.service.impl;

import com.tipray.bean.lock.LockStatus;
import com.tipray.bean.track.TrackInfo;
import com.tipray.constant.AlarmBitMarkConst;
import com.tipray.dao.AlarmRecordDao;
import com.tipray.dao.DistributionRecordDao;
import com.tipray.dao.LockDao;
import com.tipray.dao.LockRecordDao;
import com.tipray.dao.SealRecordDao;
import com.tipray.dao.TrackDao;
import com.tipray.dao.VehicleDao;
import com.tipray.service.AlarmRecordService;
import com.tipray.service.MobileAppService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.StringUtil;
import com.tipray.util.VehicleAlarmUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端APP接口业务层
 *
 * @author chenlong
 * @version 1.0 2018-10-11
 */
@Service("mobileAppService")
public class MobileAppServiceImpl implements MobileAppService {
    @Resource
    private TrackDao trackDao;
    @Resource
    private VehicleDao vehicleDao;
    @Resource
    private SealRecordDao sealRecordDao;
    @Resource
    private LockRecordDao lockRecordDao;
    @Resource
    private DistributionRecordDao distributionRecordDao;
    @Resource
    private AlarmRecordDao alarmRecordDao;
    @Resource
    private LockDao lockDao;
    @Resource
    private AlarmRecordService alarmRecordService;

    @Override
    public Map<String, Object> getTrackById(String trackId, String carNumber) {
        return getTrackById(trackId, carNumber, true);
    }

    private Map<String, Object> getTrackById(String trackId, String carNumber, boolean validLock) {
        TrackInfo trackInfo = trackDao.getTrackByTrackId(trackId);
        if (trackInfo == null) {
            return null;
        }
        Long carId = trackInfo.getCarId();
        if (StringUtil.isEmpty(carNumber)) {
            carNumber = vehicleDao.getCarNumberById(carId);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("track_id", trackInfo.getId());
        map.put("vehicle_id", carId);
        map.put("vehicle_number", carNumber == null ? "" : carNumber);
        map.put("is_lnglat_valid", trackInfo.getCoorValid() ? 1 : 0);
        map.put("longitude", trackInfo.getLongitude());
        map.put("latitude", trackInfo.getLatitude());
        map.put("vehicle_status", trackInfo.getCarStatus());
        map.put("vehicle_alarm_status", trackInfo.getTerminalAlarm());
        map.put("angle", trackInfo.getAngle());
        map.put("speed", trackInfo.getSpeed());
        // map.put("lock_status_info", BytesUtil.bytesToHex(trackInfo.getLockStatusInfo(), false));
        map.put("track_time", trackInfo.getTrackTime());
        // map.put("gmt_create", trackInfo.getCreateTime());
        return parseLockForSwitchAndAlarm(map, carId, trackInfo.getLockStatusInfo(), validLock);
    }

    private Map<String, Object> parseLockForSwitchAndAlarm(Map<String, Object> map,
                                                           Long carId,
                                                           byte[] lockStatusInfo,
                                                           boolean validLock) {
        List<LockStatus> locks = new ArrayList<>();
        if (lockStatusInfo != null) {
            for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
                byte lock = lockStatusInfo[i];
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_7_ENABLE) > 0) {
                    LockStatus lockInfo = lockDao.getLockByCarIdAndLockIndexForApp(carId, i + 1, validLock);
                    if (lockInfo != null) {
                        lockInfo.setSwitch_status(VehicleAlarmUtil.getLockStatusValue(lock));
                        lockInfo.setAlarm(VehicleAlarmUtil.getLockAlarmValues(lock));
                        locks.add(lockInfo);
                    }
                }
            }
        }
        map.put("locks", locks);
        return map;
    }

    @Override
    public List<Map<String, Object>> findSealRecords(Long carId, String carNumber, String beginTime, String endTime) {
        List<Map<String, Object>> list;
        if (StringUtil.isEmpty(beginTime)) {
            list = new ArrayList<>();
            Map<String, Object> map = sealRecordDao.getLastSealRecord(carId);
            if (map == null) {
                throw new IllegalStateException("车辆【" + carNumber + "】无施解封记录");
            }
            list.add(map);
        } else {
            list = sealRecordDao.findSealRecords(carId, beginTime, endTime);
        }
        // return buildReturnRecords(list, carNumber, true);
        list.parallelStream().forEach(map -> map.put("vehicle_number", carNumber));
        return list;
    }

    @Override
    public List<Map<String, Object>> findLockRecords(Long carId, String carNumber, String beginTime, String endTime) {
        List<Map<String, Object>> list = lockRecordDao.findLockRecords(carId, beginTime, endTime);
        // return buildReturnRecords(list, carNumber, true);
        list.parallelStream().forEach(map -> map.put("vehicle_number", carNumber));
        return list;
    }

    @Override
    public List<Map<String, Object>> findDistRecords(long carId, String carNumber, String beginTime, String endTime) {
        List<Map<String, Object>> list = distributionRecordDao.findDistRecords(carId, beginTime, endTime);
        list.parallelStream().forEach(map -> map.put("vehicle_number", carNumber));
        return list;
    }

    @Override
    public List<Map<String, Object>> findAlarmRecords(long carId, String carNumber, String beginTime, String endTime) {
        List<Map<String, Object>> list = alarmRecordDao.findAlarmRecords(carId, beginTime, endTime);
        list.removeIf(this::isInvalidAlarm);
        // return buildReturnRecords(list, carNumber, false);
        list.parallelStream().forEach(map -> map.put("vehicle_number", carNumber));
        return list;
    }

    private boolean isInvalidAlarm(Map<String, Object> alarm) {
        if (alarm.get("status").equals(0)) {
            return alarmRecordService.isInvalidAlarm(alarm);
        }
        return false;
    }

    private void setCarNumberAndTrack(Map<String, Object> map, String carNumber, boolean validLock) {
        map.put("vehicle_number", carNumber);
        Map<String, Object> track = getTrackById(map.get("vehicle_track_id").toString(), carNumber, validLock);
        if (track != null) {
            map.put("track", track);
        }
    }

    private List<Map<String, Object>> buildReturnRecords(List<Map<String, Object>> list, String carNumber, boolean validLock) {
        if (EmptyObjectUtil.isEmptyList(list)) {
            return Collections.emptyList();
        }
        list.parallelStream().forEach(map -> setCarNumberAndTrack(map, carNumber, validLock));
        return list;
    }
}
