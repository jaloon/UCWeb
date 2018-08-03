package com.tipray.net.constant;

/**
 * UDP应答错误常量
 * 
 * @author chenlong
 * @version 1.0 2018-03-15
 *
 */
public class UdpReplyErrorTag {
	/** 无错误（通用错误和业务错误无错都为0） */
	public static final short NONE_ERROR = 0;

	// 通用错误
	/** 协议版本不合法 */
	public static final short COMMON_ERROR_PROTOCOL_VERSION = 1;
	/** 消息头太短 */
	public static final short COMMON_ERROR_MSG_HEAD_TOO_SHORT = 2;
	/** 数据库操作错误 */
	public static final short COMMON_ERROR_DB = 3;
	/** 源终端地址（设备ID）不合法（值错误或者不属于本中心监管设备或者设备不可用） */
	public static final short COMMON_ERROR_SRC_TERM_ADDR = 4;
	/** 目的终端地址（设备ID）不合法（值错误或者不属于本中心监管设备或者设备不可用） */
	public static final short COMMON_ERROR_DEST_TERM_ADDR = 5;
	/** 消息标识不合法 */
	public static final short COMMON_ERROR_MSG_TAG = 6;
	/** 数据体长度超过最大长度 */
	public static final short COMMON_ERROR_DATA_LEN_OVERFLOW = 7;
	/** 数据体长度不合法 */
	public static final short COMMON_ERROR_DATA_LEN_INEQUALITY = 8;
	/** 网络错误 */
	public static final short COMMON_ERROR_NETWORK = 9;
	/** 数据体版本号不合法 */
	public static final short COMMON_ERROR_DATA_VERSION = 10;
	/** 序列号重复或者为0 */
	public static final short COMMON_ERROR_SERIAL_NUMBER = 11;
	/** 设备已登陆，发现新的端口号登陆中心 */
	public static final short COMMON_ERROR_DUPLICATE_LOGIN = 12;
	/** 设备未登陆 */
	public static final short COMMON_ERROR_UN_LOGIN = 13;
	/** 设备登陆失败 */
	public static final short COMMON_ERROR_LOGIN_FAIL = 14;
	/** 设备离线 */
	public static final short COMMON_ERROR_DEVICE_OFFLINE = 15;
	/** Http通讯错误 */
	public static final short COMMON_ERROR_HTTP = 16;
	/** 车辆信息错误：中心不存在该车辆信息,或者车辆未绑定车台 */
	public static final short COMMON_ERROR_VEHICLE = 17;
	/** 公共参数数量不对 */
	public static final short COMMON_ERROR_COMMON_PARAM_NUM = 18;
	/** 车辆信息未完成绑定 */
	public static final short COMMON_ERROR_VEHICLE_BIND_FAIL = 19;
	/** 锁信息未完成绑定 */
	public static final short COMMON_ERROR_LOCK_BIND_FAIL = 20;
	/** 车台已与其他车辆绑定 */
	public static final short COMMON_ERROR_VEHICLE_TERMINAL_BINDED = 21;
	/** 锁已经绑定 */
	public static final short COMMON_ERROR_LOCK_BINDED = 22;
	/** 轨迹时间小于2017年或者其它时间错误 */
	public static final short COMMON_ERROR_TRACK_TIME = 23;
	/** 记录ID不合法 */
	public static final short COMMON_ERROR_RECORD_ID = 24;
	/** 设备ID不合法 */
	public static final short COMMON_ERROR_DEVICE_ID = 25;
	/** 车台应答超时 */
	public static final short COMMON_ERROR_TIME_OUT = 26;
	/** 设备类型不合法 */
	public static final short COMMON_ERROR_DEVICE_TYPE = 27;

	private UdpReplyErrorTag() {}
}
