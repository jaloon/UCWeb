package com.tipray.constant;

/**
 * 报警信息位标识常量
 * <p>
 * 车台报警信息位说明：以下位序从低位开始
 * <table>
 * <tr>
 * <th>8</th>
 * <th>7</th>
 * <th>6</th>
 * <th>5</th>
 * <th>4</th>
 * <th>3</th>
 * <th>2</th>
 * <th>1</th>
 * </tr>
 * <tr>
 * <td>保留</td>
 * <td>保留</td>
 * <td>保留</td>
 * <td>保留</td>
 * <td>保留</td>
 * <td>保留</td>
 * <td>时钟电池报警</td>
 * <td>未施封越界</td>
 * </tr>
 * </table>
 * <p>
 * 锁报警信息位说明：以下位序从低位开始
 * <table>
 * <tr>
 * <th>8</th>
 * <th>7</th>
 * <th>6</th>
 * <th>5</th>
 * <th>4</th>
 * <th>3</th>
 * <th>2</th>
 * <th>1</th>
 * </tr>
 * <tr>
 * <td>开关状态</td>
 * <td>是否可用</td>
 * <td>保留</td>
 * <td>保留</td>
 * <td>进入应急</td>
 * <td>异常开锁</td>
 * <td>电池低电压</td>
 * <td>通讯异常</td>
 * </tr>
 * </table>
 * 
 * @author chenlong
 * @version 1.0 2018-04-13
 *
 */
public class AlarmBitMarkConst {
	
	// 车台报警信息位说明：以下位序从低位开始
	// ------------------------------------------------------------------------------
	// |8		|7 		|6 		|5 		|4 		|3 		|2 				|1 			|
	// ------------------------------------------------------------------------------
	// |保留		|保留 	|保留 	|保留 	|保留 	|保留 	|时钟电池报警 	|未施封越界 	|
	// ------------------------------------------------------------------------------
	
	/** 车台报警信息位1：未施封越界 */
	public static final byte TERMINAL_ALARM_BIT_1_UNSEAL_OUT = 1;
	/** 车台报警信息位2：时钟电池报警 */
	public static final byte TERMINAL_ALARM_BIT_2_CLOCK_BATTERY = 1 << 1;
	/** 车台报警信息位3：保留 */
	public static final byte TERMINAL_ALARM_BIT_3_RESERVED = 1 << 2;
	/** 车台报警信息位4：保留 */
	public static final byte TERMINAL_ALARM_BIT_4_RESERVED = 1 << 3;
	/** 车台报警信息位5：保留 */
	public static final byte TERMINAL_ALARM_BIT_5_RESERVED = 1 << 4;
	/** 车台报警信息位6：保留 */
	public static final byte TERMINAL_ALARM_BIT_6_RESERVED = 1 << 5;
	/** 车台报警信息位7：保留 */
	public static final byte TERMINAL_ALARM_BIT_7_RESERVED = 1 << 6;
	/** 车台报警信息位8：保留 */
	public static final byte TERMINAL_ALARM_BIT_8_RESERVED = (byte) (1 << 7);
	/** 有效的车台报警位 */
	public static final byte VALID_TERMINAL_ALARM_BITS = TERMINAL_ALARM_BIT_1_UNSEAL_OUT | TERMINAL_ALARM_BIT_2_CLOCK_BATTERY;

	
	// 锁报警信息位说明：以下位序从低位开始
	// --------------------------------------------------------------------------------------
	// |8 			|7			|6		|5		|4			|3 			|2 			|1			|
	// --------------------------------------------------------------------------------------
	// |开关状态		|是否可用	|保留	|保留	|进入应急	|异常开锁	|电池低电压	|通讯异常	|
	// --------------------------------------------------------------------------------------
	
	/** 锁报警信息位1：通讯异常 */
	public static final byte LOCK_ALARM_BIT_1_COMM_ANOMALY = 1;
	/** 锁报警信息位2：电池低电压 */
	public static final byte LOCK_ALARM_BIT_2_LOW_BATTERY = 1 << 1;
	/** 锁报警信息位3：异常开锁 */
	public static final byte LOCK_ALARM_BIT_3_UNUSUAL_UNLOCK = 1 << 2;
	/** 锁报警信息位4：进入应急 */
	public static final byte LOCK_ALARM_BIT_4_ENTER_URGENT = 1 << 3;
	/** 锁报警信息位5：保留 */
	public static final byte LOCK_ALARM_BIT_5_RESERVED = 1 << 4;
	/** 锁报警信息位6：保留 */
	public static final byte LOCK_ALARM_BIT_6_RESERVED = 1 << 5;
	/** 锁报警信息位7：是否可用 */
	public static final byte LOCK_ALARM_BIT_7_ENABLE = 1 << 6;
	/** 锁报警信息位8：开关状态 */
	public static final byte LOCK_ALARM_BIT_8_ON_OFF = (byte) (1 << 7);
	/** 有效的锁报警位 */
	public static final byte VALID_LOCK_ALARM_BITS = LOCK_ALARM_BIT_1_COMM_ANOMALY | LOCK_ALARM_BIT_2_LOW_BATTERY
            | LOCK_ALARM_BIT_3_UNUSUAL_UNLOCK | LOCK_ALARM_BIT_4_ENTER_URGENT;

	private AlarmBitMarkConst() {}
}
