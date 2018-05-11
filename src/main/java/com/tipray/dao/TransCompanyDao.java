package com.tipray.dao;

import java.util.List;

import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * TransCompanyDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface TransCompanyDao extends BaseDao<TransCompany> {
	/**
	 * 获取上级公司列表
	 * 
	 * @param gasStationId
	 * @return
	 */
    List<TransCompany> findSuperiorCom();

	/**
	 * 根据名称获取运输公司信息
	 * 
	 * @param name
	 * @return
	 */
    TransCompany getByName(String name);
}
