package com.tipray.constant;

/**
 * 系统属性常量
 * 
 * @author chenlong
 * @version 1.0 2018-02-24
 *
 */
public class SystemPropertyConst {
	/** 操作系统架构 */
	public static final String OS_ARCH = System.getProperty("os.arch");
	/** 操作系统名称 */
	public static final String OS_NAME = System.getProperty("os.name");
	/** 操作系统版本*/
	public static final String OS_VER = System.getProperty("os.version");
	/** 文件分隔符（在 UNIX 系统中是“/”） */
	public static final String FILE_SEP = System.getProperty("file.separator");
	/** 路径分隔符（在 UNIX 系统中是“:”） */
	public static final String PATH_SEP = System.getProperty("path.separator");
	/** 行分隔符（在 UNIX 系统中是“/n”） */
	public static final String LINE_SEP = System.getProperty("line.separator");
	/** tomcat服务器的绝对路径 */
	public static final String CATALINA_HOME = System.getProperty("catalina.home");
	/** JRE平台架构（x32/x64）JRE_ARCHITECTURE */
	public static final String JRE_ARCH = System.getProperty("sun.arch.data.model");
	/** dll/accredit */
	public static final String ACCREDIT_DLL_PATH = new StringBuffer("dll/").append(JRE_ARCH).append("/accredit").toString();
	/** dll/crc */
	public static final String CRC_DLL_PATH = new StringBuffer("dll/").append(JRE_ARCH).append("/crc").toString();
	/** dll/libotp */
	public static final String OTP_DLL_PATH = new StringBuffer("dll/").append(JRE_ARCH).append("/libotp").toString();
	/** dll/rc4 */
	public static final String RC4_DLL_PATH = new StringBuffer("dll/").append(JRE_ARCH).append("/rc4").toString();
	/** dll/umf */
	public static final String UMF_DLL_PATH = new StringBuffer("dll/").append(JRE_ARCH).append("/umf").toString();

	private SystemPropertyConst() {}
	
}
