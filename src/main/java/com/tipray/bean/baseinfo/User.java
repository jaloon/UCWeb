/*
 * User.java
 * Copyright(C) 厦门天锐科技有限公司
 * All rights reserved.
 * -----------------------------------------------
 * 2017-11-06 Created
 */
package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 操作员信息表
 *
 * @author chenlong
 * @version 1.0 2017-11-06
 */
public class User extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 角色
     */
    private Role role;
    /**
     * APP角色
     */
    private Role appRole;
    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 姓名
     */
    private String name;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 身份证号
     */
    private String identityCard;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getAppRole() {
        return appRole;
    }

    public void setAppRole(Role appRole) {
        this.appRole = appRole;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (role != null) {
            sb.append(", role=").append(role);
        }
        if (appRole != null) {
            sb.append(", appRole=").append(appRole);
        }
        if (account != null) {
            sb.append(", account='").append(account).append('\'');
        }
        if (password != null) {
            sb.append(", password='").append(password).append('\'');
        }
        if (name != null) {
            sb.append(", name='").append(name).append('\'');
        }
        if (phone != null) {
            sb.append(", phone='").append(phone).append('\'');
        }
        if (identityCard != null) {
            sb.append(", identityCard='").append(identityCard).append('\'');
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"User{");
            sb.append('}');
        }
        return sb.toString();
    }
}