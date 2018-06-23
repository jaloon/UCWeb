package com.tipray.core;

import com.tipray.constant.CenterConfigConst;
import com.tipray.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * 用户中心配置
 * 
 * @author chenlong
 * @version 1.0 2018-03-01
 *
 */
public class CenterConfig {
	private static final Logger logger = LoggerFactory.getLogger(CenterConfig.class);

	private static Integer centerId;
	private static String centerName;
	private static Integer centerVer;

	private static String pltoneHttp;
	private static String pltoneHost;
	private static Integer pltonePort;
	private static String pltoneUrl;

	private static String ftpLocalPath;
	private static String ftpProtocol;
	private static String ftpHost;
	private static Integer ftpPort;
	private static String ftpUrl;
	private static String ftpAccount;
	private static String ftpPassword;
	private static String ftpFullUrl;
	private static String ftpUploadPath;

	private static Integer udpLocalPort;
	private static String udpRemoteHost;
	private static InetAddress udpRemoteAddr;
	private static Integer udpRemotePort;

	private static Boolean webServiceForword;
	private static String webServiceRtUrl;

	private static String sqliteFilePath;
	private static String imageFilePath;
	private static String excelFilePath;
	private static String upgradeFilePath;

	static {
		init();
	}

	protected static Integer getCenterId() {
		return centerId;
	}

	protected static String getCenterName() {
		return centerName;
	}

	protected static Integer getCenterVer() {
		return centerVer;
	}

	protected static String getPltoneHttp() {
		return pltoneHttp;
	}

	protected static String getPltoneHost() {
		return pltoneHost;
	}

	protected static Integer getPltonePort() {
		return pltonePort;
	}

	protected static String getPltoneUrl() {
		return pltoneUrl;
	}

	protected static String getFtpLocalPath() {
		return ftpLocalPath;
	}

	protected static String getFtpProtocol() {
		return ftpProtocol;
	}

	protected static String getFtpHost() {
		return ftpHost;
	}

	protected static Integer getFtpPort() {
		return ftpPort;
	}

	protected static String getFtpUrl() {
		return ftpUrl;
	}

	protected static String getFtpAccount() {
		return ftpAccount;
	}

	protected static String getFtpPassword() {
		return ftpPassword;
	}

	protected static String getFtpFullUrl() {
		return ftpFullUrl;
	}

	protected static String getFtpUploadPath() {
		return ftpUploadPath;
	}

	public static Integer getUdpLocalPort() {
		return udpLocalPort;
	}

	protected static String getUdpRemoteHost() {
		return udpRemoteHost;
	}

	protected static InetAddress getUdpRemoteAddr() {
		return udpRemoteAddr;
	}

	protected static Integer getUdpRemotePort() {
		return udpRemotePort;
	}

	protected static Boolean getWebServiceForword() {
		return webServiceForword;
	}

	protected static String getWebServiceRtUrl() {
		return webServiceRtUrl;
	}

	protected static String getSqliteFilePath() {
		return sqliteFilePath;
	}

	protected static String getImageFilePath() {
		return imageFilePath;
	}

	protected static String getExcelFilePath() {
		return excelFilePath;
	}

    public static String getUpgradeFilePath() {
        return upgradeFilePath;
    }

    private static void init() {
		Properties properties = new Properties();
		try {
			properties.load(CenterConfig.class.getClassLoader().getResourceAsStream("center.properties"));
		} catch (IOException e) {
			logger.error("加载中心配置文件异常！\n{}", e.getMessage());
			return;
		}
		centerId = Integer.parseInt(properties.getProperty("this.id"), 10);
		centerName = properties.getProperty("this.name");
		centerVer = Integer.parseInt(properties.getProperty("this.ver"), 10);

		pltoneHttp = properties.getProperty("pltone.http", CenterConfigConst.DEFAULT_HTTP_PROTOCOL);
		pltoneHost = properties.getProperty("pltone.host");
		String pltonePortStr = properties.getProperty("pltone.port", CenterConfigConst.DEFAULT_PLTONE_PORT);
		pltonePort = Integer.parseInt(pltonePortStr, 10);
		pltoneUrl = new StringBuffer(pltoneHttp).append("://").append(pltoneHost).append(':').append(pltonePortStr)
				.toString();

		ftpLocalPath = properties.getProperty("ftp.localPath");
		FileUtil.createPath(ftpLocalPath);
		ftpProtocol = properties.getProperty("ftp.protocol", CenterConfigConst.DEFAULT_FTP_PROTOCOL);
		ftpHost = properties.getProperty("ftp.host");
		String ftpPortStr = properties.getProperty("ftp.port", CenterConfigConst.DEFAULT_FTP_PORT);
		ftpPort = Integer.parseInt(ftpPortStr, 10);
		ftpUrl = new StringBuffer(ftpProtocol).append("://").append(ftpHost).append(':').append(ftpPortStr).toString();
		ftpAccount = properties.getProperty("ftp.account");
		ftpPassword = properties.getProperty("ftp.password");
		// ftp://admin:admin@192.168.7.20
		ftpFullUrl = new StringBuffer(ftpProtocol).append("://").append(ftpAccount).append(':').append(ftpPassword)
				.append('@').append(ftpHost).append(':').append(ftpPortStr).toString();
		ftpUploadPath = properties.getProperty("ftp.uploadPath");

		udpLocalPort = Integer.parseInt(properties.getProperty("udp.local.port"), 10);
		udpRemoteHost = properties.getProperty("udp.remote.addr");
		try {
			udpRemoteAddr = InetAddress.getByName(udpRemoteHost);
		} catch (UnknownHostException e) {
			logger.error("获取UDP服务器地址异常！\n{}", e.getMessage());
		}
		udpRemotePort = Integer.parseInt(properties.getProperty("udp.remote.port"), 10);

		webServiceForword = properties.getProperty("webservice.forward", "0").equals("1");
		webServiceRtUrl = properties.getProperty("webservice.rt.url");

		sqliteFilePath = new StringBuffer(ftpLocalPath).append("/sqlite").toString();
		imageFilePath = new StringBuffer(ftpLocalPath).append("/image").toString();
		excelFilePath = new StringBuffer(ftpLocalPath).append("/excel").toString();
		upgradeFilePath = new StringBuffer(ftpLocalPath).append("/upgrade").toString();
		FileUtil.createPath(sqliteFilePath);
		FileUtil.createPath(imageFilePath);
		FileUtil.createPath(excelFilePath);
		FileUtil.createPath(upgradeFilePath);
	}

}
