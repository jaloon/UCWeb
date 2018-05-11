package com.tipray.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.tipray.util.VehicleAlarmUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.InOutRecord;
import com.tipray.dao.InOutRecordDao;
import com.tipray.service.InOutRecordService;

/**
 * 车辆进出记录业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("inOutRecordService")
public class InOutRecordServiceImpl implements InOutRecordService {
	@Resource
	private InOutRecordDao inOutRecordDao;

	@Override
	public InOutRecord getRecordById(Long id) {
		if (id != null) {
			InOutRecord inOutRecord = inOutRecordDao.getById(id);
			setAlarmAndLockStatus(inOutRecord);
			return inOutRecord;
		}
		return null;
	}

	@Override
	public List<InOutRecord> findAllRecords() {
		List<InOutRecord> list = inOutRecordDao.findAll();
		list.parallelStream().forEach(inOutRecord -> setAlarmAndLockStatus(inOutRecord));
		return list;
	}

	@Override
	public long countRecord(InOutRecord record) {
		return inOutRecordDao.count(record);
	}

	@Override
	public List<InOutRecord> findByPage(InOutRecord record, Page page) {
		List<InOutRecord> list = inOutRecordDao.findByPage(record, page);
		list.parallelStream().forEach(inOutRecord -> setAlarmAndLockStatus(inOutRecord));
		return list;
	}

	@Override
	public GridPage<InOutRecord> findRecordsForPage(InOutRecord record, Page page) {
		long records = countRecord(record);
		List<InOutRecord> list = findByPage(record, page);
		return new GridPage<InOutRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
	}

	private void setAlarmAndLockStatus(InOutRecord record) {
		if (record == null) {
			return;
		}
		byte[] lockStatusInfo = record.getLockStatusInfo();
		String alarm = VehicleAlarmUtil.getLockAlarmInfo(lockStatusInfo);
		String lockStatus = VehicleAlarmUtil.getLockStatus(lockStatusInfo);
		record.setAlarm(alarm);
		record.setLockStatus(lockStatus);
	}
}
