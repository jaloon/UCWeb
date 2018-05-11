package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.record.AlarmRecord;
import com.tipray.dao.AlarmRecordDao;
import com.tipray.dao.LockDao;
import com.tipray.service.AlarmRecordService;
import com.tipray.util.VehicleAlarmUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 报警记录业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("alarmRecordService")
public class AlarmRecordServiceImpl implements AlarmRecordService {
	@Resource
	private AlarmRecordDao alarmRecordDao;
	@Resource
	private LockDao lockDao;

	@Override
	public AlarmRecord getRecordById(Long id) {
		if (id != null) {
			AlarmRecord alarmRecord = alarmRecordDao.getById(id);
			setLockStatus(alarmRecord);
			return alarmRecord;
		}
		return null;
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
		list.parallelStream().forEach(alarmRecord -> setLockStatus(alarmRecord));
		return list;
	}

	@Override
	public long countRecord(AlarmRecord record) {
		return alarmRecordDao.count(record);
	}

	@Override
	public List<AlarmRecord> findByPage(AlarmRecord record, Page page) {
		List<AlarmRecord> list = alarmRecordDao.findByPage(record, page);
		list.parallelStream().forEach(alarmRecord -> setLockStatus(alarmRecord));
		return list;
	}

	@Override
	public GridPage<AlarmRecord> findRecordsForPage(AlarmRecord record, Page page) {
		long records = countRecord(record);
		List<AlarmRecord> list = findByPage(record, page);
		return new GridPage<AlarmRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
	}

	private void setLockStatus(AlarmRecord record) {
		if (record == null) {
			return;
		}
		byte[] lockStatusInfo = record.getLockStatusInfo();
        if (lockStatusInfo == null) {
            return;
        }
		String lockStatus = "";
		if (record.getDeviceType() == 2) {// 1 车台，2 锁
			Lock lock = lockDao.getByLockId(record.getDeviceId());
            if (lock == null || lock.getIndex() == null) {
                return;
            }
			lockStatus = VehicleAlarmUtil.getLockStatusByLockIndex(lockStatusInfo, lock.getIndex());
		} else {
			lockStatus = VehicleAlarmUtil.getLockStatus(lockStatusInfo);
		}
		record.setLockStatus(lockStatus);
	}

	@Override
	public void addEliminateAlarm(Map<String, Object> eAlarmMap) {
		alarmRecordDao.addEliminateAlarm(eAlarmMap);
	}

	@Override
	public void updateEliminateAlarm(Integer eliminateId, Integer eliminateStatus, String alarmIds) {
        alarmRecordDao.updateEliminateStatus(eliminateId, eliminateStatus);
        if (eliminateStatus == 2) {
            alarmRecordDao.updateEliminateAlarmDone(eliminateId, alarmIds);
        }
	}

	@Override
	public List<AlarmRecord> findNotElimited() {
		return alarmRecordDao.findNotElimited();
	}

	@Override
	public List<Map<String, Object>> findNotElimitedForApp() {
		return alarmRecordDao.findNotElimitedForApp();
	}
}
