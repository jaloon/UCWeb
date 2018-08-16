package com.tipray.dao;

import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

import java.util.List;

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
     * 统计公司名称
     * @param name
     * @return
     */
    Integer countByComName(String name);

    /**
     * 根据公司名称更新运输公司
     * @param company
     */
    void updateByComName(TransCompany company);

    /**
     * 根据公司名称删除运输公司
     * @param name
     */
    void deleteByComName(String name);

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
