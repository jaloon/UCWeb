package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Role;
import com.tipray.dao.RoleDao;
import com.tipray.dao.UserDao;
import com.tipray.service.RoleService;
import com.tipray.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {
	@Resource
	private RoleDao roleDao;
	@Resource
	private UserDao userDao;

	@Transactional
	@Override
	public Role addRole(Role role) {
		if (role != null) {
			roleDao.add(role);
		}
		return role;
	}

	@Transactional
	@Override
	public Role updateRole(Role role) {
		if (role != null) {
			roleDao.update(role);
		}
		return role;
	}

    @Transactional
	@Override
	public void deleteRolesById(Long id, Integer app) {
		roleDao.delete(id);
		if (app != null && app > 0) {
			userDao.resetUserRole(id);
		} else {
			userDao.removeAppRole(id);
		}
	}

	@Override
	public Role getRoleById(Long id) {
		return id == null ? null : roleDao.getById(id);
	}

	@Override
	public List<Role> findAllRoles() {
		return roleDao.findAll();
	}

	@Override
	public Role getRoleByName(String name) {
		return StringUtil.isNotEmpty(name) ? roleDao.getByName(name) : null;
	}

	@Override
	public List<Role> findByPage(Role role, Page page) {
		return roleDao.findByPage(role, page);
	}

	@Override
	public long countRole(Role role) {
		return roleDao.count(role);
	}

	@Override
	public GridPage<Role> findRolesForPage(Role role, Page page) {
		long records = countRole(role);
		List<Role> list = findByPage(role, page);
		return new GridPage<>(list, records, page, role);
	}
}