package com.tipray.core.listener;

import com.tipray.cache.AsynUdpCommCache;
import com.tipray.net.NioUdpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 监听容器动作，控制udp服务启动/关闭
 * 
 * @author chenlong
 * @version 1.0 2018-03-19
 *
 */
public class UdpServerListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(UdpServerListener.class);

	/**
	 * 容器初始化调用方法 Start UdpServer
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Starting UdpServer...");
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		NioUdpServer server = (NioUdpServer) ctx.getBean("udpServer");
		sce.getServletContext().setAttribute("UDPSERVER_CONTEXT_NAME", server);
		try {
			server.start();
			server.receive();
			AsynUdpCommCache.heartbeatDetection(0);
			logger.info("UdpServer started.");
		} catch (Exception e) {
			logger.error("Failed to start UdpServer! ", e);
		}
	}

	/**
	 * 容器关闭时调用方法 Stop UdpServer
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Stopping UdpServer...");
		NioUdpServer server = (NioUdpServer) sce.getServletContext().getAttribute("UDPSERVER_CONTEXT_NAME");
		if (server != null) {
			server.setEnabled(false);
			server.stop();
			sce.getServletContext().removeAttribute("UDPSERVER_CONTEXT_NAME");
			logger.info("UdpServer stopped.");
		} else {
			logger.info("No running UdpServer found!");
		}
	}
}