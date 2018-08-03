package com.tipray.bean.record;

import java.io.Serializable;
import java.util.Date;

/**
 * 授权记录
 *
 * @author chenlong
 * @version 1.0 2018-07-12
 */
public class AuthorizedRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 记录ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String userName;
    /** 授权码 */
    private Integer authCode;
    /** 授权时间 */
    private String authTime;
    /** 记录上报时间 */
    private Date reportTime;
    /** 是否手机操作 */
    private Integer isApp;
    /** 定位是否有效 */
    private Integer isLocationValid;
    /** 经度 */
    private Float longitude;
    /** 纬度 */
    private Float latitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAuthCode() {
        return authCode;
    }

    public void setAuthCode(Integer authCode) {
        this.authCode = authCode;
    }

    public String getAuthTime() {
        return authTime;
    }

    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public Integer getIsApp() {
        return isApp;
    }

    public void setIsApp(Integer isApp) {
        this.isApp = isApp;
    }

    public Integer getIsLocationValid() {
        return isLocationValid;
    }

    public void setIsLocationValid(Integer isLocationValid) {
        this.isLocationValid = isLocationValid;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthorizedRecord{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", authCode=").append(authCode);
        sb.append(", authTime='").append(authTime).append('\'');
        sb.append(", reportTime=").append(reportTime);
        sb.append(", isApp=").append(isApp);
        sb.append(", isLocationValid=").append(isLocationValid);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append('}');
        return sb.toString();
    }
}
