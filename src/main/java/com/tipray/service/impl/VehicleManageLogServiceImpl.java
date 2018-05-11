package com.tipray.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.VehicleManageLogDao;
import com.tipray.service.VehicleManageLogService;

/**
 * 车辆管理日志业务层
 * 
 * @author chenlong
 * @version 1.0 2018-04-10
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("vehicleManageLogService")
public class VehicleManageLogServiceImpl implements VehicleManageLogService {
	@Resource
	private VehicleManageLogDao vehicleManageLogDao;

	@Override
	public VehicleManageLog addVehicleManageLog(VehicleManageLog vehicleManageLog) {
		if (vehicleManageLog != null) {
			vehicleManageLogDao.add(vehicleManageLog);
		}
		return vehicleManageLog;
	}

	@Override
	public VehicleManageLog updateVehicleManageLog(VehicleManageLog vehicleManageLog) {
		if (vehicleManageLog != null) {
			vehicleManageLogDao.update(vehicleManageLog);
		}
		return vehicleManageLog;
	}

	@Override
	public VehicleManageLog getVehicleManageLogById(Long id) {
		return id == null ? null : vehicleManageLogDao.getById(id);
	}

	@Override
	public List<VehicleManageLog> findAllVehicleManageLogs() {
		return vehicleManageLogDao.findAll();
	}

	@Override
	public long countVehicleManageLog(VehicleManageLog vehicleManageLog) {
		return vehicleManageLogDao.count(vehicleManageLog);
	}

	@Override
	public List<VehicleManageLog> findByPage(VehicleManageLog vehicleManageLog, Page page) {
		return vehicleManageLogDao.findByPage(vehicleManageLog, page);
	}

	@Override
	public GridPage<VehicleManageLog> findVehicleManageLogsForPage(VehicleManageLog vehicleManageLog, Page page) {
		long records = countVehicleManageLog(vehicleManageLog);
		List<VehicleManageLog> list = findByPage(vehicleManageLog, page);
		return new GridPage<VehicleManageLog>(list, records, page.getPageId(), page.getRows(), list.size(),
				vehicleManageLog);
	}

	@Override
	public Map<String, Object> findUdpResult(Map<String, Object> map) {
		return vehicleManageLogDao.findUdpResult(map);
	}

}
