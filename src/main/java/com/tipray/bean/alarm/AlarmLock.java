package com.tipray.bean.alarm;

import java.io.Serializable;

/**
 * 报警锁信息
 *
 * @author chenlong
 * @version 1.0 2018-08-09
 */
public class AlarmLock implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 锁信息记录ID
     */
    private int lockId;
    /**
     * 锁设备ID
     */
    private int deviceId;
    /**
     * 仓号
     */
    private int storeId;
    /**
     * 仓位（1 上仓，2 下仓）
     */
    private int seat;
    /**
     * 同仓位索引
     */
    private int seatIndex;
    /**
     * 开关状态
     */
    private int switchStatus;

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }

    public int getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(int switchStatus) {
        this.switchStatus = switchStatus;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AlarmLock{");
        sb.append("lockId=").append(lockId);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", storeId=").append(storeId);
        sb.append(", seat=").append(seat);
        sb.append(", seatIndex=").append(seatIndex);
        sb.append(", switchStatus=").append(switchStatus);
        sb.append('}');
        return sb.toString();
    }
}
