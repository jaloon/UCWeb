package com.tipray.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆实时状态
 *
 * @author chenlong
 * @version 1.0 2018-05-22
 */
public class VehicleRealtimeStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 车辆ID */
    private Long vehicleId;
    /** 车牌号 */
    private String carNumber;
    /** 车辆在线状态（0 离线，1 在线） */
    private Integer online;
    // /** 车辆状态 */
    // private Integer status;
    // /** 状态名称 */
    // private String statusName;
    /** 绑定状态 */
    private Integer paramStatus;
    /** 最后在线时间 */
    private Date lastOnline;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    // public Integer getStatus() {
    //     return status;
    // }
    //
    // public void setStatus(Integer status) {
    //     this.status = status;
    // }
    //
    // public String getStatusName() {
    //     return statusName;
    // }
    //
    // public void setStatusName(String statusName) {
    //     this.statusName = statusName;
    // }

    public Integer getParamStatus() {
        return paramStatus;
    }

    public void setParamStatus(Integer paramStatus) {
        this.paramStatus = paramStatus;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("VehicleRealtimeStatus{");
        sb.append("vehicleId=").append(vehicleId);
        sb.append(", carNumber='").append(carNumber).append('\'');
        sb.append(", online=").append(online);
        // sb.append(", status=").append(status);
        // sb.append(", statusName='").append(statusName).append('\'');
        sb.append(", paramStatus=").append(paramStatus);
        sb.append(", lastOnline=").append(lastOnline);
        sb.append('}');
        return sb.toString();
    }
}
