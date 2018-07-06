package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 手持机信息表
 *
 * @author chenlong
 * @version 1.0 2017-11-06
 */
public class Handset extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 手持机设备ID
     */
    private Integer deviceId;
    /**
     * 手持机版本
     */
    private Integer ver;
    /**
     * 所属加油站
     */
    private GasStation gasStation;
    /**
     * 负责人
     */
    private String director;
    /**
     * 联系号码
     */
    private String phone;
    /**
     * 身份证号
     */
    private String identityCard;

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public GasStation getGasStation() {
        return gasStation;
    }

    public void setGasStation(GasStation gasStation) {
        this.gasStation = gasStation;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
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
        if (deviceId != null) {
            sb.append(", deviceId=").append(deviceId);
        }
        if (ver != null) {
            sb.append(", ver=").append(ver);
        }
        if (gasStation != null) {
            sb.append(", gasStation=").append(gasStation);
        }
        if (director != null) {
            sb.append(", director='").append(director).append('\'');
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
            sb.replace(0,2,"Handset{");
            sb.append('}');
        }
        return sb.toString();
    }
}
