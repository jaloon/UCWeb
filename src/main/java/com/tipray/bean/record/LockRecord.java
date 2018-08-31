package com.tipray.bean.record;

import com.tipray.bean.Record;

/**
 * 锁动作记录
 *
 * @author chenlong
 * @version 1.0 2017-12-18
 */
public class LockRecord extends Record {
    private static final long serialVersionUID = 1L;
    /**
     * 锁设备ID
     */
    private Integer lockId;
    /**
     * 锁索引
     */
    private Integer index;
    /**
     * 仓号
     */
    private Integer storeId;
    /**
     * 仓位
     */
    private Integer seat;
    /**
     * 仓位 名称
     */
    private String seatName;
    /**
     * 同仓位锁序号
     */
    private Integer seatIndex;
    /**
     * 锁状态
     */
    private Integer status;
    /**
     * 锁状态 名称
     */
    private String statusName;
    /**
     * 锁开关上报时间
     */
    private String changeReportTime;
    /**
     * 报警类型
     */
    private String alarm;

    public Integer getLockId() {
        return lockId;
    }

    public void setLockId(Integer lockId) {
        this.lockId = lockId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getSeat() {
        return seat;
    }

    public void setSeat(Integer seat) {
        this.seat = seat;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public Integer getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(Integer seatIndex) {
        this.seatIndex = seatIndex;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getChangeReportTime() {
        return changeReportTime;
    }

    public void setChangeReportTime(String changeReportTime) {
        this.changeReportTime = changeReportTime;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
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
        if (lockId != null) {
            sb.append("lockId=").append(lockId);
        }
        if (index != null) {
            sb.append(", index=").append(index);
        }
        if (storeId != null) {
            sb.append(", storeId=").append(storeId);
        }
        if (seat != null) {
            sb.append(", seat=").append(seat);
        }
        if (seatName != null) {
            sb.append(", seatName='").append(seatName).append('\'');
        }
        if (seatIndex != null) {
            sb.append(", seatIndex=").append(seatIndex);
        }
        if (status != null) {
            sb.append(", status=").append(status);
        }
        if (statusName != null) {
            sb.append(", statusName='").append(statusName).append('\'');
        }
        if (changeReportTime != null) {
            sb.append(", changeReportTime='").append(changeReportTime).append('\'');
        }
        if (alarm != null) {
            sb.append(", alarm='").append(alarm).append('\'');
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
            sb.replace(0, 2, "AlarmRecord{");
            sb.append('}');
        }
        return sb.toString();
    }
}