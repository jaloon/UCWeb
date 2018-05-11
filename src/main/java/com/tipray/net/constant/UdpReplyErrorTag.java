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

	// 车辆轨迹实时上报请求错误类型
	/** 车辆轨迹实时上报sessionId错误 */
	public static final short TRACK_TASK_ERROR_SESSION_ID = 1;
	/** 车辆轨迹实时上报上报间隔错误 */
	public static final short TRACK_TASK_ERROR_INTERVAL = 2;
	/** 车辆轨迹实时上报错误 */
	public static final short TRACK_TASK_ERROR_UP_MIN = 3;
	/** 车辆轨迹实时上报持续时间错误 */
	public static final short TRACK_TASK_ERROR_DURATION = 4;

	// 车辆轨迹列表上报请求错误类型
	/** 车辆轨迹列表上报轨迹数目错误 */
	public static final short TRACK_LIST_ERROR_TRACK_NUM = 1;
	/** 车辆轨迹列表上报数据库插入错误 */
	public static final short TRACK_LIST_ERROR_DB_INSERT = 2;

	// 报警消除上报协议错误
	/** 报警消除上报设备类型错误 */
	public static final short ALARM_ELIMINATE_ERROR_DEVICE_TYPE = 1;
	/** 报警消除上报设备ID错误 */
	public static final short ALARM_ELIMINATE_ERROR_DEVICE_ID = 2;
	/** 报警消除上报报警类型错误 */
	public static final short ALARM_ELIMINATE_ERROR_ALARM_TYPE = 3;
	/** 报警消除上报触发方式错误 */
	public static final short ALARM_ELIMINATE_ERROR_TRIGGER_TYPE = 4;
	/** 报警消除上报触发ID错误 */
	public static final short ALARM_ELIMINATE_ERROR_TRIGGER_ID = 5;
	/** 报警消除上报消除时间错误 */
	public static final short ALARM_ELIMINATE_ERROR_ELIMINATE_TIME = 6;
	/** 报警消除上报站点类型错误 */
	public static final short ALARM_ELIMINATE_ERROR_STATION_TYPE = 7;
	/** 报警消除上报站点ID错误 */
	public static final short ALARM_ELIMINATE_ERROR_STATION_ID = 8;
	/** 报警不存在或者已经消除（车台需特殊处理） */
	public static final short ALARM_ELIMINATE_ERROR_NONEXISTENT_OR_ELIMINATED = 9;

	// 远程换站协议错误
	/** 远程换站仓号错误 */
	public static final short CHANGE_STATION_ERROR_STORE_ID = 1;
	/** 远程换站新配送ID错误 */
	public static final short CHANGE_STATION_ERROR_TRANSPORT_ID_NEW = 2;
	/** 远程换站新加油站ID错误 */
	public static final short CHANGE_STATION_ERROR_GASSTATION_ID_NEW = 3;

	// 远程控制状态协议错误
	/** 远程控制ID错误 */
	public static final short REMOTE_CONTROL_ERROR_CONTROL_ID = 1;
	/** 远程控制状态事件错误 */
	public static final short REMOTE_CONTROL_ERROR_STATUS_INCIDENT = 2;
	/** 远程控制车辆状态错误 */
	public static final short REMOTE_CONTROL_ERROR_VEHICLE_STATUS = 3;

	// 锁待绑定监听协议错误
	/** 锁待绑定监听值错误 */
	public static final short LOCK_LISTEN_ERROR_VALUE = 1;

	// 锁绑定信息变更协议错误
	/** 锁绑定变更类型错误 */
	public static final short LOCK_BIND_ERROR_ALTER_TYPE = 1;
	/** 锁绑定锁信息错误 */
	public static final short LOCK_BIND_ERROR_LOCK_INFO = 2;

	private UdpReplyErrorTag() {}
	
}
