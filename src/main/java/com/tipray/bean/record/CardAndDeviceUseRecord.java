package com.tipray.bean.record;

import com.tipray.bean.Record;

/**
 * 卡及设备使用记录
 *
 * @author chenlong
 * @version 1.0 2017-12-27
 */
public class CardAndDeviceUseRecord extends Record {
    private static final long serialVersionUID = 1L;
    /**
     * 设备ID
     */
    private Integer devId;
    /**
     * 设备类型
     */
    private Integer type;
    /**
     * 设备类型名称
     */
    private String typeName;
    /**
     * 报警
     */
    private String alarm;
    /**
     * 锁状态
     */
    private String lockStatus;

    public Integer getDevId() {
        return devId;
    }

    public void setDevId(Integer devId) {
        this.devId = devId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
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
        if (getCarNumber() != null) {
            sb.append(", carNumber='").append(getCarNumber()).append('\'');
        }
        if (devId != null) {
            sb.append(", devId=").append(devId);
        }
        if (type != null) {
            sb.append(", type=").append(type);
        }
        if (typeName != null) {
            sb.append(", typeName='").append(typeName).append('\'');
        }
        if (alarm != null) {
            sb.append(", alarm='").append(alarm).append('\'');
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
            sb.replace(0, 2, "CardAndDeviceUseRecord{");
            sb.append('}');
        }
        return sb.toString();
    }
}
