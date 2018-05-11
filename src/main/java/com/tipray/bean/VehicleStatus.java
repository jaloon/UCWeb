package com.tipray.bean;

import com.tipray.core.base.BaseBean;

/**
 * 车辆状态
 * 
 * @author chenlong
 * @version 1.0 2017-12-25
 *
 */
public class VehicleStatus extends BaseBean {
	private static final long serialVersionUID = 1L;
	/** 车牌号 */
	private String carNumber;
	/** 车辆在线状态（0 离线，1 在线） */
	private Integer online;
	/** 车辆状态 */
	private Integer status;
	/** 状态名称 */
	private String statusName;

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("VehicleStatus{");
        sb.append("carNumber='").append(carNumber).append('\'');
        sb.append(", online=").append(online);
        sb.append(", status=").append(status);
        sb.append(", statusName='").append(statusName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
