package com.tipray.service;

import java.util.List;
import java.util.Map;

import com.tipray.bean.baseinfo.Permission;
import com.tipray.core.exception.ServiceException;

/**
 * PermissionService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface PermissionService {
	/**
	 * 新增权限
	 * 
	 * @param user
	 * @throws ServiceException
	 */
    Permission addPermission(Permission permission);

	/**
	 * 修改权限
	 * 
	 * @param user
	 */
    Permission updatePermission(Permission permission);

	/**
	 * 根据Id获取权限
	 * 
	 * @param id
	 * @return
	 */
    Permission getPermissionById(Long id);

	/**
	 * 获取所有的权限
	 * 
	 * @param id
	 * @return
	 */
    List<Permission> findAllPermissions();

	/**
	 * 根据权限名称获取权限
	 * 
	 * @param string
	 * @return
	 */
    Permission getPermissionByEname(String string);

	/**
	 * 根据ID集合获取权限
	 * 
	 * @param ids
	 * @return
	 */
    List<Permission> findPermissionsByIds(String ids);

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
