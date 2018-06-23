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
    /** 站点类型为空 */
	STATION_TYPE_NULL(6, "站点类型为空！"),
    /** 站点类型无效 */
    STATION_TYPE_INVALID(7, "站点类型无效！"),
	/** 站点ID为空 */
	STATION_ID_NULL(8, "站点ID为空"),
	/** 车辆状态为空 */
	CAR_STATUS_NULL(9, "车辆状态为空"),
	/** 车辆状态无效 */
	CAR_STATUS_INVALID(10, "车辆状态无效！"),
	/** 车辆离线 */
    VEHICLE_OFFLINE(11, "车辆离线！"),
	/** 油库未指定道闸转发读卡器 */
	DEPOT_BARRIER_READER_NULL(12, "油库未指定道闸转发读卡器！"),
    /** 油库道闸转发读卡器太多 */
    DEPOT_BARRIER_READER_TOO_MUCH(13, "油库道闸转发读卡器太多！"),
	/** 请求发起时间为空 */
	REQUEST_TIME_NULL(14, "请求发起时间为空！"),
	/** 时间格式不正确 */
	TIME_FORMAT_INVALID(15, "时间格式不正确！");

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
