package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.RemoteRecord;
import com.tipray.bean.track.TrackInfo;
import com.tipray.dao.RemoteRecordDao;
import com.tipray.dao.TrackDao;
import com.tipray.service.RemoteRecordService;
import com.tipray.util.DateUtil;
import com.tipray.util.EmptyObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 远程控制记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Transactional(rollbackForClassName = "Exception")
@Service("remoteRecordService")
public class RemoteRecordServiceImpl implements RemoteRecordService {
    private static final Logger logger = LoggerFactory.getLogger(RemoteRecordServiceImpl.class);
    @Resource
    private RemoteRecordDao remoteRecordDao;
    @Resource
    private TrackDao trackDao;

    @Override
    public RemoteRecord getRecordById(Long id) {
        if (id == null) {
            return null;
        }
        RemoteRecord remoteRecord = remoteRecordDao.getById(id);
        if (remoteRecord == null) {
            return null;
        }
        remoteRecord.setRecordTime(DateUtil.formatDate(remoteRecord.getCreateDate(), DateUtil.FORMAT_DATETIME));
        TrackInfo trackInfo = trackDao.getTrackByTrackId(remoteRecord.getTrackId().toString());
        if (trackInfo == null) {
            logger.warn("轨迹数据异常！");
        } else {
            setTrackForRecord(remoteRecord, trackInfo);
        }
        return remoteRecord;
    }

    @Override
    public List<RemoteRecord> findAllRecords() {
        return remoteRecordDao.findAll();
    }

    @Override
    public long countRecord(RemoteRecord record) {
        return remoteRecordDao.count(record);
    }

    @Override
    public List<RemoteRecord> findByPage(RemoteRecord record, Page page) {
        List<RemoteRecord> list = remoteRecordDao.findByPage(record, page);
        if (!EmptyObjectUtil.isEmptyList(list)) {
            StringBuffer trackIds = new StringBuffer();
            list.forEach(remoteRecord -> trackIds.append(',').append(remoteRecord.getTrackId()));
            trackIds.deleteCharAt(0);
            try {
                Map<Long, TrackInfo> trackInfoMap = trackDao.findTrackMapByTrackIds(trackIds.toString());
                list.forEach(remoteRecord -> {
                    TrackInfo trackInfo = trackInfoMap.get(remoteRecord.getTrackId());
                    if (trackInfo != null) {
                        setTrackForRecord(remoteRecord, trackInfo);
                    }
                });
            } catch (Exception e) {
                logger.error("轨迹数据异常！", e);
            }
        }
        return list;
    }

    private RemoteRecord setTrackForRecord(RemoteRecord remoteRecord, TrackInfo trackInfo) {
        remoteRecord.setLongitude(trackInfo.getLongitude());
        remoteRecord.setLatitude(trackInfo.getLatitude());
        remoteRecord.setAngle(trackInfo.getAngle());
        remoteRecord.setVelocity(trackInfo.getSpeed());
        return remoteRecord;
    }

    @Override
    public GridPage<RemoteRecord> findRecordsForPage(RemoteRecord record, Page page) {
        long records = countRecord(record);
        List<RemoteRecord> list = findByPage(record, page);
        return new GridPage<RemoteRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
    }

}
