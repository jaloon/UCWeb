package com.tipray.constant;

/**
 * 远程操作相关常量
 * 
 * @author chenlong
 * @version 1.0 2018-01-31
 *
 */
public class RemoteControlConst {
	/** 远程进油库 */
	public final static int REMOTE_INTO_DEPOT = 1;
	/** 远程出油库 */
	public final static int REMOTE_QUIT_DEPOT = 2;
	/** 远程进加油站 */
	public final static int REMOTE_INTO_STATION = 3;
	/** 远程出加油站 */
	public final static int REMOTE_QUIT_STATION = 4;
	/** 进入应急 */
	public final static int REMOTE_INTO_URGENT = 5;
	/** 取消应急 */
    public final static int REMOTE_QUIT_URGENT = 6;
	/** 远程状态变更 */
	public final static int REMOTE_ALTER_STATUS = 7;
	/** 待进油区 */
	public final static int REMOTE_WAIT_OILDOM = 8;
	/** 远程进油区 */
	public final static int REMOTE_INTO_OILDOM = 9;
	/** 远程出油区 */
	public final static int REMOTE_QUIT_OILDOM = 10;

	/** 远程操作/换站未完成 */
	public final static int REMOTE_PROGRESS_FAIL = 0;
	/** 远程操作/换站请求中 */
	public final static int REMOTE_PROGRESS_REQUST = 1;
	/** 远程操作/换站已完成 */
	public final static int REMOTE_PROGRESS_DONE = 2;
	
	private RemoteControlConst() {}
	
}
