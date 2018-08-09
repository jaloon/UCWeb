package com.tipray.bean.alarm;

import java.io.Serializable;

/**
 * 报警信息
 *
 * @author chenlong
 * @version 1.0 2018-08-09
 */
public class AlarmInfo implements Serializable, Comparable<AlarmInfo> {
    private static final long serialVersionUID = 1L;
    /**
     * 报警ID
     */
    private int alarmId;
    /**
     * 车辆ID
     */
    private int vehicleId;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 设备类型（1 车台，2 锁）
     */
    private int deviceType;
    /**
     * 设备ID
     */
    private int deviceId;
    /**
     * 报警类型
     */
    private int alarmType;
    /**
     * 报警名称
     */
    private String alarmName;
    /**
     * 报警站点
     */
    private String station;
    /**
     * 报警时间
     */
    private String alarmTime;
    /**
     * 报警轨迹ID
     */
    private long trackId;
    /**
     * 报警锁信息
     */
    private AlarmLock alarmLock;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AlarmInfo{");
        sb.append("alarmId=").append(alarmId);
        sb.append(", vehicleId=").append(vehicleId);
        sb.append(", carNumber='").append(carNumber).append('\'');
        sb.append(", deviceType=").append(deviceType);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", alarmType=").append(alarmType);
        sb.append(", alarmName='").append(alarmName).append('\'');
        sb.append(", station='").append(station).append('\'');
        sb.append(", alarmTime='").append(alarmTime).append('\'');
        sb.append(", trackId=").append(trackId);
        sb.append(", alarmLock=").append(alarmLock);
        sb.append('}');
        return sb.toString();
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public AlarmLock getAlarmLock() {
        return alarmLock;
    }

    public void setAlarmLock(AlarmLock alarmLock) {
        this.alarmLock = alarmLock;
    }

    @Override
    public int compareTo(AlarmInfo o) {
        if (o == null) {
            return -1;
        }
        if (this.alarmName.equals(o.alarmName)) {
            return o.alarmId - this.alarmId;
        }
        return o.alarmName.compareTo(this.alarmName);
    }
}