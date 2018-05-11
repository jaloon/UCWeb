package com.tipray.constant;

import java.io.File;

/**
 * 车辆公共配置文件（Sqlite数据库文件）常量
 * 
 * @author chenlong
 * @version 1.0 2018-01-26
 *
 */
public class SqliteFileConst {
	
	/** 应急卡 */
	public static final String URGENT_CARD = "emergency_card_info";
	/** 管理卡 */
	public static final String MANAGE_CARD = "management_card_info";
	/** 出入库卡 */
	public static final String IN_OUT_CARD = "in_out_oildepot_card_info";
	/** 出入库读卡器 */
	public static final String IN_OUT_DEV = "in_out_oildepot_dev_info";
	/** 油库 */
	public static final String OIL_DEPOT = "oildepot_info";

	/** sqlite油库数据库文件 */
	public static final File OIL_DEPOT_DB_FILE = new File(CenterConfigConst.SQLITE_FILE_PATH, OIL_DEPOT + ".db");
	/** sqlite油库设备数据库文件 */
	public static final File IN_OUT_DEV_DB_FILE = new File(CenterConfigConst.SQLITE_FILE_PATH, IN_OUT_DEV + ".db");
	/** sqlite出入库卡数据库文件 */
	public static final File IN_OUT_CARD_DB_FILE = new File(CenterConfigConst.SQLITE_FILE_PATH, IN_OUT_CARD + ".db");
	/** sqlite管理卡卡数据库文件 */
	public static final File MANAGE_CARD_DB_FILE = new File(CenterConfigConst.SQLITE_FILE_PATH, MANAGE_CARD + ".db");
	/** sqlite应急卡数据库文件 */
	public static final File URGENT_CARD_DB_FILE = new File(CenterConfigConst.SQLITE_FILE_PATH, URGENT_CARD + ".db");

	private SqliteFileConst() {
	}

}
