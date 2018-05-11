package com.tipray.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.VehicleParamVer;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.VehicleParamVerDao;
import com.tipray.service.VehicleParamVerService;

/**
 * 车辆参数版本业务层
 * 
 * @author chenlong
 * @version 1.0 2018-01-15
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("carParamVerService")
public class VehicleParamVerServiceImpl implements VehicleParamVerService {
	@Resource
	private VehicleParamVerDao vehicleParamVerDao;

	@Override
	public VehicleParamVer addCarParamVer(VehicleParamVer carParamVer) {
		if (carParamVer != null) {
			vehicleParamVerDao.add(carParamVer);
		}
		return carParamVer;
	}

	@Override
	public VehicleParamVer updateCarParamVer(VehicleParamVer carParamVer) {
		if (carParamVer != null) {
			vehicleParamVerDao.update(carParamVer);
		}
		return carParamVer;
	}

	@Override
	public void deleteCarParamVersById(Long id) {
		vehicleParamVerDao.delete(id);
	}

	@Override
	public VehicleParamVer getCarParamVerById(Long id) {
		return id == null ? null : vehicleParamVerDao.getById(id);
	}

	@Override
	public VehicleParamVer getByParam(String param) {
		return vehicleParamVerDao.getByParam(param);
	}

	@Override
	public void deleteByParam(String param) {
		vehicleParamVerDao.deleteByParam(param);
	}

	@Override
	public void updateVerByParam(String param, Long ver) {
		vehicleParamVerDao.updateVerByParam(param, ver);		
	}

}
