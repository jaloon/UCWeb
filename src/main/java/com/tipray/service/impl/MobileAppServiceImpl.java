package com.tipray.service.impl;

import com.tipray.bean.track.LockStatus;
import com.tipray.bean.track.TrackInfo;
import com.tipray.constant.AlarmBitMarkConst;
import com.tipray.dao.LockDao;
import com.tipray.dao.LockRecordDao;
import com.tipray.dao.SealRecordDao;
import com.tipray.dao.TrackDao;
import com.tipray.dao.VehicleDao;
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
    private LockDao lockDao;

    @Override
    public Map<String, Object> getTrackById(String trackId, String carNumber) {
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
        return parseLockForSwitchAndAlarm(map, carId, trackInfo.getLockStatusInfo());
    }

    private Map<String, Object> parseLockForSwitchAndAlarm(Map<String, Object> map, Long carId, byte[] lockStatusInfo) {
        List<LockStatus> locks = new ArrayList<>();
        if (lockStatusInfo != null) {
            for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
                byte lock = lockStatusInfo[i];
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_7_ENABLE) > 0) {
                    LockStatus lockInfo = lockDao.getLockByCarIdAndLockIndexForApp(carId, i + 1);
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
    public List<Map<String, Object>> findSealRecords(Long carId, String carNumber, String beginTime) {
        List<Map<String, Object>> list = sealRecordDao.findSealRecords(carId, beginTime);
        return buildReturnRecords(list, carNumber);
    }

    @Override
    public List<Map<String, Object>> findLockRecords(Long carId, String carNumber, String beginTime) {
        List<Map<String, Object>> list = lockRecordDao.findLockRecords(carId, beginTime);
        return buildReturnRecords(list, carNumber);
    }

    private Map<String, Object> setCarNumberAndTrack(Map<String, Object> map, String carNumber) {
        map.put("vehicle_number", carNumber);
        Map<String, Object> track = getTrackById(map.get("vehicle_track_id").toString(), carNumber);
        if (track != null) {
            map.put("track", track);
        }
        return map;
    }

    private List<Map<String, Object>> buildReturnRecords(List<Map<String, Object>> list, String carNumber) {
        if (EmptyObjectUtil.isEmptyList(list)) {
            return Collections.emptyList();
        }
        list.parallelStream().forEach(map -> setCarNumberAndTrack(map, carNumber));
        return list;
    }
}
