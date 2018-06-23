package com.tipray.constant;

import java.nio.charset.StandardCharsets;

/**
 * 车台升级常量
 * 
 * @author chenlong
 * @version 1.0 2018-04-25
 *
 */
public final class TerminalUpgradeConst {
    /* 升级类型 */

    /** 升级类型1：APP，文件100 */
    public static final byte UPGRADE_TYPE_1_APP = 1;
    /** 升级类型2：内核+文件系统，文件2、3、4 */
    public static final byte UPGRADE_TYPE_2_KERNEL_FILESYS = 2;
    /** 升级类型3：内核+文件系统+APP，文件2、3、4、100 */
    public static final byte UPGRADE_TYPE_3_KERNEL_FILESYS_APP = 3;

    /* 文件类型（1个类型对应1个文件） */

	/** 文件类型1：BOOTLOADER（暂未使用） */
	@Deprecated
	public static final byte FILE_TYPE_1_BOOTLOADER = 1;
	/** 文件类型2：内核 */
	public static final byte FILE_TYPE_2_KERNEL = 2;
	/** 文件类型3：文件系统 */
	public static final byte FILE_TYPE_3_FILESYS = 3;
	/** 文件类型4：设备树*/
	public static final byte FILE_TYPE_4_DEVICE_TREE = 4;
	/** 文件类型100：APP */
	public static final byte FILE_TYPE_100_APP = 100;

    /* 文件名 */

    /** 内核文件 */
    public static final String FILE_NAME_KERNEL = "zImage";
    /** 文件系统文件 */
    public static final String FILE_NAME_FILESYS = "rootfs.mx6ul.tar.bz2";
    /** 设备树文件 */
    public static final String FILE_NAME_DEVICE_TREE = "imx6ul-cz0101.dtb";
    /** APP文件 */
    public static final String FILE_NAME_APP = "app.tar.bz2";

    /** 内核文件 */
    public static final byte[] FILE_NAME_BYTES_KERNEL = FILE_NAME_KERNEL.getBytes(StandardCharsets.UTF_8);
    /** 文件系统文件 */
    public static final byte[] FILE_NAME_BYTES_FILESYS = FILE_NAME_FILESYS.getBytes(StandardCharsets.UTF_8);
    /** 设备树文件 */
    public static final byte[] FILE_NAME_BYTES_DEVICE_TREE = FILE_NAME_DEVICE_TREE.getBytes(StandardCharsets.UTF_8);
    /** APP文件 */
    public static final byte[] FILE_NAME_BYTES_APP = FILE_NAME_APP.getBytes(StandardCharsets.UTF_8);

    /** 内核文件名长度 */
    public static final byte FILE_NAME_LENGTH_KERNEL = (byte) FILE_NAME_BYTES_KERNEL.length;
    /** 文件系统文件名长度 */
    public static final byte FILE_NAME_LENGTH_FILESYS = (byte) FILE_NAME_BYTES_FILESYS.length;
    /** 设备树文件名长度 */
    public static final byte FILE_NAME_LENGTH_DEVICE_TREE = (byte) FILE_NAME_BYTES_DEVICE_TREE.length;
    /** APP文件名长度 */
    public static final byte FILE_NAME_LENGTH_APP = (byte) FILE_NAME_BYTES_APP.length;


    /** 版本文件 */
    public static final String FILE_VER = "ver";
}
