package com.tipray.service;

import java.util.List;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.core.exception.ServiceException;

/**
 * TransCompanyService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface TransCompanyService {
	/**
	 * 新增运输公司
	 * 
	 * @param transCompany
	 * @throws ServiceException
	 */
    TransCompany addTransCompany(TransCompany transCompany) throws ServiceException;

	/**
	 * 修改运输公司信息
	 * 
	 * @param transCompany
	 * @throws ServiceException
	 */
    TransCompany updateTransCompany(TransCompany transCompany) throws ServiceException;

	/**
	 * 根据Id删除运输公司
	 * 
	 * @param id
	 * @throws ServiceException
	 */
    void deleteTransCompanyById(Long id) throws ServiceException;

	/**
	 * 根据Id获取运输公司信息
	 * 
	 * @param id
	 * @return
	 */
    TransCompany getTransCompanyById(Long id);

	/**
	 * 根据名称获取运输公司信息
	 * 
	 * @param name
	 * @return
	 */
    TransCompany getByName(String name);

	/**
	 * 查询所有的运输公司信息列表
	 * 
	 * @return
	 */
    List<TransCompany> findAllTransCompanys();

	/**
	 * 获取上级公司列表
	 * 
	 * @param gasStationId
	 * @return
	 */
    List<TransCompany> findSuperiorCom();

	/**
	 * 获取运输公司数目
	 * 
	 * @return
	 */
    long countTransCompany(TransCompany transCompany);

	/**
	 * 分页查询运输公司信息
	 * 
	 * @param transCompany
	 * @param page
	 * @return
	 */
    List<TransCompany> findByPage(TransCompany transCompany, Page page);

	/**
	 * 分页查询运输公司信息
	 * 
	 * @param transCompany
	 * @param page
	 * @return
	 */
    GridPage<TransCompany> findTransCompanysForPage(TransCompany transCompany, Page page);
}