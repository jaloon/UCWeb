package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.UnlockResetRecord;
import com.tipray.bean.track.TrackInfo;
import com.tipray.dao.TrackDao;
import com.tipray.dao.UnlockResetRecordDao;
import com.tipray.service.UnlockResetRecordService;
import com.tipray.util.DateUtil;
import com.tipray.util.EmptyObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author chenlong
 * @version 1.0 2018-08-30
 */
@Service("unlockResetRecordService")
public class UnlockResetRecordServiceImpl implements UnlockResetRecordService {
    private static final Logger logger = LoggerFactory.getLogger(UnlockResetRecordServiceImpl.class);
    @Resource
    private UnlockResetRecordDao unlockResetRecordDao;
    @Resource
    private TrackDao trackDao;

    @Override
    public UnlockResetRecord getRecordById(Long id) {
        if (id == null) {
            return null;
        }
        UnlockResetRecord record = unlockResetRecordDao.getById(id);
        if (record == null) {
            return null;
        }
        record.setRecordTime(DateUtil.formatDate(record.getCreateDate(), DateUtil.FORMAT_DATETIME));
        TrackInfo trackInfo = trackDao.getTrackByTrackId(record.getTrackId().toString());
        if (trackInfo == null) {
            logger.warn("轨迹数据异常！");
        } else {
            setTrackForRecord(record, trackInfo);
        }
        return record;
    }

    private UnlockResetRecord setTrackForRecord(UnlockResetRecord record, TrackInfo trackInfo) {
        record.setCoorValid(trackInfo.getCoorValid());
        record.setLongitude(trackInfo.getLongitude());
        record.setLatitude(trackInfo.getLatitude());
        record.setAngle(trackInfo.getAngle());
        record.setVelocity(trackInfo.getSpeed());
        return record;
    }

    @Override
    public List<UnlockResetRecord> findAllRecords() {
        return unlockResetRecordDao.findAll();
    }

    @Override
    public long countRecord(UnlockResetRecord record) {
        return record == null ? 0 : unlockResetRecordDao.count(record);
    }

    @Override
    public List<UnlockResetRecord> findByPage(UnlockResetRecord record, Page page) {
        List<UnlockResetRecord> list = unlockResetRecordDao.findByPage(record, page);
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(resetRecord -> trackIds.append(',').append(resetRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(resetRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(resetRecord.getTrackId());
                    if (trackInfo != null) {
                        setTrackForRecord(resetRecord, trackInfo);
                    }
                });
            } catch (Exception e) {
                logger.warn("轨迹数据异常！{}", e.toString());
            }
        }
        return list;
    }

    @Override
    public GridPage<UnlockResetRecord> findRecordsForPage(UnlockResetRecord record, Page page) {
        long records = countRecord(record);
        List<UnlockResetRecord> list = findByPage(record, page);
        return new GridPage<>(list, records, page.getPageId(), page.getRows(), list.size(), record);
    }
}
