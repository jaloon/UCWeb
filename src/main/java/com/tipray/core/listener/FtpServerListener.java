package com.tipray.core.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.tipray.ftp.FtpServer;

/**
 * 监听容器动作，控制ftp服务启动/关闭
 * 
 * @author chenlong
 * @version 1.0 2018-01-26
 *
 */
public class FtpServerListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(FtpServerListener.class);

	/**
	 * 容器初始化调用方法 Start FtpServer
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Starting FtpServer...");
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		FtpServer server = (FtpServer) ctx.getBean("ftpServer");
		sce.getServletContext().setAttribute("FTPSERVER_CONTEXT_NAME", server);
		try {
			server.start();
			logger.info("FtpServer started.");
		} catch (Exception e) {
			logger.error("Failed to start FtpServer!\n{}", e.toString());
		}

	}

	/**
	 * 容器关闭时调用方法 Stop FtpServer
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Stopping FtpServer...");
		FtpServer server = (FtpServer) sce.getServletContext().getAttribute("FTPSERVER_CONTEXT_NAME");
		if (server != null) {
			server.stop();
			sce.getServletContext().removeAttribute("FTPSERVER_CONTEXT_NAME");
			logger.info("FtpServer stopped.");
		} else {
			logger.info("No running FtpServer found!");
		}
	}

}
