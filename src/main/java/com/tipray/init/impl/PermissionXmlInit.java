package com.tipray.init.impl;

import com.tipray.bean.PermissionConfig;
import com.tipray.bean.baseinfo.Permission;
import com.tipray.init.AbstractInitialization;
import com.tipray.service.PermissionService;
import com.tipray.util.ConvertXmlAndBeanUtil;
import com.tipray.util.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 权限树的初始化 读取permissionConfig.xml文件
 * 
 * @author chends
 *
 */
public class PermissionXmlInit extends AbstractInitialization {
	private static Logger logger = LoggerFactory.getLogger(PermissionXmlInit.class);

	private PermissionService permissionService;

	@Override
	public void init() throws Exception {
		this.permissionService = SpringBeanUtil.getPermissionService();
		initPermission("permissionConfig.xml");
	}

	@Override
	public void update() {
	}

	private void initPermission(String xml) throws Exception {
		PermissionConfig permissionConfig = getPermissionConfig(xml);
		if (null != permissionConfig) {
			createDataInDB(permissionConfig.getPermission(), null);
		}
	}

	/**
	 * set data in DB,parentid is null;
	 *
     */
	private void createDataInDB(List<Permission> permissionObjects, Permission parent) {
		if (permissionObjects != null) {
			for (int i = 0; i < permissionObjects.size(); i++) {
				Permission permission = permissionObjects.get(i);
				boolean enable = permission.getEnable() == null ? (parent == null ? true : parent.getEnable())
						: permission.getEnable();
				permission.setEnable(enable);
				permission.setIndexId(new Long(i));
				permission.setParentId(parent != null ? parent.getId() : 0L);
				List<Permission> childrenPermissions = permission.getChildren();
				permission.setIsParent(childrenPermissions != null);
				permissionService.addPermission(permission);
				createDataInDB(childrenPermissions, permission);
			}
		}
	}

	/**
	 * @return permissionConfig object
	 */
	private PermissionConfig getPermissionConfig(String xml) {
		try {
			PermissionConfig permissionConfig = (PermissionConfig) ConvertXmlAndBeanUtil
					.xmlToBean(getPermissionConfigPath(xml), PermissionConfig.class, getMapInputPermissionPath());
			return permissionConfig;
		} catch (Exception e) {
			logger.error("problem in execute!", e);
			return null;
		}
	}

	private String getPermissionConfigPath(String xml) {
		return xml;
	}

	private String getMapInputPermissionPath() {
		return PermissionXmlInit.class.getClassLoader().getResource("permissionMapping.xml").getPath();
	}

}
