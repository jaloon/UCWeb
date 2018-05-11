package com.tipray.ftp;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * ftp服务器
 * 
 * @author chenlong
 * @version 1.0 2018-01-26
 *
 */
public class FtpServer {
	private static final Logger logger = LoggerFactory.getLogger(FtpServer.class);
	private org.apache.ftpserver.FtpServer ftpServer;

	/**
	 * 启动ftp服务器
	 */
	public void start() {
		try {
			init();
			ftpServer.start();
		} catch (FtpException e) {
			logger.error("FtpServer start error!\n{}", e.toString());
		}
	}

	/**
	 * 停止ftp服务器
	 */
	public void stop() {
		ftpServer.stop();
	}

	/**
	 * 初始化ftp服务器
	 */
	private void init() {
		FtpServerFactory serverFactory = new FtpServerFactory();
		// 设置监听端口，默认21
		ListenerFactory listenerFactory = new ListenerFactory();
		listenerFactory.setPort(10021); serverFactory.addListener("default", listenerFactory.createListener());

		/*
		 * //代码设置用户 // 加载中心配置文件 Properties properties = new Properties();
		 * properties.load(FtpServer.class.getClassLoader().getResourceAsStream(
		 * "center.properties")); // 获取ftp路径 String ftpPath =
		 * properties.getProperty("ftp.path"); // 添加一个匿名用户anonymous，只读权限 BaseUser user =
		 * new BaseUser(); user.setName("anonymous"); user.setHomeDirectory(ftpPath);
		 * serverFactory.getUserManager().save(user); // 添加一个测试用户test，密码123456，读写权限 user
		 * = new BaseUser(); user.setName("test"); user.setPassword("123456");
		 * user.setHomeDirectory(ftpPath); List<Authority> authorities = new
		 * ArrayList<Authority>(); authorities.add(new WritePermission());
		 * user.setAuthorities(authorities); serverFactory.getUserManager().save(user);
		 */

		// 配置文件设置用户
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setFile(new File("ftpusers.properties"));
		// 设定用户输入密码时直接输入明文
		userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
		serverFactory.setUserManager(userManagerFactory.createUserManager());

		ftpServer = serverFactory.createServer();
	}
}
