package com.tipray.constant;

/**
 * 车台配置信息位标识常量
 * <p>
 * 公共配置文件列表（1字节），用位标识下列更新文件：
 * <ol>
 * <li>emergency_card_info.db</li>
 * <li>management_card_info.db</li>
 * <li>in_out_oildepot_card_info.db</li>
 * <li>in_out_oildepot_dev_info.db</li>
 * <li>oildepot_info.db</li>
 * </ol>
 * <p>
 * 车台功能启用配置参数（4字节），通过位来表示开启和关闭的设置，位序从低位开始：
 * <ol>
 * <li>加油站内施解封是否启动GPS校验</li>
 * <li>油库出入库是否启动GPS校验</li>
 * <li>加油站开锁是否启动GPS校验</li>
 * <li>油库开锁是否启动GPS校验</li>
 * <li>是否检测站点信息合法</li>
 * <li>是否可在不同解封中多次开锁</li>
 * <li>是否只允许一次开锁</li>
 * </ol>
 * 
 * @author chenlong
 * @version 1.0 2018-04-13
 *
 */
public class TerminalConfigBitMarkConst {

	// 车台公共配置文件信息位

	/** 车台公共配置文件信息位1：应急卡文件 */
	public static final byte COMMON_CONFIG_BIT_1_URGENT_CARD = 0b00000001;
	/** 车台公共配置文件信息位2：管理卡文件 */
	public static final byte COMMON_CONFIG_BIT_2_MANAGE_CARD = 0b00000010;
	/** 车台公共配置文件信息位3：出入库卡文件 */
	public static final byte COMMON_CONFIG_BIT_3_IN_OUT_CARD = 0b00000100;
	/** 车台公共配置文件信息位4：出入库读卡器文件 */
	public static final byte COMMON_CONFIG_BIT_4_IN_OUT_DEV = 0b00001000;
	/** 车台公共配置文件信息位5：油库文件 */
	public static final byte COMMON_CONFIG_BIT_5_OIL_DEPOT = 0b00010000;

	// 车台功能启用配置信息位

	/** 车台功能启用配置信息位1：加油站内施解封是否启动GPS校验 */
	public static final int FUNCTION_CONFIG_BIT_1_GPS_CHECK_STATION_SEAL = 1;
	/** 车台功能启用配置信息位2：油库出入库是否启动GPS校验 */
	public static final int FUNCTION_CONFIG_BIT_2_GPS_CHECK_IN_OUT_DEPOT = 1 << 1;
	/** 车台功能启用配置信息位3：加油站开锁是否启动GPS校验 */
	public static final int FUNCTION_CONFIG_BIT_3_GPS_CHECK_STATION_UNLOCK = 1 << 2;
	/** 车台功能启用配置信息位4：油库开锁是否启动GPS校验 */
	public static final int FUNCTION_CONFIG_BIT_4_GPS_CHECK_DEPOT_UNLOCK = 1 << 3;
	/** 车台功能启用配置信息位5：是否检测基站信息合法 */
	public static final int FUNCTION_CONFIG_BIT_5_BASE_STATION_CHECK = 1 << 4;
	/** 车台功能启用配置信息位6：是否可在不同解封中多次开锁 */
	public static final int FUNCTION_CONFIG_BIT_6_MULTIPLE_UNLOCK = 1 << 5;
	/** 车台功能启用配置信息位7：是否只允许一次开锁 */
	public static final int FUNCTION_CONFIG_BIT_7_UNLOCK_ONLY_ONCE = 1 << 6;

	private TerminalConfigBitMarkConst() {}

}
