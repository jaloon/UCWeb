package com.tipray.util;

import java.util.HashMap;
import java.util.Map;

import com.tipray.bean.baseinfo.Role;

/**
 * 角色缓存工具类
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class CacheUtil {
	private static Map<Long, Role> cacheRoles = new HashMap<Long, Role>();

	public static Role getCacheRole(Long roleId) {
		if (roleId == null) {
			return null;
		}
		synchronized (cacheRoles) {
			Role role = cacheRoles.get(roleId);
			if (role == null) {
				role = SpringBeanUtil.getRoleService().getRoleById(roleId);
				if (role != null) {
					if (role.getIsSuper()) {
						role.setPermissions(SpringBeanUtil.getPermissionService().findAllPermissions());
					} else {
						role.setPermissions(
								SpringBeanUtil.getPermissionService().findPermissionsByIds(role.getPermissionIds()));
					}
				}
				cacheRoles.put(roleId, role);
			}
			return role;
		}
	}

	public static void clearRoleCache() {
		cacheRoles.clear();
	}
}
