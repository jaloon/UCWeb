package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Driver;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.dao.DriverDao;
import com.tipray.service.DriverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 司机管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Service("driverService")
public class DriverServiceImpl implements DriverService {
	@Resource
	private DriverDao driverDao;

	@Transactional
	@Override
	public Driver addDriver(Driver driver) {
		if (driver != null) {
			driverDao.add(driver);
		}
		return driver;
	}

	@Transactional
	@Override
	public Driver updateDriver(Driver driver) {
		if (driver != null) {
			driverDao.update(driver);
		}
		return driver;
	}

    @Transactional
	@Override
	public void deleteDriverById(Long id) {
		driverDao.delete(id);
	}

	@Override
	public Driver getDriverById(Long id) {
		return id == null ? null : driverDao.getById(id);
	}

	@Override
	public List<Driver> findByTransCompany(TransCompany transCompany) {
		return null;
	}

	@Override
	public List<Driver> findAllDrivers() {
		return driverDao.findAll();
	}

	@Override
	public long countDriver(Driver driver) {
		return driverDao.count(driver);
	}

	@Override
	public List<Driver> findByPage(Driver driver, Page page) {
		return driverDao.findByPage(driver, page);
	}

	@Override
	public GridPage<Driver> findDriversForPage(Driver driver, Page page) {
		long records = countDriver(driver);
		List<Driver> list = findByPage(driver, page);
		return new GridPage<>(list, records, page, driver);
	}

	@Override
	public List<Driver> findFreeDrivers() {
		return driverDao.findFreeDrivers();
	}

}