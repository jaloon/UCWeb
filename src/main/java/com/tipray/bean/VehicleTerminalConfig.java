package com.tipray.bean;

import java.io.Serializable;

/**
 * 车台配置Bean
 * 
 * @author chenlong
 * @version 1.0 2018-03-08
 *
 */
public class VehicleTerminalConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 配置范围：0 所有车辆，1 单部车辆 */
	private Integer scope;
	/** 车牌号 */
	private String carNumber;
	/** 车载终端ID */
	private Integer terminalId;
	/** 轨迹采集间隔（秒） */
	private Integer scanInterval;
	/** 默认轨迹上报间隔（秒） */
	private Integer uploadInterval;
	/** 轨迹生成距离间隔（米） */
	private Integer generateDistance;
	/** 车辆状况：0 车不存在，1 正常，2 未绑定车台，3 配置范围越界 */
	private Integer carState;
	/** 车台配置是否有更新：0 否，1 是 */
	private Integer isUpdate;

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public Integer getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(Integer terminalId) {
		this.terminalId = terminalId;
	}

	public Integer getScanInterval() {
		return scanInterval;
	}

	public void setScanInterval(Integer scanInterval) {
		this.scanInterval = scanInterval;
	}

	public Integer getUploadInterval() {
		return uploadInterval;
	}

	public void setUploadInterval(Integer uploadInterval) {
		this.uploadInterval = uploadInterval;
	}

	public Integer getGenerateDistance() {
		return generateDistance;
	}

	public void setGenerateDistance(Integer generateDistance) {
		this.generateDistance = generateDistance;
	}

	public Integer getCarState() {
		return carState;
	}

	public void setCarState(Integer carState) {
		this.carState = carState;
	}

	public Integer getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Integer isUpdate) {
		this.isUpdate = isUpdate;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		if (scope != null) {
            sb.append(", scope=").append(scope);
        }
        if (carNumber != null && !carNumber.trim().isEmpty()) {
            sb.append(", carNumber='").append(carNumber).append('\'');
        }
        if (terminalId != null) {
		sb.append(", terminalId=").append(terminalId);
        }
        if (scanInterval != null) {
		sb.append(", scanInterval=").append(scanInterval);
        }
        if (uploadInterval != null) {
		sb.append(", uploadInterval=").append(uploadInterval);
        }
        if (generateDistance != null) {
		sb.append(", generateDistance=").append(generateDistance);
        }
        if (carState != null) {
		sb.append(", carState=").append(carState);
        }
        if (isUpdate != null) {
		sb.append(", isUpdate=").append(isUpdate);
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"VehicleTerminalConfig{");
            sb.append('}');
        }
		return sb.toString();
	}
}
