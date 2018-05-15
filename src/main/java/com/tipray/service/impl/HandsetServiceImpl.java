package com.tipray.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.Handset;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.GasStationDao;
import com.tipray.dao.HandsetDao;
import com.tipray.service.HandsetService;

/**
 * 手持机管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("handsetService")
public class HandsetServiceImpl implements HandsetService {
	@Resource
	private HandsetDao handsetDao;
	@Resource
	private GasStationDao gasStationDao;

	@Override
	public Handset addHandset(Handset handset) {
		if (handset != null) {
			handsetDao.add(handset);
		}
		return handset;
	}

	@Override
	public Handset updateHandset(Handset handset) {
		if (handset != null) {
			handsetDao.update(handset);
		}
		return handset;
	}

	@Override
	public void deleteHandsetById(Long id) {
		handsetDao.delete(id);
	}

	@Override
	public Handset getHandsetById(Long id) {
		return id == null ? null : handsetDao.getById(id);
	}

	@Override
	public Handset getByGasStationId(Long gasStationId) {
		return gasStationId == null ? null : handsetDao.getByGasStationId(gasStationId);

	}

	@Override
	public List<Handset> findAllHandsets() {
		return handsetDao.findAll();
	}

	@Override
	public long countHandset(Handset handset) {
		return handsetDao.count(handset);
	}

	@Override
	public List<Handset> findByPage(Handset handset, Page page) {
		return handsetDao.findByPage(handset, page);
	}

	@Override
	public GridPage<Handset> findHandsetsForPage(Handset handset, Page page) {
		long records = countHandset(handset);
		List<Handset> list = findByPage(handset, page);
		return new GridPage<Handset>(list, records, page.getPageId(), page.getRows(), list.size(), handset);
	}

	@Override
	public List<GasStation> getGasStationList() {
		return gasStationDao.getGasStationList();
	}

	@Override
	public List<Integer> findUnaddHandset() {
		return handsetDao.findUnaddHandset();
	}

	@Override
	public List<GasStation> findUnconfigGasStation() {
		return gasStationDao.findUnconfigGasStation();
	}

	@Override
	public List<Integer> findUnusedHandset() {
		return handsetDao.findUnusedHandset();
	}

}