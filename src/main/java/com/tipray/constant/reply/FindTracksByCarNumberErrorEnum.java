package com.tipray.constant.reply;

/**
 * 根据车牌号获取车辆轨迹错误枚举
 * 
 * @author chenlong
 * @version 1.0 2018-04-02
 *
 */
public enum FindTracksByCarNumberErrorEnum {
	/** 车牌号为空 */
	CAR_NUMBER_NULL(1, "车牌号为空！"),
	/** 轨迹开始时间为空 */
	BEGIN_TIME_NULL(2, "轨迹开始时间为空！"),
	/** 时间格式不正确 */
	TIME_FORMAT_INVALID(3, "时间格式不正确！"),
	/** 时间超出查询范围（一天内） */
	TIME_OUT_OF_SCOPE(4, "时间超出查询范围（一天内）！"),
	/** 时段内无所查车辆轨迹 */
	TRACK_NULL(5, "时段内无所查车辆轨迹！");
	
	private int code;
	private String msg;

	public int code() {
		return code;
	}

	public String msg() {
		return msg;
	}

	FindTracksByCarNumberErrorEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	/**
	 * 根据code获取轨迹获取错误枚举
	 * 
	 * @param code
	 *            {@link int}
	 * @return
	 */
	public static FindTracksByCarNumberErrorEnum getByCode(int code) {
		for (FindTracksByCarNumberErrorEnum findTracksError : values()) {
			if (findTracksError.code == code) {
				return findTracksError;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return msg;
	}
}
