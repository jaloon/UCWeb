package com.tipray.bean.record;

import com.tipray.bean.Record;

/**
 * 报警记录
 *
 * @author chenlong
 * @version 1.0 2017-12-18
 */
public class AlarmRecord extends Record {
    private static final long serialVersionUID = 1L;
    /**
     * 车辆ID
     */
    private Long vehicleId;
    /**
     * 车载终端设备ID
     */
    private Integer terminalId;
    /**
     * 设备类型（1:车台，2:锁）
     */
    private Integer deviceType;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 报警类型
     */
    private Integer type;
    /**
     * 报警类型名称
     */
    private String typeName;
    /**
     * 报警状态
     */
    private String status;
    /**
     * 锁状态
     */
    private String lockStatus;
    /**
     * 报警业务类型 （100 消除报警，110 报警(default)，120 延时推送报警，130 忽略报警）
     */
    private int biz = 110;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Integer terminalId) {
        this.terminalId = terminalId;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(String lockStatus) {
        this.lockStatus = lockStatus;
    }

    public int getBiz() {
        return biz;
    }

    public void setBiz(int biz) {
        this.biz = biz;
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
        if (vehicleId != null) {
            sb.append(", vehicleId=").append(vehicleId);
        }
        if (terminalId != null) {
            sb.append(", terminalId=").append(terminalId);
        }
        if (deviceType != null) {
            sb.append(", deviceType=").append(deviceType);
        }
        if (deviceId != null) {
            sb.append(", deviceId=").append(deviceId);
        }
        if (type != null) {
            sb.append(", type=").append(type);
        }
        if (typeName != null) {
            sb.append(", typeName='").append(typeName).append('\'');
        }
        if (status != null) {
            sb.append(", status='").append(status).append('\'');
        }
        if (lockStatus != null) {
            sb.append(", lockStatus='").append(lockStatus).append('\'');
        }
        sb.append(", biz=").append(biz);
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
        sb.replace(0, 2, "AlarmRecord{");
        sb.append('}');
        return sb.toString();
    }
}