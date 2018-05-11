package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.LockRecord;
import com.tipray.dao.LockRecordDao;
import com.tipray.service.LockRecordService;
import com.tipray.util.VehicleAlarmUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 锁动作记录业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("lockRecordService")
public class LockRecordServiceImpl implements LockRecordService {
	@Resource
	private LockRecordDao lockRecordDao;

	@Override
	public LockRecord getRecordById(Long id) {
		if (id != null) {
			LockRecord lockRecord = lockRecordDao.getById(id);
			setAlarm(lockRecord);
			return lockRecord;
		}
		return null;
	}

	@Override
	public List<LockRecord> findAllRecords() {
		List<LockRecord> list = lockRecordDao.findAll();
		list.parallelStream().forEach(lockRecord -> setAlarm(lockRecord));
		return list;
	}

	@Override
	public long countRecord(LockRecord record) {
		return lockRecordDao.count(record);
	}

	@Override
	public List<LockRecord> findByPage(LockRecord record, Page page) {
		List<LockRecord> list = lockRecordDao.findByPage(record, page);
		list.parallelStream().forEach(lockRecord -> setAlarm(lockRecord));
		return list;
	}

	@Override
	public GridPage<LockRecord> findRecordsForPage(LockRecord record, Page page) {
		long records = countRecord(record);
		List<LockRecord> list = findByPage(record, page);
		return new GridPage<LockRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
	}

	private void setAlarm(LockRecord record) {
		if (record == null || record.getLockStatusInfo() == null) {
			return;
		}
		String alarm = VehicleAlarmUtil.getLockAlarmByLockIndex(record.getLockStatusInfo(), record.getIndex());
		record.setAlarm(alarm);
	}
}
