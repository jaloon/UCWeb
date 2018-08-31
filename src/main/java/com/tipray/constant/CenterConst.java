package com.tipray.constant;

import com.tipray.core.CenterConstantConfig;

import java.net.InetAddress;

/**
 * 用户中心常量
 * 
 * @author chenlong
 * @version 1.0 2018-02-09
 *
 */
public class CenterConst extends CenterConstantConfig {
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
	public static final String DEFAULT_PLTONE_PORT = "3003";

	/** 本中心ID */
	public static final Integer CENTER_ID = CenterConstantConfig.getCenterId();
	/** 本中心名称 */
	public static final String CENTER_NAME = CenterConstantConfig.getCenterName();
	/** 本中心版本号 */
	public static final Integer CENTER_VER = CenterConstantConfig.getCenterVer();

	/** 普利通中心http协议 */
	public static final String PLTONE_HTTP = CenterConstantConfig.getPltoneHttp();
	/** 普利通中心主机地址 */
	public static final String PLTONE_HOST = CenterConstantConfig.getPltoneHost();
	/** 普利通中心端口 */
	public static final Integer PLTONE_PORT = CenterConstantConfig.getPltonePort();
	/** 普利通中心路径 */
	public static final String PLTONE_URL = CenterConstantConfig.getPltoneUrl();

	/** ftp文件本地存放路径 */
	public static final String FTP_LOCAL_PATH = CenterConstantConfig.getFtpLocalPath();
	/** ftp协议 */
	public static final String FTP_PROTOCOL = CenterConstantConfig.getFtpProtocol();
	/** ftp服务器主机地址 */
	public static final String FTP_HOST = CenterConstantConfig.getFtpHost();
	/** ftp服务器端口 */
	public static final Integer FTP_PORT = CenterConstantConfig.getFtpPort();
	/** ftp服务器路径 */
	public static final String FTP_URL = CenterConstantConfig.getFtpUrl();
	/** ftp用户账号 */
	public static final String FTP_ACOUNT = CenterConstantConfig.getFtpAccount();
	/** ftp用户密码 */
	public static final String FTP_PASSWORD = CenterConstantConfig.getFtpPassword();
	/** ftp登录全路径（ftp://admin:admin@192.168.7.20） */
	public static final String FTP_FULL_URL = CenterConstantConfig.getFtpFullUrl();
	/** ftp文件上传路径 */
	public static final String FTP_UPLOAD_PATH = CenterConstantConfig.getFtpUploadPath();

	/** 本地UDP端口 */
	public static final Integer UDP_LOCAL_PORT = CenterConstantConfig.getUdpLocalPort();
	/** 远程UDP服务器主机名称 */
	public static final String UDP_REMOTE_HOST = CenterConstantConfig.getUdpRemoteHost();
	/** 远程UDP服务器主机地址 */
	public static final InetAddress UDP_REMOTE_ADDR = CenterConstantConfig.getUdpRemoteAddr();
	/** 远程UDP服务器主机端口 */
	public static final Integer UDP_REMOTE_PORT = CenterConstantConfig.getUdpRemotePort();

	/** WebService端口 */
	public static final Integer WEBSERVICE_PORT = CenterConstantConfig.getWebServicePort();
    /** WebService路径 */
	public static final String WEBSERVICE_PATH = CenterConstantConfig.getWebServicePath();
    /** WebService真实地址 */
	public static final String WEBSERVICE_REAL_ADDR = CenterConstantConfig.getWebServiceRealAddr();
    /** WebService发布地址 */
	public static final String WEBSERVICE_PUBLISH_ADDR = CenterConstantConfig.getWebServicePublishAddr();
	/** WebService是否转发配送信息 */
	public static final Boolean WEBSERVICE_FORWORD = CenterConstantConfig.getWebServiceForword();
	/** 瑞通系统WebService地址（http://服务器地址:端口号/Elock_Service.asmx） */
	public static final String WEBSERVICE_RT_URL = CenterConstantConfig.getWebServiceRtUrl();

	/** Sqlite数据库文件路径 */
	public static final String SQLITE_FILE_PATH = CenterConstantConfig.getSqliteFilePath();
	/** 头像图片存放路径 */
	public static final String IMAGE_FILE_PATH = CenterConstantConfig.getImageFilePath();
	/** Excel文件存放路径 */
	public static final String EXCEL_FILE_PATH = CenterConstantConfig.getExcelFilePath();
	/** 车台升级文件存放路径 */
	public static final String UPGRADE_FILE_PATH = CenterConstantConfig.getUpgradeFilePath();

	private CenterConst() {}
}
