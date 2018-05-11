/*
 * Role.java
 * Copyright(C) 厦门天锐科技有限公司
 * All rights reserved.
 * -----------------------------------------------
 * 2016-01-13 Created
 */
package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

import java.util.List;

/**
 * 角色表
 *
 * @author chends
 * @version 1.0 2016-01-13
 */
public class Role extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 是否超级管理员{1:是,0:否}
     */
    private Boolean isSuper;
    /**
     * 是否APP角色{1:是,0:否}
     */
    private Integer isApp;
    /**
     * 功能权限，权限Id集合，多个权限用逗号相隔
     */
    private String permissionIds;
    private List<Permission> permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Boolean getIsSuper() {
        return isSuper;
    }

    public void setIsSuper(Boolean isSuper) {
        this.isSuper = isSuper;
    }

    public Integer getIsApp() {
        return isApp;
    }

    public void setIsApp(Integer isApp) {
        this.isApp = isApp;
    }

    public String getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(String permissionIds) {
        this.permissionIds = permissionIds;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (name != null) {
            sb.append(", name='").append(name).append('\'');
        }
        if (isSuper != null) {
            sb.append(", isSuper=").append(isSuper);
        }
        if (isApp != null) {
            sb.append(", isApp=").append(isApp);
        }
        if (permissionIds != null) {
            sb.append(", permissionIds='").append(permissionIds).append('\'');
        }
        if (permissions != null) {
            sb.append(", permissions=").append(permissions);
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"Role{");
            sb.append('}');
        }
        return sb.toString();
    }
}