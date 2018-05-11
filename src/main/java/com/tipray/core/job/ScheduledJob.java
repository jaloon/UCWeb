package com.tipray.core.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;

/**
 * 定时任务
 * 
 * @author chenlong
 * @version 1.0 2018-04-18
 *
 */
@Component
public class ScheduledJob {
	private static final Logger logger = LoggerFactory.getLogger(ScheduledJob.class);
	@Resource
	private NioUdpServer udpServer;

	/**
	 * 链路维护心跳任务
	 */
	public void executeHeartbeat() {
		try {
			udpServer.send(SendPacketBuilder.buildProtocol0x1100());
		} catch (Exception e) {
			logger.error("UDP心跳包发送异常：{}", e.toString());
		}
	}
	
}
