package com.tipray.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.tipray.core.base.BaseAction;
import com.tipray.util.JSONUtil;
import com.tipray.util.StringUtil;
import com.tipray.websocket.AlarmWebSocketHandler;
import com.tipray.websocket.MonitorWebSocketHandler;

import net.sf.json.JSON;

/**
 * 外部接口
 * 
 * @author chenlong
 *
 */
@Controller
@RequestMapping("api")
public class ExternalInterface extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ExternalInterface.class);
	@Autowired
	private AlarmWebSocketHandler alarmWebSocketHandler;
	@Autowired
	private MonitorWebSocketHandler monitorWebSocketHandler;

	/**
	 * 绑定信息更新
	 * 
	 * @param lockWaitBind
	 *            锁待绑定列表更新
	 */
	@RequestMapping("bind")
	@ResponseBody
	public void bind(Long lockWaitBind) {
		// 待定(暂时砍掉)
		return;
	}

	/**
	 * 报警
	 * 
	 * @param alarmId
	 *            {@link Long} 报警ID
	 * @param biz
	 *            {@link Integer} 业务类型（1 报警，2 车台消除报警）
	 */
	@RequestMapping(value = "alarm", method = RequestMethod.POST)
	@ResponseBody
	public void alarm(Long alarmId, Integer biz) {
		logger.debug("报警业务：alarmId = {}，biz = {}", alarmId, biz);
		boolean isParamsValid = alarmId != null && alarmId > 0 && biz != null && biz > 0;
		if (isParamsValid) {
			switch (biz) {
			case 1:
				alarmWebSocketHandler.broadcastAlarm(alarmId);
				return;
			case 2:
				alarmWebSocketHandler.broadcastClearAlarm(alarmId);
				return;
			default:
				logger.warn("报警业务类型超出可处理范围，biz = {}", biz);
				return;
			}
		}
		logger.error("报警业务：参数无效，alarmId = {}，biz = {}", alarmId, biz);
	}

	/**
	 * 车辆监控
	 * 
	 * @param track
	 *            {@link JSON} 单条轨迹信息
	 * @param trackList
	 *            {@link JSON} 轨迹列表信息
	 * @param vehicleIsOnline
	 *            {@link JSON} 在线/离线 vehicleIsOnline={"vehicle_id": 1, "is_online":1}
	 * @param vehicleCfg
	 *            {@link JSON} 车辆配置信息 vehicleCfg={"vehicle_id": 1}
	 */
	@RequestMapping(value = "monitor", method = RequestMethod.POST)
	@ResponseBody
	public void monitor(String track, String trackList, String vehicleIsOnline, String vehicleCfg) {
		logger.debug("车辆监控业务：track = {}，trackList = {}, vehicleIsOnline = {}, vehicleCfg = {}",
				track, trackList, vehicleIsOnline, vehicleCfg);
		WebSocketSession session = null;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (StringUtil.isNotEmpty(track)) {
				map.put("biz", "track");
				map.put("track", track);
			} else if (StringUtil.isNotEmpty(trackList)) {
				map.put("biz", "tracklist");
				map.put("tracklist", trackList);
			} else if (StringUtil.isNotEmpty(vehicleIsOnline)) {
				Map<String, Object> onlineMap = JSONUtil.parseToMap(vehicleIsOnline);
				int online = (int) onlineMap.get("is_online");
				if (online == 0) {
					map.put("biz", "offline");
					map.put("vehicle_id", onlineMap.get("vehicle_id"));
				}
			} else if (StringUtil.isNotEmpty(vehicleCfg)) {
				// 待处理
				return;
			}
			String msg = JSONUtil.stringify(map);
			monitorWebSocketHandler.handleMessage(session, new TextMessage(msg));
			// 必须return，否则monitorWebSocketHandler会一直重复发送消息
			return;
		} catch (Exception e) {
			logger.error("处理监控信息异常：\n{}", e.toString());
		}
	}
	
}
