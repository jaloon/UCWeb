package com.tipray.service.impl;

import com.tipray.bean.baseinfo.Permission;
import com.tipray.dao.PermissionDao;
import com.tipray.service.PermissionService;
import com.tipray.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 权限管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {
	@Resource
	private PermissionDao permissionDao;

	@Transactional
	@Override
	public Permission addPermission(Permission permission) {
		if (permission != null) {
			permissionDao.add(permission);
		}
		return permission;
	}

    @Transactional
	@Override
	public Permission updatePermission(Permission permission) {
		if (permission != null) {
			permissionDao.update(permission);
		}
		return permission;
	}

	@Override
	public Permission getPermissionById(Long id) {
		if (id == null) {
			return null;
		}
		return permissionDao.getById(id);
	}

	@Override
	public List<Permission> findAllPermissions() {
		return permissionDao.findAll();
	}

	@Override
	public Permission getPermissionByEname(String ename) {
		if (StringUtil.isNotEmpty(ename)) {
			return permissionDao.getByEname(ename);
		}
		return null;
	}

	@Override
	public List<Permission> findPermissionsByIds(String ids) {
		if (StringUtil.isEmpty(ids)) {
			return null;
		}
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		return permissionDao.findByIds(ids);
	}

	@Override
	public List<Permission> findOperatePermissions() {
		return permissionDao.findOperatePermissions();
	}

	@Override
	public List<Map<String, Object>> findByIdsForLogin(String ids) {
		if (StringUtil.isEmpty(ids)) {
			return null;
		}
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		return permissionDao.findByIdsForLogin(ids);
	}

	@Override
	public List<String> findEnamesByIds(String ids) {
		if (StringUtil.isEmpty(ids)) {
			return null;
		}
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		return permissionDao.findEnamesByIds(ids);
	}
}