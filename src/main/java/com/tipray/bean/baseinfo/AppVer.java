package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * APP版本信息
 *
 * @author chenlong
 * @version 1.0 2018-07-26
 */
public class AppVer extends BaseBean {
    private static final long serialVersionUID = 1L;
    /** 用户中心ID */
    private Long centerId;
    /** 用户中心名称 */
    private String centerName;
    /** 应用标识（pltone_e_seal | pltone_e_seal_accredit | ...） */
    private String appid;
    /** 设备系统（Android | iOS | ...） */
    private String system;
    /** 指定WebAPP版本 */
    private String assignVer;
    /** 允许运行的最低WebAPP版本 */
    private String minVer;

    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getAssignVer() {
        return assignVer;
    }

    public void setAssignVer(String assignVer) {
        this.assignVer = assignVer;
    }

    public String getMinVer() {
        return minVer;
    }

    public void setMinVer(String minVer) {
        this.minVer = minVer;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (centerId != null) {
            sb.append("centerId=").append(centerId);
        }
        if (centerName != null) {
            sb.append(", centerName='").append(centerName).append('\'');
        }
        if (appid != null) {
            sb.append(", appid='").append(appid).append('\'');
        }
        if (system != null) {
            sb.append(", system='").append(system).append('\'');
        }
        if (assignVer != null) {
            sb.append(", assignVer='").append(assignVer).append('\'');
        }
        if (minVer != null) {
            sb.append(", minVer='").append(minVer).append('\'');
        }
        if (getCreateDate() != null) {
            sb.append(", createDate=").append(getCreateDate());
        }
        if (sb.length() > 0) {
            sb.replace(0, 2, "AppVer{");
            sb.append('}');
        }
        return sb.toString();
    }
}
