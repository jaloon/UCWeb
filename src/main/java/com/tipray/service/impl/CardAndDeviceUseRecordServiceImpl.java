package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.record.CardAndDeviceUseRecord;
import com.tipray.bean.track.TrackInfo;
import com.tipray.dao.CardAndDeviceUseRecordDao;
import com.tipray.dao.LockDao;
import com.tipray.dao.TrackDao;
import com.tipray.service.CardAndDeviceUseRecordService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.VehicleAlarmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 卡及设备使用记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Transactional(rollbackForClassName = "Exception")
@Service("deviceRecordService")
public class CardAndDeviceUseRecordServiceImpl implements CardAndDeviceUseRecordService {
    private static final Logger logger = LoggerFactory.getLogger(CardAndDeviceUseRecordServiceImpl.class);
    @Resource
    private CardAndDeviceUseRecordDao deviceRecordDao;
    @Resource
    private TrackDao trackDao;
    @Resource
    private LockDao lockDao;

    @Override
    public CardAndDeviceUseRecord getRecordById(Long id) {
        if (id != null) {
            CardAndDeviceUseRecord deviceRecord = deviceRecordDao.getById(id);
            TrackInfo trackInfo = trackDao.getTrackByTrackId(deviceRecord.getTrackId().toString());
            if (trackInfo == null) {
                logger.warn("轨迹数据异常！");
            } else {
                setTrackForRecord(deviceRecord, trackInfo);
                byte terminalAlarmStatus = trackInfo.getTerminalAlarm().byteValue();
                byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
                boolean isAlarm = VehicleAlarmUtil.isAlarm(terminalAlarmStatus, lockStatusInfo);
                deviceRecord.setAlarm(isAlarm ? "是" : "否");

                StringBuffer alarmType = new StringBuffer();
                alarmType.append(VehicleAlarmUtil.getTerminalAlarmInfo(terminalAlarmStatus));

                StringBuffer lockStatusBuf = new StringBuffer();
                Long carId = deviceRecord.getCarId();
                List<Lock> locks = lockDao.findLocksByCarId(carId);
                Integer maxLockIndex = lockDao.getMaxLockIndexByCarId(carId);

                if (EmptyObjectUtil.isEmptyList(locks)) {
                    alarmType.append("锁绑定状态异常！");
                    lockStatusBuf.append("锁绑定状态异常！");
                } else {
                    if (lockStatusInfo.length < locks.size() || lockStatusInfo.length != maxLockIndex) {
                        alarmType.append("锁绑定状态异常！");
                        lockStatusBuf.append("锁绑定状态异常！");
                    } else {
                        StringBuffer alarmBuf = new StringBuffer();
                        locks.forEach(lock -> {
                            StringBuffer lockBuf = new StringBuffer();
                            lockBuf.append(',');
                            lockBuf.append(lock.getStoreId()).append("号仓");
                            lockBuf.append(lock.getSeatName()).append(lock.getSeatIndex());
                            alarmBuf.append(lockBuf);
                            lockStatusBuf.append(lockBuf);
                            int lockIndex = lock.getIndex();
                            alarmBuf.append(VehicleAlarmUtil.getLockAlarmByLockIndex(lockStatusInfo, lockIndex));
                            lockStatusBuf.append(VehicleAlarmUtil.getLockStatusByLockIndex(lockStatusInfo, lockIndex));
                        });
                        alarmBuf.deleteCharAt(0);
                        alarmType.append(alarmBuf);
                        lockStatusBuf.deleteCharAt(0);
                    }
                }
                deviceRecord.setAlarmType(alarmType.toString());
                deviceRecord.setLockStatus(lockStatusBuf.toString());
            }
            return deviceRecord;
        }
        return null;
    }

    @Override
    public List<CardAndDeviceUseRecord> findAllRecords() {
        List<CardAndDeviceUseRecord> list = deviceRecordDao.findAll();
        return list;
    }

    @Override
    public long countRecord(CardAndDeviceUseRecord record) {
        return deviceRecordDao.count(record);
    }

    @Override
    public List<CardAndDeviceUseRecord> findByPage(CardAndDeviceUseRecord record, Page page) {
        List<CardAndDeviceUseRecord> list = deviceRecordDao.findByPage(record, page);
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(useRecord -> trackIds.append(',').append(useRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(useRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(useRecord.getTrackId());
                    if (trackInfo != null) {
                        setTrackForRecord(useRecord, trackInfo);
                        boolean isAlarm = VehicleAlarmUtil.isAlarm(trackInfo.getTerminalAlarm().byteValue(), trackInfo.getLockStatusInfo());
                        useRecord.setAlarm(isAlarm ? "是" : "否");
                    }
                });
            } catch (Exception e) {
                logger.error("轨迹数据异常！", e);
            }
        }
        return list;
    }

    private CardAndDeviceUseRecord setTrackForRecord(CardAndDeviceUseRecord useRecord, TrackInfo trackInfo) {
        useRecord.setLongitude(trackInfo.getLongitude());
        useRecord.setLatitude(trackInfo.getLatitude());
        useRecord.setAngle(trackInfo.getAngle());
        useRecord.setVelocity(trackInfo.getSpeed());
        return useRecord;
    }

    @Override
    public GridPage<CardAndDeviceUseRecord> findRecordsForPage(CardAndDeviceUseRecord record, Page page) {
        long records = countRecord(record);
        List<CardAndDeviceUseRecord> list = findByPage(record, page);
        return new GridPage<CardAndDeviceUseRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
    }
}
