package com.tipray.constant.reply;

/**
 * 远程操作错误枚举
 * 
 * @author chenlong
 * @version 1.0 2018-03-23
 *
 */
public enum RemoteControlErrorEnum {
	/** 车牌号为空 */
	CARNUMBER_NULL(1, "车牌号为空！"),
	/** 车辆不存在 */
	VEHICLE_INVALID(2, "车辆不存在！"),
	/** 车辆未绑定车台 */
	VEHICLE_UNBINDED(3, "车辆未绑定车台！"),
	/** 远程操作类型为空 */
	CONTROL_TYPE_NULL(4, "远程操作类型为空"),
	/** 远程操作类型无效 */
	CONTROL_TYPE_INVALID(5, "远程操作类型无效！"),
	/** 站点ID为空 */
	STATION_ID_NULL(6, "站点ID为空"),
	/** 车辆状态为空 */
	CAR_STATUS_NULL(7, "车辆状态为空"),
	/** 车辆状态无效 */
	CAR_STATUS_INVALID(8, "车辆状态无效！"),
	/** 车辆离线 */
    VEHICLE_OFFLINE(9, "车辆离线！"),
	/** 请求发起时间为空 */
	REQUEST_TIME_NULL(9, "请求发起时间为空！"),
	/** 时间格式不正确 */
	TIME_FORMAT_INVALID(10, "时间格式不正确！");

	private int code;
	private String msg;

	public int code() {
		return code;
	}

	public String msg() {
		return msg;
	}

	RemoteControlErrorEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	/**
	 * 根据code获取远程操作错误枚举
	 * 
	 * @param code
	 *            {@link int}
	 * @return
	 */
	public static RemoteControlErrorEnum getByCode(int code) {
		for (RemoteControlErrorEnum remoteControlError : values()) {
			if (remoteControlError.code == code) {
				return remoteControlError;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return msg;
	}
}
