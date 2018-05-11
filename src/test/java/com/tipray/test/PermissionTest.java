package com.tipray.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tipray.bean.PermissionConfig;
import com.tipray.bean.baseinfo.Permission;
import com.tipray.init.impl.PermissionXmlInit;
import com.tipray.service.PermissionService;
import com.tipray.util.ConvertXmlAndBeanUtil;

/**
 * 权限管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class PermissionTest {
	@Resource
	private PermissionService permissionService;

	@Test
	public void init() {
		try {
			new PermissionXmlInit().init();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Test
	public void add() {
		Permission p = new Permission();
		p.setCname("人员管理");
		p.setEname("pem");
		p.setPermissionType(1);
		p.setEnable(true);
		p.setParentId(0L);
		p.setDescription("");
		p.setIndexId(2L);
		p.setGridUrl("");
		permissionService.addPermission(p);
	}

	@Test
	public void findAllPermissions() {
		List<Permission> plist = permissionService.findAllPermissions();
		System.out.println();
		for (Permission permission : plist) {
			System.out.println(permission.getId() + "：" + permission);
		}
		System.out.println();
	}

	@Test
	public void getPermission() {
		try {
			PermissionConfig permissionConfig = (PermissionConfig) ConvertXmlAndBeanUtil.xmlToBean(
					"permissionConfig.xml", PermissionConfig.class,
					PermissionTest.class.getClassLoader().getResource("permissionMapping.xml").getPath());
			List<Permission> permissionObjects = permissionConfig.getPermission();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
