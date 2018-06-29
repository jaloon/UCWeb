package com.tipray.constant;

/**
 * 操作日志类型常量
 * <p>
 * 日志类型使用整形数（4个字节）表示。
 * <ul>
 * <li>第1个字节：操作类别</li>
 * <li>第2个字节：操作实体</li>
 * <li>第3个字节：操作类型</li>
 * <li>第4个字节：操作结果</li>
 * </ul>
 * 例：0x01010101，基础信息管理设备添加成功
 * 
 * @author chenlong
 * @version 1.0 2018-04-11
 *
 */
public class LogTypeConst {
	/** 未知 */
	public static final int UNKNOWN = 0;
	
	// 第1个字节：操作类别
	/** 基础信息管理 */
	public static final int CLASS_BASEINFO_MANAGE = 0x01_00_00_00;
	/** 车辆管理 */
	public static final int CLASS_VEHICLE_MANAGE = 0x02_00_00_00;

	// 第2个字节：操作实体（0x00~0x7f为信息管理实体，0x80~0xff为车辆管理实体）
	/** 设备 */
	public static final int ENTITY_DEVICE = 0x00_01_00_00;
	/** 油库 */
	public static final int ENTITY_OIL_DEPOT = 0x00_02_00_00;
	/** 加油站 */
	public static final int ENTITY_GAS_STATION = 0x00_03_00_00;
	/** 卡 */
	public static final int ENTITY_CARD = 0x00_04_00_00;
	/** 手持机 */
	public static final int ENTITY_HANDSET = 0x00_05_00_00;
	/** 配送卡 */
	public static final int ENTITY_DISTRIBUTION_CARD = 0x00_06_00_00;
	/** 运输公司 */
	public static final int ENTITY_TRANSPORT_COMPANY = 0x00_07_00_00;
	/** 司机 */
	public static final int ENTITY_DRIVER = 0x00_08_00_00;
	/** 车辆 */
	public static final int ENTITY_VEHICLE = 0x00_09_00_00;
	/** 操作员 */
	public static final int ENTITY_USER = 0x00_0A_00_00;
	/** 角色 */
	public static final int ENTITY_ROLE = 0x00_0B_00_00;

	/** 车载终端 */
	public static final int ENTITY_TERMINAL = 0x00_81_00_00;
	/** 锁 */
	public static final int ENTITY_LOCK = 0x00_82_00_00;
	/** 配送信息 */
	public static final int ENTITY_DISTRIBUTION = 0x00_83_00_00;
	/** 报警信息 */
	public static final int ENTITY_ALARM = 0x00_84_00_00;
	/** 轨迹信息 */
	public static final int ENTITY_TRACK = 0x00_85_00_00;
	/** 远程操作 */
	public static final int ENTITY_REMOTE = 0x00_86_00_00;
	/** 道闸 */
	public static final int ENTITY_BARRIER = 0x00_87_00_00;

	// 第3个字节：操作类型
	/** 添加 */
	public static final int TYPE_INSERT = 0x00_00_01_00;
	/** 修改 */
	public static final int TYPE_UPDATE = 0x00_00_02_00;
	/** 删除 */
	public static final int TYPE_DELETE = 0x00_00_03_00;
	/** 同步 */
	public static final int TYPE_SYNC = 0x00_00_04_00;
	/** 批量导入 */
	public static final int TYPE_BATCH_IMPORT = 0x00_00_05_00;
	/** 批量导出 */
	public static final int TYPE_BATCH_EXPORT = 0x00_00_06_00;
	/** 重置密码 */
	public static final int TYPE_PASSWORD_RESET = 0x00_00_07_00;
	/** 修改密码 */
	public static final int TYPE_PASSWORD_ALTER = 0x00_00_08_00;

	/** 设备绑定 */
	public static final int TYPE_DEVICE_BIND = 0x00_00_11_00;
	/** 设备解绑 */
	public static final int TYPE_DEVICE_UNBIND = 0x00_00_12_00;

	/** 车台轨迹采集和上报频率配置 */
	public static final int TYPE_TERMINAL_TRACK_CONFIG = 0x00_00_13_00;
	/** 车台功能启用配置 */
	public static final int TYPE_TERMINAL_ENABLE_CONFIG = 0x00_00_14_00;
	/** 车台软件升级 */
	public static final int TYPE_TERMINAL_SOFTWARE_UPGRADE = 0x00_00_15_00;
	/** 车台取消升级 */
	public static final int TYPE_TERMINAL_CANCEL_UPGRADE = 0x00_00_16_00;

	/** 监听待绑定锁列表 */
	public static final int TYPE_LOCK_LISTEN = 0x00_00_21_00;
	/** 清除待绑定锁列表 */
	public static final int TYPE_LOCK_CLEAR = 0x00_00_22_00;
	/** 锁绑定触发开启关闭控制 */
	public static final int TYPE_LOCK_TRIGGER = 0x00_00_23_00;
	/** 开锁重置 */
	public static final int TYPE_LOCK_RESET = 0x00_00_24_00;

	/** 配送单下发 */
	public static final int TYPE_INVOICE_ISSUE = 0x00_00_31_00;
	/** 配送单变更 */
	public static final int TYPE_INVOICE_ALTER = 0x00_00_32_00;
	/** 远程换站 */
	public static final int TYPE_CHANGE_STATION = 0x00_00_33_00;

	/** 消除报警 */
	public static final int TYPE_ALARM_ELIMINATE = 0x00_00_41_00;

	/** 实时监控 */
	public static final int TYPE_REALTIME_MONITOR = 0x00_00_51_00;
	/** 实时监控取消 */
	public static final int TYPE_REALTIME_CANCEL = 0x00_00_52_00;
	/** 重点监控 */
	public static final int TYPE_FOCUS_MONITOR = 0x00_00_53_00;
	/** 重点监控取消 */
	public static final int TYPE_FOCUS_CANCEL = 0x00_00_54_00;
	/** 轨迹回放 */
	public static final int TYPE_TRACK_REPLAY = 0x00_00_55_00;

	/** 远程操作 */
	public static final int TYPE_REMOTE_CONTROL = 0x00_00_60_00;
	/** 进油库 */
	public static final int TYPE_IN_OIL_DEPOT = 0x00_00_61_00;
	/** 出油库 */
	public static final int TYPE_OUT_OIL_DEPOT = 0x00_00_62_00;
	/** 进加油站 */
	public static final int TYPE_IN_GAS_STATION = 0x00_00_63_00;
	/** 出加油站 */
	public static final int TYPE_OUT_GAS_STATION = 0x00_00_64_00;
	/** 进入应急 */
	public static final int TYPE_IN_URGENT = 0x00_00_65_00;
	/** 取消应急 */
	public static final int TYPE_OUT_URGENT = 0x00_00_66_00;
	/** 车辆状态变更 */
	public static final int TYPE_CAR_STATUS_ALTER = 0x00_00_67_00;
    /** 待进油区 */
    public final static int TYPE_WAIT_OILDOM = 0x00_00_68_00;

	/** 道闸开启 */
	public static final int TYPE_BARRIER_OPEN = 0x00_00_70_00;
	/** 进道闸 */
	public static final int TYPE_BARRIER_IN = 0x00_00_71_00;
	/** 出道闸 */
	public static final int TYPE_BARRIER_OUT = 0x00_00_72_00;

	// 第4个字节：操作结果
	/** 成功 */
	public static final int RESULT_DONE = 0x00_00_00_01;
	/** 失败 */
	public static final int RESULT_FAIL = 0x00_00_00_02;
	/** 部分成功 */
	public static final int RESULT_PART = 0x00_00_00_03;

	private LogTypeConst() {
	}
}
