package com.tipray.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.dao.CommonDao;
import com.tipray.service.CommonService;
import com.tipray.util.StringUtil;

/**
 * 数据库操作业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("commonService")
public class CommonServiceImpl implements CommonService {

	private final Set<String> existTables = new HashSet<String>();
	@Resource
	private CommonDao commonDao;

	@Override
	public String getDBDatadir() {
		return commonDao.getDBDatadir();
	}

	@Override
	public boolean isTablesExist() {
		return commonDao.countTables() > 0;
	}

	@Override
	public boolean isTableExist(String tableName) {
		if (existTables.contains(tableName)) {
			return true;
		}
		boolean exist = commonDao.countTableByName(tableName) > 0;
		if (exist) {
			existTables.add(tableName);
		}
		return exist;
	}

	@Override
	public List<String> findTables(String tableName) {
		List<String> list = new ArrayList<String>();
		if (StringUtil.isNotEmpty(tableName)) {
			list = commonDao.findTables(tableName);
		}
		return list;
	}

	@Override
	public boolean removeTable(String table) {
		if (StringUtil.isEmpty(table)) {
			return false;
		}
		commonDao.removeTable(table);
		return true;
	}

	@Override
	public void executeUpdate(String sql) {
		if (StringUtil.isEmpty(sql)) {
			return;
		}
		commonDao.executeUpdate(sql);
	}

	@Override
	public void backupTable(String table, String outFile, String ids) {
		if (StringUtil.isEmpty(table)) {
			return;
		}
		if (commonDao.countTableRow(table) > 0) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("table", table);
			params.put("outFile", outFile);
			params.put("ids", ids);
			commonDao.backupTable(params);
		}
	}

	@Override
	public void clearTable(String... tables) {
		if (tables == null || tables.length == 0) {
			return;
		}
		for (String table : tables) {
			if (isTableExist(table)) {
				commonDao.clearTable(table);
			}
		}
	}
}
