package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.record.UsageRecord;
import com.tipray.bean.track.TrackInfo;
import com.tipray.constant.AlarmBitMarkConst;
import com.tipray.dao.LockDao;
import com.tipray.dao.TrackDao;
import com.tipray.dao.UsageRecordDao;
import com.tipray.service.UsageRecordService;
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
 * 卡及设备使用记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Service("usageRecordService")
public class UsageRecordServiceImpl implements UsageRecordService {
    private static final Logger logger = LoggerFactory.getLogger(UsageRecordServiceImpl.class);
    @Resource
    private UsageRecordDao usageRecordDao;
    @Resource
    private TrackDao trackDao;
    @Resource
    private LockDao lockDao;

    @Override
    public UsageRecord getRecordById(Long id) {
        if (id == null) {
            return null;
        }
        UsageRecord record = usageRecordDao.getById(id);
        if (record == null) {
            return null;
        }
        record.setRecordTime(DateUtil.formatDate(record.getCreateDate(), DateUtil.FORMAT_DATETIME));
        TrackInfo trackInfo = trackDao.getTrackByTrackId(record.getTrackId().toString());
        if (trackInfo == null) {
            logger.warn("轨迹数据异常！");
            return record;
        }
        setTrackForRecord(record, trackInfo);
        byte terminalAlarmStatus = trackInfo.getTerminalAlarm().byteValue();
        byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
        boolean isAlarm = VehicleAlarmUtil.isAlarm(terminalAlarmStatus, lockStatusInfo);
        record.setAlarm(isAlarm ? "是" : "否");

        StringBuffer alarmType = new StringBuffer();
        alarmType.append(VehicleAlarmUtil.getTerminalAlarmInfo(terminalAlarmStatus)).append("<br>");

        StringBuffer lockStatusBuf = new StringBuffer();
        Long carId = record.getCarId();

        if (lockStatusInfo != null) {
            StringBuffer lockBuf;
            StringBuffer alarmBuf = new StringBuffer();
            for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
                byte lockInfo = lockStatusInfo[i];
                if ((lockInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_7_ENABLE) > 0) {
                    Lock lock = lockDao.getLockByCarIdAndLockIndex(carId, i + 1);
                    if (lock != null) {
                        lockBuf = new StringBuffer();
                        lockBuf.append('仓').append(lock.getStoreId()).append('-')
                                .append(lock.getSeatName()).append('-')
                                .append(lock.getSeatIndex());
                        alarmBuf.append(lockBuf).append('-')
                                .append(VehicleAlarmUtil.getLockAlarm(lockInfo))
                                .append("<br>");
                        lockStatusBuf.append(lockBuf).append('-')
                                .append(VehicleAlarmUtil.getLockStatus(lockInfo))
                                .append("<br>");
                    }
                }
            }
            alarmType.append(alarmBuf);
        }
        record.setAlarmType(alarmType.toString());
        record.setLockStatus(lockStatusBuf.toString());
        return record;
    }

    @Override
    public List<UsageRecord> findAllRecords() {
        return usageRecordDao.findAll();
    }

    @Override
    public long countRecord(UsageRecord record) {
        return usageRecordDao.count(record);
    }

    @Override
    public List<UsageRecord> findByPage(UsageRecord record, Page page) {
        List<UsageRecord> list = usageRecordDao.findByPage(record, page);
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(usageRecord -> trackIds.append(',').append(usageRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(usageRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(usageRecord.getTrackId());
                    if (trackInfo != null) {
                        setTrackForRecord(usageRecord, trackInfo);
                        boolean isAlarm = VehicleAlarmUtil.isAlarm(trackInfo.getTerminalAlarm().byteValue(), trackInfo.getLockStatusInfo());
                        usageRecord.setAlarm(isAlarm ? "是" : "否");
                    }
                });
            } catch (Exception e) {
                logger.warn("轨迹数据异常！{}", e.toString());
            }
        }
        return list;
    }

    private UsageRecord setTrackForRecord(UsageRecord record, TrackInfo trackInfo) {
        record.setCoorValid(trackInfo.getCoorValid());
        record.setLongitude(trackInfo.getLongitude());
        record.setLatitude(trackInfo.getLatitude());
        record.setAngle(trackInfo.getAngle());
        record.setVelocity(trackInfo.getSpeed());
        return record;
    }

    @Override
    public GridPage<UsageRecord> findRecordsForPage(UsageRecord record, Page page) {
        long records = countRecord(record);
        List<UsageRecord> list = findByPage(record, page);
        return new GridPage<>(list, records, page, record);
    }
}