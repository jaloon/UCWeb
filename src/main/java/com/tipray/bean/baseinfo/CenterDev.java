package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 中心手机UUID
 *
 * @author chenlong
 * @version 1.0 2018-09-12
 */
public class CenterDev extends BaseBean {
    private static final long serialVersionUID = 1L;
    /** 手机唯一码 */
    private String uuid;
    /** 用户中心ID */
    private Long centerId;
    /** 用户中心名称 */
    private String centerName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CenterDev{");
        sb.append("id=").append(getId());
        sb.append(", uuid='").append(uuid).append('\'');
        sb.append(", centerId=").append(centerId);
        sb.append(", centerName='").append(centerName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
