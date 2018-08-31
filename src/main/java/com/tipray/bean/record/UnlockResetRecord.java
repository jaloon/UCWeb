package com.tipray.bean.record;

import com.tipray.bean.Record;

/**
 * 开锁重置记录
 *
 * @author chenlong
 * @version 1.0 2018-08-29
 */
public class UnlockResetRecord extends Record {
    private static final long serialVersionUID = 1L;
    /**
     * 锁设备ID
     */
    private Integer lockId;
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
     * 状态 (0：未完成 | 1：远程操作请求中 | 2：远程操作完成 | 3：车台主动重置完成)
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
     * 重置上报时间
     */
    private String resetReportTime;

    public Integer getLockId() {
        return lockId;
    }

    public void setLockId(Integer lockId) {
        this.lockId = lockId;
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

    public String getResetReportTime() {
        return resetReportTime;
    }

    public void setResetReportTime(String resetReportTime) {
        this.resetReportTime = resetReportTime;
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
            sb.append(", lockId=").append(lockId);
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
        if (authtype != null) {
            sb.append(", authtype=").append(authtype);
        }
        if (authid != null) {
            sb.append(", authid=").append(authid);
        }
        if (resetReportTime != null) {
            sb.append(", resetReportTime='").append(resetReportTime).append('\'');
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
            sb.replace(0, 2, "UnlockResetRecord{");
            sb.append('}');
        }
        return sb.toString();
    }
}
