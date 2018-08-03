package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.record.InOutRecord;
import com.tipray.bean.track.TrackInfo;
import com.tipray.dao.InOutRecordDao;
import com.tipray.dao.LockDao;
import com.tipray.dao.TrackDao;
import com.tipray.service.InOutRecordService;
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
 * 车辆进出记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Transactional(rollbackForClassName = "Exception")
@Service("inOutRecordService")
public class InOutRecordServiceImpl implements InOutRecordService {
    private static final Logger logger = LoggerFactory.getLogger(InOutRecordServiceImpl.class);
    @Resource
    private InOutRecordDao inOutRecordDao;
    @Resource
    private TrackDao trackDao;
    @Resource
    private LockDao lockDao;

    @Override
    public InOutRecord getRecordById(Long id) {
        if (id != null) {
            InOutRecord inOutRecord = inOutRecordDao.getById(id);
            TrackInfo trackInfo = trackDao.getTrackByTrackId(inOutRecord.getTrackId().toString());
            if (trackInfo == null) {
                logger.warn("轨迹数据异常！");
            } else {
                setTrackForRecord(inOutRecord, trackInfo);
                byte terminalAlarmStatus = trackInfo.getTerminalAlarm().byteValue();
                byte[] lockStatusInfo = trackInfo.getLockStatusInfo();
                boolean isAlarm = VehicleAlarmUtil.isAlarm(terminalAlarmStatus, lockStatusInfo);
                inOutRecord.setAlarm(isAlarm ? "是" : "否");

                StringBuffer alarmType = new StringBuffer();
                alarmType.append(VehicleAlarmUtil.getTerminalAlarmInfo(terminalAlarmStatus));

                StringBuffer lockStatusBuf = new StringBuffer();
                Long carId = inOutRecord.getCarId();
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
                inOutRecord.setAlarmType(alarmType.toString());
                inOutRecord.setLockStatus(lockStatusBuf.toString());
            }
            return inOutRecord;
        }
        return null;
    }

    @Override
    public List<InOutRecord> findAllRecords() {
        List<InOutRecord> list = inOutRecordDao.findAll();
        return list;
    }

    @Override
    public long countRecord(InOutRecord record) {
        return inOutRecordDao.count(record);
    }

    @Override
    public List<InOutRecord> findByPage(InOutRecord record, Page page) {
        List<InOutRecord> list = inOutRecordDao.findByPage(record, page);
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(inOutRecord -> trackIds.append(',').append(inOutRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(inOutRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(inOutRecord.getTrackId());
                    if (trackInfo != null) {
                        setTrackForRecord(inOutRecord, trackInfo);
                        boolean isAlarm = VehicleAlarmUtil.isAlarm(trackInfo.getTerminalAlarm().byteValue(), trackInfo.getLockStatusInfo());
                        inOutRecord.setAlarm(isAlarm ? "是" : "否");
                    }
                });
            } catch (Exception e) {
                logger.error("轨迹数据异常：{}", e.toString());
            }
        }
        return list;
    }

    private InOutRecord setTrackForRecord(InOutRecord inOutRecord, TrackInfo trackInfo) {
        inOutRecord.setLongitude(trackInfo.getLongitude());
        inOutRecord.setLatitude(trackInfo.getLatitude());
        inOutRecord.setAngle(trackInfo.getAngle());
        inOutRecord.setVelocity(trackInfo.getSpeed());
        return inOutRecord;
    }

    @Override
    public GridPage<InOutRecord> findRecordsForPage(InOutRecord record, Page page) {
        long records = countRecord(record);
        List<InOutRecord> list = findByPage(record, page);
        return new GridPage<InOutRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
    }

}
