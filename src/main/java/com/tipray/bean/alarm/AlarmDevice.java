package com.tipray.bean.alarm;

import java.io.Serializable;

/**
 * 报警设备
 *
 * @author chenlong
 * @version 1.0 2018-08-09
 */
public class AlarmDevice implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 车辆ID
     */
    private long vehicleId;
    /**
     * 设备类型（1 车台，2 锁）
     */
    private int deviceType;
    /**
     * 设备ID
     */
    private int deviceId;
    /**
     * 锁信息表记录Id
     */
    private int lockId;
    /**
     * 报警类型
     */
    private int alarmType;

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
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

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AlarmDevice{");
        sb.append("vehicleId=").append(vehicleId);
        sb.append(", deviceType=").append(deviceType);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", lockId=").append(lockId);
        sb.append(", alarmType=").append(alarmType);
        sb.append('}');
        return sb.toString();
    }
}
