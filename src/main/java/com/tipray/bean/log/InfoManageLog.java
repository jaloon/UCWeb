package com.tipray.bean.log;

import com.tipray.bean.baseinfo.User;
import com.tipray.core.base.BaseBean;

/**
 * 信息管理日志
 *
 * @author chenlong
 * @version 1.0 2018-04-08
 */
public class InfoManageLog extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 操作员
     */
    private User user;
    /**
     * 日志类型
     */
    private Integer type;
    /**
     * 操作描述
     */
    private String description;

    /**
     * 开始时间
     */
    private String begin;
    /**
     * 结束时间
     */
    private String end;

    public InfoManageLog() {
        super();
    }

    public InfoManageLog(User user) {
        super();
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (user != null) {
            sb.append(", user=").append(user);
        }
        if (type != null) {
            sb.append(", type=").append(type);
        }
        if (description != null) {
            sb.append(", description='").append(description).append('\'');
        }
        if (begin != null) {
            sb.append(", begin='").append(begin).append('\'');
        }
        if (end != null) {
            sb.append(", end='").append(end).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"InfoManageLog{");
            sb.append('}');
        }
        return sb.toString();
    }
}
