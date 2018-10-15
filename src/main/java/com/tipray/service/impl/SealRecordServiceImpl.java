package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.record.SealRecord;
import com.tipray.bean.track.TrackInfo;
import com.tipray.dao.LockDao;
import com.tipray.dao.SealRecordDao;
import com.tipray.dao.TrackDao;
import com.tipray.service.SealRecordService;
import com.tipray.util.DateUtil;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.VehicleAlarmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 车辆进出记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Service("sealRecordService")
public class SealRecordServiceImpl implements SealRecordService {
    private static final Logger logger = LoggerFactory.getLogger(SealRecordServiceImpl.class);
    @Resource
    private SealRecordDao sealRecordDao;
    @Resource
    private TrackDao trackDao;
    @Resource
    private LockDao lockDao;

    @Override
    public SealRecord getRecordById(Long id) {
        if (id == null) {
            return null;
        }
        SealRecord sealRecord = sealRecordDao.getById(id);
        if (sealRecord == null) {
            return null;
        }
        sealRecord.setRecordTime(DateUtil.formatDate(sealRecord.getCreateDate(), DateUtil.FORMAT_DATETIME));
        TrackInfo trackInfo = trackDao.getTrackByTrackId(sealRecord.getTrackId().toString());
        if (trackInfo == null) {
            logger.warn("轨迹数据异常！");
        } else {
            setTrackForRecord(sealRecord, trackInfo);
            byte terminalAlarmStatus = trackInfo.getTerminalAlarm().byteValue();
            byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
            boolean isAlarm = VehicleAlarmUtil.isAlarm(terminalAlarmStatus, lockStatusInfo);
            sealRecord.setAlarm(isAlarm ? "是" : "否");

            StringBuffer alarmType = new StringBuffer();
            alarmType.append(VehicleAlarmUtil.getTerminalAlarmInfo(terminalAlarmStatus)).append("<br>");

            StringBuffer lockStatusBuf = new StringBuffer();
            Long carId = sealRecord.getCarId();
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
                        int lockIndex = lock.getIndex();
                        lockBuf.append('仓').append(lock.getStoreId()).append('-')
                                .append(lock.getSeatName()).append('-').append(lock.getSeatIndex());
                        alarmBuf.append(lockBuf).append('-')
                                .append(VehicleAlarmUtil.getLockAlarmByLockIndex(lockStatusInfo, lockIndex))
                                .append("<br>");
                        lockStatusBuf.append(lockBuf).append('-')
                                .append(VehicleAlarmUtil.getLockStatusByLockIndex(lockStatusInfo, lockIndex))
                                .append("<br>");
                    });
                    alarmType.append(alarmBuf);
                }
            }
            sealRecord.setAlarmType(alarmType.toString());
            sealRecord.setLockStatus(lockStatusBuf.toString());
        }
        return sealRecord;
    }

    @Override
    public List<SealRecord> findAllRecords() {
        return sealRecordDao.findAll();
    }

    @Override
    public long countRecord(SealRecord record) {
        return sealRecordDao.count(record);
    }

    @Override
    public List<SealRecord> findByPage(SealRecord record, Page page) {
        List<SealRecord> list = sealRecordDao.findByPage(record, page);
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(sealRecord -> trackIds.append(',').append(sealRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(sealRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(sealRecord.getTrackId());
                    if (trackInfo != null) {
                        setTrackForRecord(sealRecord, trackInfo);
                        boolean isAlarm = VehicleAlarmUtil.isAlarm(trackInfo.getTerminalAlarm().byteValue(), trackInfo.getLockStatusInfo());
                        sealRecord.setAlarm(isAlarm ? "是" : "否");
                    }
                });
            } catch (Exception e) {
                logger.warn("轨迹数据异常！{}", e.toString());
            }
        }
        return list;
    }

    private SealRecord setTrackForRecord(SealRecord sealRecord, TrackInfo trackInfo) {
        sealRecord.setCoorValid(trackInfo.getCoorValid());
        sealRecord.setLongitude(trackInfo.getLongitude());
        sealRecord.setLatitude(trackInfo.getLatitude());
        sealRecord.setAngle(trackInfo.getAngle());
        sealRecord.setVelocity(trackInfo.getSpeed());
        return sealRecord;
    }

    @Override
    public GridPage<SealRecord> findRecordsForPage(SealRecord record, Page page) {
        long records = countRecord(record);
        List<SealRecord> list = findByPage(record, page);
        return new GridPage<>(list, records, page, record);
    }
}