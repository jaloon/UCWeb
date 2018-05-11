package com.tipray.bean.log;

import com.tipray.bean.ResponseMsg;
import com.tipray.bean.baseinfo.User;
import com.tipray.core.base.BaseBean;

/**
 * 车辆管理日志
 *
 * @author chenlong
 * @version 1.0 2018-04-08
 */
public class VehicleManageLog extends BaseBean {
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
     * 操作结果
     */
    private String result;
    /**
     * 是否手机操作
     */
    private Integer isApp = 0;
    /**
     * UDP业务ID
     */
    private Short udpBizId;
    /**
     * 请求令牌UUID
     */
    private String token;
    /**
     * UDP回复信息
     */
    private ResponseMsg responseMsg;

    /**
     * 开始时间
     */
    private String begin;
    /**
     * 结束时间
     */
    private String end;

    public VehicleManageLog() {
        super();
    }

    public VehicleManageLog(Long id) {
        super();
        this.setId(id);
    }

    public VehicleManageLog(User user) {
        super();
        this.user = user;
    }

    public VehicleManageLog(User user, Integer isApp) {
        super();
        this.user = user;
        setIsApp(isApp);
    }

    public VehicleManageLog(User user, Integer isApp, short udpBizId) {
        super();
        this.user = user;
        setIsApp(isApp);
        this.udpBizId = udpBizId;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getIsApp() {
        return isApp;
    }

    public void setIsApp(Integer isApp) {
        if (isApp != null) {
            this.isApp = isApp;
        }
    }

    public Short getUdpBizId() {
        return udpBizId;
    }

    public void setUdpBizId(Short udpBizId) {
        this.udpBizId = udpBizId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ResponseMsg getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(ResponseMsg responseMsg) {
        this.responseMsg = responseMsg;
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
        if (result != null) {
            sb.append(", result='").append(result).append('\'');
        }
        if (isApp != null) {
            sb.append(", isApp=").append(isApp);
        }
        if (udpBizId != null) {
            sb.append(", udpBizId=").append(udpBizId);
        }
        if (token != null) {
            sb.append(", token='").append(token).append('\'');
        }
        if (user != null) {
            sb.append(", responseMsg=").append(responseMsg);
        }
        if (begin != null) {
            sb.append(", begin='").append(begin).append('\'');
        }
        if (end != null) {
            sb.append(", end='").append(end).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"VehicleManageLog{");
            sb.append('}');
        }
        return sb.toString();
    }

}
