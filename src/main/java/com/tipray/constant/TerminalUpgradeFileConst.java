package com.tipray.constant;

/**
 * 车台升级文件常量
 * 
 * @author chenlong
 * @version 1.0 2018-04-25
 *
 */
public class TerminalUpgradeFileConst {
	/** 文件类型1：BOOTLOADER */
	public static final byte FILE_TYPE_1_BOOTLOADER = 1;
	/** 文件类型2：内核 */
	public static final byte FILE_TYPE_2_KERNEL = 2;
	/** 文件类型3：内核+文件系统 */
	public static final byte FILE_TYPE_3_KERNEL_FILESYS = 3;
	/** 文件类型4：BOOTLOADER+内核+文件系统 */
	public static final byte FILE_TYPE_4_BOOTLOADER_KERNEL_FILESYS = 4;
	/** 文件类型20：APP */
	public static final byte FILE_TYPE_20_APP = 20;
	/** 文件类型21：CONFIG */
	public static final byte FILE_TYPE_21_CONFIG = 21;

}
