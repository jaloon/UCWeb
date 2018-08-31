package com.tipray.service.impl;

import com.tipray.bean.VehicleParamVer;
import com.tipray.dao.VehicleParamVerDao;
import com.tipray.service.VehicleParamVerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 车辆参数版本业务层
 * 
 * @author chenlong
 * @version 1.0 2018-01-15
 *
 */
@Service("carParamVerService")
public class VehicleParamVerServiceImpl implements VehicleParamVerService {
	@Resource
	private VehicleParamVerDao vehicleParamVerDao;

	@Transactional
	@Override
	public VehicleParamVer addCarParamVer(VehicleParamVer carParamVer) {
		if (carParamVer != null) {
			vehicleParamVerDao.add(carParamVer);
		}
		return carParamVer;
	}

    @Transactional
	@Override
	public VehicleParamVer updateCarParamVer(VehicleParamVer carParamVer) {
		if (carParamVer != null) {
			vehicleParamVerDao.update(carParamVer);
		}
		return carParamVer;
	}

    @Transactional
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

    @Transactional
	@Override
	public void deleteByParam(String param) {
		vehicleParamVerDao.deleteByParam(param);
	}

    @Transactional
	@Override
	public void updateVerByParam(String param, Long ver) {
		vehicleParamVerDao.updateVerByParam(param, ver);		
	}
}