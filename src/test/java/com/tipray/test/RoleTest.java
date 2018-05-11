package com.tipray.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tipray.bean.baseinfo.Role;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.RoleService;

/**
 * 角色管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class RoleTest {
	@Resource
	private RoleService roleService;

	@Test
	public void add() {
		Role role1 = new Role();
		role1.setId(1L);
		role1.setName("超级管理员");
		role1.setIsSuper(true);
		role1.setPermissionIds("1,2");
		Role role2 = new Role();
		role2.setId(2L);
		role2.setName("操作员");
		role2.setIsSuper(true);
		role2.setPermissionIds("9,10,13");
		try {
			roleService.addRole(role1);
			roleService.addRole(role2);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void findAllRoles() {
		List<Role> rlist = roleService.findAllRoles();
		System.out.println();
		for (Role role : rlist) {
			System.out.println(role.getId() + ": " + role);
		}
		System.out.println();
	}
}
