package com.tipray.service;

import java.util.List;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Role;
import com.tipray.core.exception.ServiceException;

/**
 * RoleService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface RoleService {
	/**
	 * 新增角色
	 * 
	 * @param bean
	 * @throws ServiceException
	 */
    Role addRole(Role bean) throws ServiceException;

	/**
	 * 修改角色
	 * 
	 * @param bean
	 */
    Role updateRole(Role bean) throws ServiceException;

	/**
	 * 根据Id和角色类型（是否APP角色）删除角色
	 * 
	 * @param id
	 * @param app 是否APP角色
	 */
    void deleteRolesById(Long id, Integer app) throws ServiceException;

	/**
	 * 根据Id获取角色
	 * 
	 * @param id
	 * @return
	 */
    Role getRoleById(Long id);

	/**
	 * 获取所有的角色
	 * 
	 * @param id
	 * @return
	 */
    List<Role> findAllRoles();

	/**
	 * 获取角色数目
	 * 
	 * @param role
	 * @return
	 */
    long countRole(Role role);

	/**
	 * 分页查询角色集合
	 * 
	 * @param role
	 * @param page
	 * @return
	 */
    List<Role> findByPage(Role role, Page page);

	/**
	 * 分页查询角色集合
	 * 
	 * @param role
	 * @param page
	 * @return
	 */
    GridPage<Role> findRolesForPage(Role role, Page page);

	/**
	 * 根据角色名称获取角色
	 * 
	 * @param name
	 * @return
	 */
    Role getRoleByName(String name);
}
