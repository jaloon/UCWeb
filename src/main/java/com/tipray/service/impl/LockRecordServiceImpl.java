package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.LockRecord;
import com.tipray.bean.track.TrackInfo;
import com.tipray.dao.LockRecordDao;
import com.tipray.dao.TrackDao;
import com.tipray.service.LockRecordService;
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
 * 锁动作记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Service("lockRecordService")
public class LockRecordServiceImpl implements LockRecordService {
    private static final Logger logger = LoggerFactory.getLogger(LockRecordServiceImpl.class);
    @Resource
    private LockRecordDao lockRecordDao;
    @Resource
    private TrackDao trackDao;

    @Override
    public LockRecord getRecordById(Long id) {
        if (id == null) {
            return null;
        }
        LockRecord lockRecord = lockRecordDao.getById(id);
        if (lockRecord == null) {
            return null;
        }
        lockRecord.setRecordTime(DateUtil.formatDate(lockRecord.getCreateDate(), DateUtil.FORMAT_DATETIME));
        TrackInfo trackInfo = trackDao.getTrackByTrackId(lockRecord.getTrackId().toString());
        if (trackInfo == null) {
            logger.warn("轨迹数据异常！");
        } else {
            setTrackForRecord(lockRecord, trackInfo);
            setAlarm(lockRecord);
        }
        return lockRecord;
    }

    @Override
    public List<LockRecord> findAllRecords() {
        List<LockRecord> list = lockRecordDao.findAll();
        return list;
    }

    @Override
    public long countRecord(LockRecord record) {
        return lockRecordDao.count(record);
    }

    @Override
    public List<LockRecord> findByPage(LockRecord record, Page page) {
        List<LockRecord> list = lockRecordDao.findByPage(record, page);
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(LockRecord -> trackIds.append(',').append(LockRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(lockRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(lockRecord.getTrackId());
                    if (trackInfo != null) {
                        setTrackForRecord(lockRecord, trackInfo);
                        setAlarm(lockRecord);
                    }
                });
            } catch (Exception e) {
                logger.warn("轨迹数据异常！{}", e.toString());
            }
        }
        return list;
    }

    private LockRecord setTrackForRecord(LockRecord lockRecord, TrackInfo trackInfo) {
        lockRecord.setCoorValid(trackInfo.getCoorValid());
        lockRecord.setLongitude(trackInfo.getLongitude());
        lockRecord.setLatitude(trackInfo.getLatitude());
        lockRecord.setAngle(trackInfo.getAngle());
        lockRecord.setVelocity(trackInfo.getSpeed());
        lockRecord.setLockStatusInfo(trackInfo.getLockStatusInfo());
        return lockRecord;
    }

    @Override
    public GridPage<LockRecord> findRecordsForPage(LockRecord record, Page page) {
        long records = countRecord(record);
        List<LockRecord> list = findByPage(record, page);
        return new GridPage<LockRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
    }

    private void setAlarm(LockRecord record) {
        if (record == null) {
            return;
        }
        byte[] lockStatusInfo = record.getLockStatusInfo();
        int lockIndex = record.getIndex();
        if (lockStatusInfo == null || lockStatusInfo.length < lockIndex) {
            record.setAlarm("数据异常");
        } else {
            String alarm = VehicleAlarmUtil.getLockAlarmByLockIndex(lockStatusInfo, lockIndex);
            record.setAlarm(alarm);
        }
    }
}