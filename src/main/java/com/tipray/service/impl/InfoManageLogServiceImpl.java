package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.dao.InfoManageLogDao;
import com.tipray.service.InfoManageLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 操作日志管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("infoManageLogService")
public class InfoManageLogServiceImpl implements InfoManageLogService {
	@Resource
	private InfoManageLogDao infoManageLogDao;

	@Override
	public InfoManageLog addInfoManageLog(InfoManageLog infoManageLog) {
		if (infoManageLog != null) {
			infoManageLogDao.add(infoManageLog);
		}
		return infoManageLog;
	}

	@Override
	public InfoManageLog getInfoManageLogById(Long id) {
		return id == null ? null : infoManageLogDao.getById(id);
	}

	@Override
	public List<InfoManageLog> findAllInfoManageLogs() {
		return infoManageLogDao.findAll();
	}

	@Override
	public long countInfoManageLog(InfoManageLog infoManageLog) {
		return infoManageLogDao.count(infoManageLog);
	}

	@Override
	public List<InfoManageLog> findByPage(InfoManageLog infoManageLog, Page page) {
		return infoManageLogDao.findByPage(infoManageLog, page);
	}

	@Override
	public GridPage<InfoManageLog> findInfoManageLogsForPage(InfoManageLog infoManageLog, Page page) {
		long records = countInfoManageLog(infoManageLog);
		List<InfoManageLog> list = findByPage(infoManageLog, page);
		return new GridPage<InfoManageLog>(list, records, page.getPageId(), page.getRows(), list.size(), infoManageLog);
	}

}
