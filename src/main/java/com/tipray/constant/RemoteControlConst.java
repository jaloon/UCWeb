package com.tipray.constant;

/**
 * 远程操作相关常量
 * 
 * @author chenlong
 * @version 1.0 2018-01-31
 *
 */
public class RemoteControlConst {
	// 远程操作类型
	/** 远程进油库 */
	public final static int REMOTE_TYPE_1_INTO_DEPOT = 1;
	/** 远程出油库 */
	public final static int REMOTE_TYPE_2_QUIT_DEPOT = 2;
	/** 远程进加油站 */
	public final static int REMOTE_TYPE_3_INTO_STATION = 3;
	/** 远程出加油站 */
	public final static int REMOTE_TYPE_4_QUIT_STATION = 4;
	/** 进入应急 */
	public final static int REMOTE_TYPE_5_INTO_URGENT = 5;
	/** 取消应急 */
    public final static int REMOTE_TYPE_6_QUIT_URGENT = 6;
	/** 远程状态变更 */
	public final static int REMOTE_TYPE_7_ALTER_STATUS = 7;
	/** 待进油区 */
	public final static int REMOTE_TYPE_8_WAIT_OILDOM = 8;
	/** 远程进油区 */
	public final static int REMOTE_TYPE_9_INTO_OILDOM = 9;
	/** 远程出油区 */
	public final static int REMOTE_TYPE_10_QUIT_OILDOM = 10;

	// 站点类型
    /** 站点类型1：油库 */
    public final static byte STATION_TYPE_1_DEPOT = 1;
    /** 站点类型2：加油站 */
    public final static byte STATION_TYPE_2_STATION = 2;

    // 车辆状态
    /** 1 在油库 */
    public final static byte VEHICLE_STATUS_1_IN_DEPOT = 1;
    /** 2 在途 */
    public final static byte VEHICLE_STATUS_2_IN_TRANSIT = 2;
    /** 3 在加油站 */
    public final static byte VEHICLE_STATUS_3_IN_STATION = 3;
    /** 4 返程 */
    public final static byte VEHICLE_STATUS_4_RETURN_ = 4;
    /** 5 应急 */
    public final static byte VEHICLE_STATUS_5_URGENT = 5;
    /** 6 油区外 */
    public final static byte VEHICLE_STATUS_6_OUT_OILDOM = 6;
    /** 7 在油区 */
    public final static byte VEHICLE_STATUS_7_IN_OILDOM = 7;

	// 远程操作结果
	/** 远程操作/换站未完成 */
	public final static int REMOTE_PROGRESS_FAIL = 0;
	/** 远程操作/换站请求中 */
	public final static int REMOTE_PROGRESS_REQUST = 1;
	/** 远程操作/换站已完成 */
	public final static int REMOTE_PROGRESS_DONE = 2;
	
	private RemoteControlConst() {}
	
}
