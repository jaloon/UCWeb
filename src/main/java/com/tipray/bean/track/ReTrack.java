package com.tipray.bean.track;

import com.tipray.bean.Record;

import java.util.Map;

/**
 * 车辆轨迹（轨迹回放）
 *
 * @author chenlong
 * @version 1.0 2018-01-22
 */
public class ReTrack extends Record {
    private static final long serialVersionUID = 1L;
    private String carStatus;
    private boolean alarm;
    private Map<Integer, String> alarmMap;

    public String getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public Map<Integer, String> getAlarmMap() {
        return alarmMap;
    }

    public void setAlarmMap(Map<Integer, String> alarmMap) {
        this.alarmMap = alarmMap;
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
        if (carStatus != null) {
            sb.append("carStatus='").append(carStatus).append('\'');
        }
        sb.append(", alarm=").append(alarm);
        if (alarmMap != null) {
            sb.append(", alarmMap=").append(alarmMap);
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
        sb.replace(0, 2, "ReTrack{");
        sb.append('}');
        return sb.toString();
    }
}
