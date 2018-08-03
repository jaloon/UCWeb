package com.tipray.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.junit.Test;

import com.tipray.constant.SqliteFileConst;
import com.tipray.ftp.FtpClient;
import com.tipray.net.UdpProtocol;
import com.tipray.util.FtpUtil;

/**
 * ftp服务器测试
 * 
 * @author chenlong
 * @version 1.0 2018-01-25
 *
 */
public class FtpTest {
	@Test
	public void testServer() throws Exception {
		// 加载中心配置文件
		Properties properties = new Properties();
		properties.load(UdpProtocol.class.getClassLoader().getResourceAsStream("center-constant.properties"));
		// 获取ftp路径
		String ftpPath = properties.getProperty("ftp.path");

		FtpServerFactory serverFactory = new FtpServerFactory();
		// 添加一个匿名用户anonymous，只读权限
		BaseUser user = new BaseUser();
		user.setName("anonymous");
		user.setHomeDirectory(ftpPath);
		serverFactory.getUserManager().save(user);
		// 添加一个测试用户test，密码123456，读写权限
		user = new BaseUser();
		user.setName("test");
		user.setPassword("123456");
		user.setHomeDirectory(ftpPath);
		List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(new WritePermission());
		user.setAuthorities(authorities);
		serverFactory.getUserManager().save(user);

		FtpServer server = serverFactory.createServer();
		server.start();
	}
	
	@Test
	public void testClient() {
		FtpClient ftp = new FtpClient("192.168.5.80", 21, "admin", "admin");
		System.out.println(ftp.login());
//		System.out.println(ftp.downloadFile("3G00-1712062556_1.xml", "d:/tmp/", ""));
		// 上传文件夹
		//ftp.uploadDirectory("d://DataProtemp", "/home/data/");
		// 下载文件夹
//		System.out.println(ftp.downLoadDirectory("d://tmp//", "/params"));
		System.out.println(ftp.uploadFile(new File("D:\\ftp\\3G00-1712062556_1.xml"), ""));
		ftp.logout();
		FtpUtil.upload(SqliteFileConst.URGENT_CARD_DB_FILE);
		FtpUtil.upload(SqliteFileConst.MANAGE_CARD_DB_FILE);
		FtpUtil.upload(SqliteFileConst.IN_OUT_CARD_DB_FILE);
		FtpUtil.upload(SqliteFileConst.IN_OUT_DEV_DB_FILE);
		FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
		
	}
}
