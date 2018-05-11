/*
 * PermissionDao.java
 * Copyright(C) 厦门天锐科技有限公司
 * All rights reserved.
 * -----------------------------------------------
 * 2016-01-11 Created
 */
package com.tipray.dao;

import java.util.List;
import java.util.Map;

import com.tipray.bean.baseinfo.Permission;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * PermissionDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface PermissionDao extends BaseDao<Permission> {
	/**
	 * 根据权限名称获取权限
	 * 
	 * @param ename
	 * @return
	 */
    Permission getByEname(String ename);

	/**
	 * 根据id集合获取权限
	 * 
	 * @param ids
	 * @return
	 */
    List<Permission> findByIds(String ids);

	/**
	 * 获取系统操作权限
	 * 
	 * @return
	 */
    List<Permission> findOperatePermissions();

	/**
	 * 根据id集合获取权限
	 * 
	 * @param ids
	 * @return
	 */
    List<Map<String, Object>> findByIdsForLogin(String ids);

	/**
	 * 根据id集合获取权限英文名称
	 * 
	 * @param ids
	 * @return
	 */
    List<String> findEnamesByIds(String ids);
}