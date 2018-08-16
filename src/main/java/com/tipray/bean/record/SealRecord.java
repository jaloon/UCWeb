package com.tipray.bean.record;

import com.tipray.bean.Record;

/**
 * 车辆施解封记录
 *
 * @author chenlong
 * @version 1.0 2017-12-18
 */
public class SealRecord extends Record {
    private static final long serialVersionUID = 1L;
    /**
     * 车辆ID
     */
    private Long carId;
    /**
     * 施解封类型
     */
    private Integer type;
    /**
     * 前状态
     */
    private Integer prestatus;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 认证类型
     */
    private Integer authtype;
    /**
     * 认证编号
     */
    private Long authid;
    /**
     * 是否报警
     */
    private String alarm;
    /**
     * 报警类型
     */
    private String alarmType;
    /**
     * 锁状态
     */
    private String lockStatus;

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPrestatus() {
        return prestatus;
    }

    public void setPrestatus(Integer prestatus) {
        this.prestatus = prestatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAuthtype() {
        return authtype;
    }

    public void setAuthtype(Integer authtype) {
        this.authtype = authtype;
    }

    public Long getAuthid() {
        return authid;
    }

    public void setAuthid(Long authid) {
        this.authid = authid;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(String lockStatus) {
        this.lockStatus = lockStatus;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (carId != null) {
            sb.append(", carId=").append(carId);
        }
        if (getCarNumber() != null) {
            sb.append(", carNumber='").append(getCarNumber()).append('\'');
        }
        if (type != null) {
            sb.append(", type=").append(type);
        }
        if (prestatus != null) {
            sb.append(", prestatus=").append(prestatus);
        }
        if (status != null) {
            sb.append(", status=").append(status);
        }
        if (authtype != null) {
            sb.append(", authtype=").append(authtype);
        }
        if (authid != null) {
            sb.append(", authid=").append(authid);
        }
        if (getStation() != null) {
            sb.append(", station='").append(getStation()).append('\'');
        }
        if (alarm != null) {
            sb.append(", alarm='").append(alarm).append('\'');
        }
        if (alarmType != null) {
            sb.append(", alarmType='").append(alarmType).append('\'');
        }
        if (lockStatus != null) {
            sb.append(", lockStatus='").append(lockStatus).append('\'');
        }
        if (getLongitude() != null) {
            sb.append(", longitude=").append(getLongitude());
        }
        if (getLatitude() != null) {
            sb.append(", latitude=").append(getLatitude());
        }
        if (getStation() != null) {
            sb.append(", station='").append(getStation()).append('\'');
        }
        if (getVelocity() != null) {
            sb.append(", velocity=").append(getVelocity());
        }
        if (getAngle() != null) {
            sb.append(", angle=").append(getAngle());
        }
        if (getTerminalAlarmStatus() != null) {
            sb.append(", terminalAlarmStatus=").append(getTerminalAlarmStatus());
        }
        if (getLockStatusInfo() != null) {
            sb.append(", lockStatusInfo=");
            sb.append('[');
            for (int i = 0, len = getLockStatusInfo().length; i < len; ++i)
                sb.append(i == 0 ? "" : ", ").append(getLockStatusInfo()[i]);
            sb.append(']');
        }
        if (getIsApp() != null) {
            sb.append(", isApp=").append(getIsApp());
        }
        if (getBegin() != null) {
            sb.append(", begin='").append(getBegin()).append('\'');
        }
        if (getEnd() != null) {
            sb.append(", end='").append(getEnd()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0, 2, "SealRecord{");
            sb.append('}');
        }
        return sb.toString();
    }
}
