package com.tipray.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.RemoteRecord;
import com.tipray.dao.RemoteRecordDao;
import com.tipray.service.RemoteRecordService;

/**
 * 远程控制记录业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("remoteRecordService")
public class RemoteRecordServiceImpl implements RemoteRecordService {
	@Resource
	private RemoteRecordDao remoteRecordDao;

	@Override
	public RemoteRecord getRecordById(Long id) {
		return id == null ? null : remoteRecordDao.getById(id);
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
		return remoteRecordDao.findByPage(record, page);
	}

	@Override
	public GridPage<RemoteRecord> findRecordsForPage(RemoteRecord record, Page page) {
		long records = countRecord(record);
		List<RemoteRecord> list = findByPage(record, page);
		return new GridPage<RemoteRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
	}

}
