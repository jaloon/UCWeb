package com.tipray.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.CardAndDeviceUseRecord;
import com.tipray.dao.CardAndDeviceUseRecordDao;
import com.tipray.service.CardAndDeviceUseRecordService;
import com.tipray.util.VehicleAlarmUtil;

/**
 * 卡及设备使用记录业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("deviceRecordService")
public class CardAndDeviceUseRecordServiceImpl implements CardAndDeviceUseRecordService {
	@Resource
	private CardAndDeviceUseRecordDao deviceRecordDao;

	@Override
	public CardAndDeviceUseRecord getRecordById(Long id) {
		if (id != null) {
			CardAndDeviceUseRecord deviceRecord = deviceRecordDao.getById(id);
			setAlarmAndLockStatus(deviceRecord);
			return deviceRecord;
		}
		return null;
	}

	@Override
	public List<CardAndDeviceUseRecord> findAllRecords() {
		List<CardAndDeviceUseRecord> list = deviceRecordDao.findAll();
		list.parallelStream().forEach(deviceRecord -> setAlarmAndLockStatus(deviceRecord));
		return list;
	}

	@Override
	public long countRecord(CardAndDeviceUseRecord record) {
		return deviceRecordDao.count(record);
	}

	@Override
	public List<CardAndDeviceUseRecord> findByPage(CardAndDeviceUseRecord record, Page page) {
		List<CardAndDeviceUseRecord> list = deviceRecordDao.findByPage(record, page);
		list.parallelStream().forEach(deviceRecord -> setAlarmAndLockStatus(deviceRecord));
		return list;
	}

	@Override
	public GridPage<CardAndDeviceUseRecord> findRecordsForPage(CardAndDeviceUseRecord record, Page page) {
		long records = countRecord(record);
		List<CardAndDeviceUseRecord> list = findByPage(record, page);
		return new GridPage<CardAndDeviceUseRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
	}

	private void setAlarmAndLockStatus(CardAndDeviceUseRecord record) {
		byte[] lockStatusInfo = record.getLockStatusInfo();
		String alarm = VehicleAlarmUtil.getLockAlarmInfo(lockStatusInfo);
		String lockStatus = VehicleAlarmUtil.getLockStatus(lockStatusInfo);
		record.setAlarm(alarm);
		record.setLockStatus(lockStatus);
	}
}
