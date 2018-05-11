package com.tipray.bean;

import com.tipray.bean.baseinfo.Permission;

import java.util.ArrayList;
/**
 * 获取权限配置
 * 
 * @author         chenlong
 * @version        1.0  2017-10-10
 *
 */
public class PermissionConfig {
	private ArrayList<Permission> permission;

	public ArrayList<Permission> getPermission() {
		return permission;
	}

	public void setPermission(ArrayList<Permission> permission) {
		this.permission = permission;
	}

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PermissionConfig{");
        sb.append("permission=").append(permission);
        sb.append('}');
        return sb.toString();
    }
}
