package com.tipray.service;

import java.util.List;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Driver;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.core.exception.ServiceException;

/**
 * DriverService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface DriverService {
	/**
	 * 新增司机
	 * 
	 * @param driver
	 * @throws ServiceException
	 */
    Driver addDriver(Driver driver) throws ServiceException;

	/**
	 * 修改司机信息
	 * 
	 * @param driver
	 * @throws ServiceException
	 */
    Driver updateDriver(Driver driver) throws ServiceException;

	/**
	 * 根据Id删除司机
	 * 
	 * @param id
	 * @throws ServiceException
	 */
    void deleteDriverById(Long id) throws ServiceException;

	/**
	 * 根据Id获取司机信息
	 * 
	 * @param id
	 * @return
	 */
    Driver getDriverById(Long id);

	/**
	 * 根据所属运输公司获取司机信息
	 * 
	 * @param transCompany
	 * @return
	 */
    List<Driver> findByTransCompany(TransCompany transCompany);

	/**
	 * 查询所有的司机信息列表
	 * 
	 * @param
	 * @return
	 */
    List<Driver> findAllDrivers();

	/**
	 * 获取司机数目
	 * 
	 * @return
	 */
    long countDriver(Driver driver);

	/**
	 * 分页查询司机信息
	 * 
	 * @param driver
	 * @param page
	 * @return
	 */
    List<Driver> findByPage(Driver driver, Page page);

	/**
	 * 分页查询司机信息
	 * 
	 * @param driver
	 * @param page
	 * @return
	 */
    GridPage<Driver> findDriversForPage(Driver driver, Page page);

	/**
	 * 获取空闲司机列表
	 * 
	 * @return
	 */
    List<Driver> findFreeDrivers();
}
