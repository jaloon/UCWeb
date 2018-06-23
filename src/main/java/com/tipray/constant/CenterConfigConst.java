package com.tipray.constant;

import com.tipray.core.CenterConfig;

import java.net.InetAddress;

/**
 * 用户中心配置常量
 * 
 * @author chenlong
 * @version 1.0 2018-02-09
 *
 */
public class CenterConfigConst extends CenterConfig {
	/** 默认http协议 */
	public static final String DEFAULT_HTTP_PROTOCOL = "http";
	/** 默认http端口 */
	public static final String DEFAULT_HTTP_PORT = "80";
	/** 默认https协议 */
	public static final String DEFAULT_HTTPS_PROTOCOL = "https";
	/** 默认https端口 */
	public static final String DEFAULT_HTTPS_PORT = "443";
	/** 默认ftp协议 */
	public static final String DEFAULT_FTP_PROTOCOL = "ftp";
	/** 默认ftp端口 */
	public static final String DEFAULT_FTP_PORT = "21";
	/** 默认普利通中心端口 */
	public static final String DEFAULT_PLTONE_PORT = "10090";

	/** 本中心ID */
	public static final Integer CENTER_ID = CenterConfig.getCenterId();
	/** 本中心名称 */
	public static final String CENTER_NAME = CenterConfig.getCenterName();
	/** 本中心版本号 */
	public static final Integer CENTER_VER = CenterConfig.getCenterVer();

	/** 普利通中心http协议 */
	public static final String PLTONE_HTTP = CenterConfig.getPltoneHttp();
	/** 普利通中心主机地址 */
	public static final String PLTONE_HOST = CenterConfig.getPltoneHost();
	/** 普利通中心端口 */
	public static final Integer PLTONE_PORT = CenterConfig.getPltonePort();
	/** 普利通中心路径 */
	public static final String PLTONE_URL = CenterConfig.getPltoneUrl();

	/** ftp文件本地存放路径 */
	public static final String FTP_LOCAL_PATH = CenterConfig.getFtpLocalPath();
	/** ftp协议 */
	public static final String FTP_PROTOCOL = CenterConfig.getFtpProtocol();
	/** ftp服务器主机地址 */
	public static final String FTP_HOST = CenterConfig.getFtpHost();
	/** ftp服务器端口 */
	public static final Integer FTP_PORT = CenterConfig.getFtpPort();
	/** ftp服务器路径 */
	public static final String FTP_URL = CenterConfig.getFtpUrl();
	/** ftp用户账号 */
	public static final String FTP_ACOUNT = CenterConfig.getFtpAccount();
	/** ftp用户密码 */
	public static final String FTP_PASSWORD = CenterConfig.getFtpPassword();
	/** ftp登录全路径（ftp://admin:admin@192.168.7.20） */
	public static final String FTP_FULL_URL = CenterConfig.getFtpFullUrl();
	/** ftp文件上传路径 */
	public static final String FTP_UPLOAD_PATH = CenterConfig.getFtpUploadPath();

	/** 本地UDP端口 */
	public static final Integer UDP_LOCAL_PORT = CenterConfig.getUdpLocalPort();
	/** 远程UDP服务器主机名称 */
	public static final String UDP_REMOTE_HOST = CenterConfig.getUdpRemoteHost();
	/** 远程UDP服务器主机地址 */
	public static final InetAddress UDP_REMOTE_ADDR = CenterConfig.getUdpRemoteAddr();
	/** 远程UDP服务器主机端口 */
	public static final Integer UDP_REMOTE_PORT = CenterConfig.getUdpRemotePort();

	/** WebService是否转发配送信息 */
	public static final Boolean WEBSERVICE_FORWORD = CenterConfig.getWebServiceForword();
	/** 瑞通系统WebService地址（http://服务器地址:端口号/Elock_Service.asmx） */
	public static final String WEBSERVICE_RT_URL = CenterConfig.getWebServiceRtUrl();

	/** Sqlite数据库文件路径 */
	public static final String SQLITE_FILE_PATH = CenterConfig.getSqliteFilePath();
	/** 头像图片存放路径 */
	public static final String IMAGE_FILE_PATH = CenterConfig.getImageFilePath();
	/** Excel文件存放路径 */
	public static final String EXCEL_FILE_PATH = CenterConfig.getExcelFilePath();
	/** 车台升级文件存放路径 */
	public static final String UPGRADE_FILE_PATH = CenterConfig.getUpgradeFilePath();

	private CenterConfigConst() {}
}
