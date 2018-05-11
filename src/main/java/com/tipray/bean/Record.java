package com.tipray.bean;

import com.tipray.core.base.BaseBean;

/**
 * 记录查询基类
 *
 * @author chenlong
 * @version 1.0 2017-12-18
 */
public class Record extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 经度
     */
    private Float longitude;
    /**
     * 纬度
     */
    private Float latitude;
    /**
     * 站点名称
     */
    private String station;
    /**
     * 速度
     */
    private Integer velocity;
    /**
     * 角度
     */
    private Integer angle;
    /**
     * 车台报警状态
     */
    private Integer terminalAlarmStatus;
    /**
     * 锁状态信息流
     */
    private byte[] lockStatusInfo;
    /**
     * 是否APP操作
     */
    private Integer isApp;
    /**
     * 开始时间
     */
    private String begin;
    /**
     * 结束时间
     */
    private String end;

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
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

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Integer getVelocity() {
        return velocity;
    }

    public void setVelocity(Integer velocity) {
        this.velocity = velocity;
    }

    public Integer getAngle() {
        return angle;
    }

    public void setAngle(Integer angle) {
        this.angle = angle;
    }

    public Integer getTerminalAlarmStatus() {
        return terminalAlarmStatus;
    }

    public void setTerminalAlarmStatus(Integer terminalAlarmStatus) {
        this.terminalAlarmStatus = terminalAlarmStatus;
    }

    public byte[] getLockStatusInfo() {
        return lockStatusInfo;
    }

    public void setLockStatusInfo(byte[] lockStatusInfo) {
        this.lockStatusInfo = lockStatusInfo;
    }

    public Integer getIsApp() {
        return isApp;
    }

    public void setIsApp(Integer isApp) {
        this.isApp = isApp;
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
        if (carNumber != null) {
            sb.append(", carNumber='").append(carNumber).append('\'');
        }
        if (longitude != null) {
            sb.append(", longitude=").append(longitude);
        }
        if (latitude != null) {
            sb.append(", latitude=").append(latitude);
        }
        if (station != null) {
            sb.append(", station='").append(station).append('\'');
        }
        if (velocity != null) {
            sb.append(", velocity=").append(velocity);
        }
        if (angle != null) {
            sb.append(", angle=").append(angle);
        }
        if (terminalAlarmStatus != null) {
            sb.append(", terminalAlarmStatus=").append(terminalAlarmStatus);
        }
        if (lockStatusInfo != null) {
            sb.append(", lockStatusInfo=");
            sb.append('[');
            for (int i = 0, len = lockStatusInfo.length; i < len; ++i)
                sb.append(i == 0 ? "" : ", ").append(lockStatusInfo[i]);
            sb.append(']');
        }
        if (isApp != null) {
            sb.append(", isApp=").append(isApp);
        }
        if (begin != null) {
            sb.append(", begin='").append(begin).append('\'');
        }
        if (end != null) {
            sb.append(", end='").append(end).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0, 2, "Record{");
            sb.append('}');
        }
        return sb.toString();
    }
}
