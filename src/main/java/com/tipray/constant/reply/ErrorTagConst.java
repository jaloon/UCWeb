package com.tipray.constant.reply;

/**
 * 错误标志常量
 * 
 * @author chenlong
 * @version 1.0 2018-03-15
 *
 */
public class ErrorTagConst {
	/** 无错标志位 */
	public static final byte NO_ERROR_TAG = 0;
	
	/** http连接错误标志位 */
	public static final byte HTTP_CONNECTION_ERROR_TAG = 110;
	
	/** 登录错误标志位 */
	public static final byte LOGIN_ERROR_TAG = 1;
	
	/** 权限错误标志位 */
	public static final byte PERMISSION_ERROR_TAG = 2;
	
	/** 根据设备ID获取用户中心IP、端口、RC4等信息错误标志位 */
	public static final byte CENTER_INFO_ERROR_TAG = 3;
	/** 根据用户中心ID获取用户中心RC4秘钥信息错误标志位 */
	public static final byte CENTER_CR4_ERROR_TAG = 4;
	
	/** 数据库操作异常标志位 */
	public static final byte DB_OPERATE_ERROR_TAG = 10;
	/** 数据库查询异常标志位 */
	public static final byte DB_SELECT_ERROR_TAG = 11;
	/** 数据库新增异常标志位 */
	public static final byte DB_INSERT_ERROR_TAG = 12;
	/** 数据库修改异常标志位 */
	public static final byte DB_UPDATE_ERROR_TAG = 13;
	/** 数据库删除异常标志位 */
	public static final byte DB_DELETE_ERROR_TAG = 14;

	/** UDP通信错误标志位 */
	public static final byte UDP_COMMUNICATION_ERROR_TAG = 20;
	/** UDP解析错误标志位 */
	public static final byte UDP_PARSE_ERROR_TAG = 21;
	/** UDP应答错误标志位 */
	public static final byte UDP_REPLY_ERROR_TAG = 22;
	/** UDP通用错误标志位 */
	public static final byte UDP_COMMON_ERROR_TAG = 23;
	/** UDP业务错误标志位 */
	public static final byte UDP_BIZ_ERROR_TAG = 24;
	
	/** 车台配置错误标志位 */
	public static final byte TERMINAL_CONFIG_ERROR_TAG = 31;
	/** 车台软件升级错误标志位 */
	public static final byte TERMINAL_UPGRADE_ERROR_TAG = 32;
	/** 设备绑定错误标志位 */
	public static final byte DEV_BIND_ERROR_TAG = 33;
	
	/** 远程控制错误标志位 */
	public static final byte ROMOTE_CONTROL_ERROR_TAG = 40;
	/** 远程换站错误标志位 */
	public static final byte CHANGE_STATION_ERROR_TAG = 41;
	/** 消除报警错误标志位 */
	public static final byte ELIMINATE_ALARM_ERROR_TAG = 42;
	/** 开锁重置错误标志位 */
	public static final byte LOCK_OPEN_RESET_ERROR_TAG = 43;
	/** 轨迹查询错误标志位 */
	public static final byte FIND_TRACK_ERROR_TAG = 46;
	
	/** 异常信息标志位 */
	public static final byte EXCEPTON_MESSAGE_TAG = Byte.MAX_VALUE;
	
	private ErrorTagConst() {}
}
