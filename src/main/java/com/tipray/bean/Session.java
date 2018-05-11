package com.tipray.bean;

import com.tipray.bean.baseinfo.User;
import com.tipray.core.base.BaseBean;

import java.util.Date;

/**
 * 保存用户的会话
 *
 * @author chends
 */
public class Session extends BaseBean {
    private static final long serialVersionUID = 1L;
    private String uuid;
    /**
     * 当前登录用户
     */
    private User user;
    /**
     * 登录用户的客户端IP
     */
    private String ip;
    /**
     * 是否手机登录
     */
    private Integer isApp;
    /**
     * 登录时间
     */
    private Date loginDate;
    /**
     * 最近一次操作时间
     */
    private Date operateDate;
    /**
     * 此用户上一次登录的sessionId，用于判断是否异地登录
     */
    private String oldUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getIsApp() {
        return isApp;
    }

    public void setIsApp(Integer isApp) {
        if (isApp == null) {
            this.isApp = 0;
            return;
        }
        this.isApp = isApp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public String getOldUuid() {
        return oldUuid;
    }

    public void setOldUuid(String oldUuid) {
        this.oldUuid = oldUuid;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (uuid != null) {
            sb.append(", uuid='").append(uuid).append('\'');
        }
        if (user != null) {
            sb.append(", user=").append(user);
        }
        if (ip != null) {
            sb.append(", ip='").append(ip).append('\'');
        }
        if (isApp != null) {
            sb.append(", isApp=").append(isApp);
        }
        if (loginDate != null) {
            sb.append(", loginDate=").append(loginDate);
        }
        if (operateDate != null) {
            sb.append(", operateDate=").append(operateDate);
        }
        if (oldUuid != null) {
            sb.append(", oldUuid='").append(oldUuid).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"Session{");
            sb.append('}');
        }
        return sb.toString();
    }
}
