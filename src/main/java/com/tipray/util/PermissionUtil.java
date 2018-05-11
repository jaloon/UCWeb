package com.tipray.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.tipray.bean.baseinfo.Permission;
/**
 * 权限工具类
 * @author chends
 *
 */
public class PermissionUtil {
	/**
	 * permissionType，权限类型：菜单
	 */
	public static final Integer PERMISSION_TYPE_MENU = 1;
	/**
	 * permissionType，权限类型：功能
	 */
	public static final Integer PERMISSION_TYPE_FUNC = 0;
	
	public static Permission getByEname(List<Permission> permissions, String ename) {
		if (permissions != null && ename != null) {
			for (Permission permission : permissions) {
				if (permission.getEname().equals(ename)) {
					return permission;
				}
			}
		}
		return null;
	}
	
	/**
	 * 拼装树结构的权限列表
	 * @param permissions
	 * @param parentId
	 * @param permissionType
	 * @return
	 */
	public static List<Permission> toTree(List<Permission> permissions, Long parentId, Integer permissionType) {
		if (parentId == null) {
			parentId = getZeroPermission().getId();
		}
		List<Permission> list = new ArrayList<Permission>();
		if (permissions != null) {
			for (Permission permission : permissions) {
				if (permission.getParentId() == parentId 
						&& (permissionType == null || permission.getPermissionType() == permissionType)) {
					if (permission.getChecked() != null && permission.getChecked()) {
						permission.setOpen(true);
					}
					if (permission.getPermissionType() == PERMISSION_TYPE_MENU) {
						permission.setChildren(toTree(permissions,permission.getId(),permissionType));
					}
					if (permission.getChildren() == null || permission.getChildren().size() == 0) {
						permission.setIsParent(false);
					}
					list.add(permission);
				}
			}
		}
		Collections.sort(list, new SortByIndexId());
		return list;
	}
	/**
	 * 集合的替换
	 * @return
	 */
	public static List<Permission> replace(List<Permission> sourceList, List<Permission> replaceList) {
		List<Permission> resultList = new ArrayList<Permission>();
		for (int i = 0; i < sourceList.size(); i++) {
			Permission permission = sourceList.get(i);
			boolean flag = replaceList != null && replaceList.contains(permission);
			permission.setChecked(flag);
			permission.setOpen(flag);
			resultList.add(permission);
		}
		return resultList;
	}
	/**
	 * 选中集合节点
	 * @param sourceList
	 */
	public static void check(List<Permission> sourceList){
		if (sourceList != null) {
			for (Permission permission : sourceList) {
				permission.setChecked(true);
			}
		}
	}
	
	public static Permission getZeroPermission() {
		Permission permission = new Permission();
		permission.setId(0L);
		permission.setPermissionType(1);
		return permission;
	}
	/**
	 * 判断权限集合是否包含某个权限ename
	 * @param permissions
	 * @param ename
	 * @return
	 */
	public static boolean containPermission(List<Permission> permissions, String ename) {
		if (permissions != null) {
			for (Permission permission : permissions) {
				if (permission.getEname().equals(ename)) {
					return true;
				}
			}
		}
		return false;
	}
	
	static class SortByIndexId implements Comparator<Permission> {
		@Override
		public int compare(Permission p1, Permission p2) {
			if (p1 == null || p1.getIndexId() == null || p2 == null || p2.getIndexId() == null) {
				return 0;
			}
			return (int) (p1.getIndexId()-p2.getIndexId());
		}
	}

}
