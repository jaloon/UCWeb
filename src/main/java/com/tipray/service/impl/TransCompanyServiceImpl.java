package com.tipray.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.TransCompanyDao;
import com.tipray.service.TransCompanyService;
import com.tipray.util.StringUtil;

/**
 * 运输公司管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("transCompanyService")
public class TransCompanyServiceImpl implements TransCompanyService {
	@Resource
	private TransCompanyDao transCompanyDao;

	@Override
	public TransCompany addTransCompany(TransCompany transCompany) {
		if (transCompany != null) {
			transCompanyDao.add(transCompany);
		}
		return transCompany;
	}

	@Override
	public TransCompany updateTransCompany(TransCompany transCompany) {
		if (transCompany != null) {
			transCompanyDao.update(transCompany);
		}
		return transCompany;
	}

	@Override
	public void deleteTransCompanyById(Long id) {
		transCompanyDao.delete(id);
	}

	@Override
	public TransCompany getTransCompanyById(Long id) {
		return id == null ? null : transCompanyDao.getById(id);
	}

	@Override
	public TransCompany getByName(String name) {
		if (StringUtil.isNotEmpty(name)) {
			return transCompanyDao.getByName(name);
		}
		return null;
	}

	@Override
	public List<TransCompany> findAllTransCompanys() {
		return transCompanyDao.findAll();
	}

	@Override
	public List<TransCompany> findSuperiorCom() {
		return transCompanyDao.findSuperiorCom();
	}

	@Override
	public long countTransCompany(TransCompany transCompany) {
		return transCompanyDao.count(transCompany);
	}

	@Override
	public List<TransCompany> findByPage(TransCompany transCompany, Page page) {
		return transCompanyDao.findByPage(transCompany, page);
	}

	@Override
	public GridPage<TransCompany> findTransCompanysForPage(TransCompany transCompany, Page page) {
		long records = countTransCompany(transCompany);
		List<TransCompany> list = findByPage(transCompany, page);
		return new GridPage<TransCompany>(list, records, page.getPageId(), page.getRows(), list.size(), transCompany);
	}

}
