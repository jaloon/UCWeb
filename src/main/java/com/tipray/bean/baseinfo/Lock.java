package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 锁
 *
 * @author chenlong
 * @version 1.0 2017-11-06
 */
public class Lock extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 车辆ID
     */
    private Long carId;
    /**
     * 锁序号
     */
    private Integer index;
    /**
     * 锁设备ID
     */
    private Integer lockId;
    /**
     * 仓位
     */
    private Integer seat;
    /**
     * 仓位名称
     */
    private String seatName;
    /**
     * 相同仓位锁索引号
     */
    private Integer seatIndex;
    /**
     * 仓号
     */
    private Integer storeId;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 是否允许开锁（1 否，2 是）
     */
    private Integer allowOpen;
    /**
     * 绑定状态（ 0：未知（非法值） | 1：待车台触发确认 | 2：车台已触发确认 ）
     */
    private Integer bindStatus;
    /**
     * 设备备注
     */
    private String deviceRemark;

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getLockId() {
        return lockId;
    }

    public void setLockId(Integer lockId) {
        this.lockId = lockId;
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

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Integer getAllowOpen() {
        return allowOpen;
    }

    public void setAllowOpen(Integer allowOpen) {
        this.allowOpen = allowOpen;
    }

    public Integer getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(Integer bindStatus) {
        this.bindStatus = bindStatus;
    }

    public String getDeviceRemark() {
        return deviceRemark;
    }

    public void setDeviceRemark(String deviceRemark) {
        this.deviceRemark = deviceRemark;
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
        if (index != null) {
            sb.append(", index=").append(index);
        }
        if (lockId != null) {
            sb.append(", lockId=").append(lockId);
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
        if (storeId != null) {
            sb.append(", storeId=").append(storeId);
        }
        if (carNumber != null) {
            sb.append(", carNumber='").append(carNumber).append('\'');
        }
        if (allowOpen != null) {
            sb.append(", allowOpen=").append(allowOpen);
        }
        if (bindStatus != null) {
            sb.append(", bindStatus=").append(bindStatus);
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (deviceRemark != null) {
            sb.append(", deviceRemark='").append(deviceRemark).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"Lock{");
            sb.append('}');
        }
        return sb.toString();
    }
}
