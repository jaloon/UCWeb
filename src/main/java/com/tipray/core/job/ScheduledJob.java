package com.tipray.core.job;

import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.service.VehicleService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.websocket.MonitorWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
	@Resource
	private VehicleService vehicleService;
	private MonitorWebSocketHandler monitorWebSocketHandler = new MonitorWebSocketHandler();

	/**
	 * UDP链路维护心跳任务
	 */
	public void executeUdpHeartbeat() {
		try {
			udpServer.send(SendPacketBuilder.buildProtocol0x1100());
		} catch (Exception e) {
			logger.error("UDP心跳包发送异常：{}", e.toString());
		}
	}

    /**
     * 车辆在线状态监测
     */
	public void executeVehicleOnlineMonitor() {
        List<Long> onlineCars = vehicleService.monitorVehicleOnline();
        if (!EmptyObjectUtil.isEmptyList(onlineCars)) {
            monitorWebSocketHandler.monitorVehicleOnline(onlineCars);
        }
    }

    /**
     * 最新车辆轨迹查询
     */
    public void executeLastTtracksQuery() {

    }

}
