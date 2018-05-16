package com.tipray.websocket;

import com.tipray.bean.record.AlarmRecord;
import com.tipray.service.AlarmRecordService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SpringWebSocket报警业务处理
 * <p>
 * 报警业务类型 <br>
 * 0 websocket关闭 <br>
 * 1 websocket启动 <br>
 * 100 消除报警 <br>
 * 101 消除报警回复 <br>
 * 110 报警(default) <br>
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class AlarmWebSocketHandler implements WebSocketHandler {
	private static final Logger logger = LoggerFactory.getLogger(AlarmWebSocketHandler.class);
	private static final List<AlarmRecord> ALARM_RECORDS_CACHE = new CopyOnWriteArrayList<>();
    private static final Map<Long, WebSocketSession> WEB_SOCKET_CLIENTS = new HashMap<Long, WebSocketSession>();
	private static final Map<Long, Integer> SESSION_USER_CACHE = new HashMap<Long, Integer>();
	private static final Map<Integer, Long> USER_ONLINE_TIME_CACHE = new HashMap<Integer, Long>();
	private static final Map<Long, String> ALARM_CACHE = new HashMap<Long, String>();
	private static final Map<Long, Long> ALARM_TIME_CACHE = new HashMap<Long, Long>();
	private static final Map<Integer, Boolean> ALARM_TIP_CACHE = new HashMap<Integer, Boolean>();
	// private static final int WEB_SOCKET_CLOSE = 0;
	private static final int WEB_SOCKET_OPEN = 1;
	private static final int ALARM_CLEAR = 100;
	// private static final int ALARM_CLEAR_REPLY = 101;
	// private static final int ALARM_DEFAULT = 110;
	private static final int ALARM_DELAY = 120;
	private static final int ALARM_IGNORE = 130;

	@Resource
	private AlarmRecordService alarmRecordService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		Long sessionId = Long.parseLong(session.getId(), 16);
		WEB_SOCKET_CLIENTS.put(sessionId, session);
		logger.info("alarm connection established：{}", sessionId);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		if (message.getPayload() instanceof ByteBuffer) {
			// IE浏览器不间断发送0字节的PongMessage以维持WebSocket连接，服务端忽略此消息
			return;
		}
		logger.debug("handle message：{}" + message.getPayload());
		String msgText;
		try {
			msgText = URLDecoder.decode((String) message.getPayload(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("报警业务：解码浏览器发送的消息异常！\n{}", e.getMessage());
			return;
		}
		Map<String, Object> msgMap;
		try {
			msgMap = JSONUtil.parseToMap(msgText);
		} catch (Exception e) {
			logger.error("报警业务：解析浏览器发送的消息异常！\n{}", e.getMessage());
			return;
		}
		int biz = (int) msgMap.get("biz");
		switch (biz) {
		case WEB_SOCKET_OPEN:
			// WebSocket启动
			dealWebSocketOpen(session, msgMap);
			break;
		case ALARM_IGNORE:
			// 忽略报警
			Integer userId = (Integer) msgMap.get("userId");
			ALARM_TIP_CACHE.put(userId, false);
			break;
		default:
			break;
		}
	}

	/**
	 * 处理WebSocket启动业务
	 * 
	 * @param session
	 *            {@link WebSocketSession}
	 * @param msgMap
	 *            {@link Map} JSON文本 WebSocketMessage 的实际对象
	 */
	private void dealWebSocketOpen(WebSocketSession session, Map<String, Object> msgMap) {
		// Integer userId = Integer.parseInt((String) msgMap.get("userId"), 10);
		Long sessionId = Long.parseLong(session.getId(), 16);
		// 添加WebSocketSessionId与用户对应关系缓存
		// SESSION_USER_CACHE.put(sessionId, userId);
		// 报警业务类型 （100 消除报警，110 报警(default)）
        if (EmptyObjectUtil.isEmptyList(ALARM_RECORDS_CACHE)) {
            List<AlarmRecord> alarmRecords = alarmRecordService.findNotElimited();
            if (EmptyObjectUtil.isEmptyList(alarmRecords)) {
                return;
            }
            ALARM_RECORDS_CACHE.addAll(alarmRecords);
        }
		Map<String, Object> alarmCacheMap = new HashMap<String, Object>();
		// if (!EmptyObjectUtil.isEmptyMap(ALARM_CACHE)) {
		// 	alarmCacheMap.put("biz", ALARM_DELAY);
		// 	List<String> alarmCacheList = new ArrayList<String>();
		// 	if (!EmptyObjectUtil.isEmptyMap(USER_ONLINE_TIME_CACHE) && USER_ONLINE_TIME_CACHE.containsKey(userId)) {
		// 		// 上次退出时间
		// 		Long lastOutTime = USER_ONLINE_TIME_CACHE.get(userId);
		// 		ALARM_TIME_CACHE.keySet().parallelStream().forEach(alarmId -> {
		// 			if (ALARM_TIME_CACHE.get(alarmId).compareTo(lastOutTime) > 0) {
		// 				alarmCacheList.add(ALARM_CACHE.get(alarmId));
		// 			}
		// 		});
		// 	} else {
		// 		ALARM_CACHE.values().parallelStream().forEach(alarmCache -> alarmCacheList.add(alarmCache));
		// 	}
		// 	// 用户报警提示更新为TRUE
		// 	ALARM_TIP_CACHE.put(userId, true);
		// 	// 更新用户在线时间
		// 	USER_ONLINE_TIME_CACHE.put(userId, System.currentTimeMillis());
		// 	alarmCacheMap.put("alarmCacheList", alarmCacheList);
		// } else if (!EmptyObjectUtil.isEmptyMap(ALARM_TIP_CACHE) && ALARM_TIP_CACHE.containsKey(userId)
		// 		&& ALARM_TIP_CACHE.get(userId)) {
		// 	alarmCacheMap.put("biz", ALARM_IGNORE);
		// } else {
		// 	return;
		// }
		alarmCacheMap.put("biz", 111);
		alarmCacheMap.put("cacheAlarm", ALARM_RECORDS_CACHE);
		try {
			String alarmCacheMsg = JSONUtil.stringify(alarmCacheMap);
			session.sendMessage(new TextMessage(alarmCacheMsg));
		} catch (Exception e) {
			logger.error("send cache alarm msg to {} error: {}", sessionId, e.toString());
			logger.debug("send cache alarm msg error stack: ", e);
		}

	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		if (session.isOpen()) {
			try {
				session.close();
			} catch (IOException e) {
				logger.error("报警业务：关闭传输错误的客户端异常！\n{}", e.getMessage());
			}
		}
		Long sessionId = Long.parseLong(session.getId(), 16);
		dealConnectionClose(sessionId);
		logger.error("alarm connection {} transport error: {}", sessionId, exception.toString());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		Long sessionId = Long.parseLong(session.getId(), 16);
		int closeStatusCode = closeStatus.getCode();
		// if (closeStatusCode == WebSocketCloseStatusEnum.GOING_AWAY.code()) {
		// 	Long userId = SESSION_USER_CACHE.get(sessionId).longValue();
		// 	Session userSession = SessionUtil.getSession(userId);
		// 	SessionUtil.removeSession(userSession);
		// 	Map<String, Object> attributes = session.getAttributes();
		// 	HttpSession httpSession = (HttpSession) attributes.get(HandshakeInterceptor.HTTP_SESSION_ATTR_NAME);
		// 	httpSession.invalidate();
		// }
		dealConnectionClose(sessionId);
		logger.info("alarm connection {} closed by {}", sessionId, WebSocketCloseStatusEnum.getByCode(closeStatusCode));
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	/**
	 * 处理WebSocket连接关闭
	 * 
	 * @param sessionId
	 *            {@link WebSocketSession#getId()}
	 */
	private void dealConnectionClose(Long sessionId) {
		// 移除WebSocket客户端缓存
		WEB_SOCKET_CLIENTS.remove(sessionId);
		// if (SESSION_USER_CACHE.containsKey(sessionId)) {
		// 	// 更新用户在线时间
		// 	USER_ONLINE_TIME_CACHE.put(SESSION_USER_CACHE.get(sessionId), System.currentTimeMillis());
		// 	// 移除WebSocketSessionId与用户对应关系缓存
		// 	SESSION_USER_CACHE.remove(sessionId);
		// }
	}

	/**
	 * 发送信息给指定客户端
	 * 
	 * @param session
	 *            {@link WebSocketSession}
	 * @param message
	 *            {@link TextMessage}
	 */
	public void sendMessageToClient(WebSocketSession session, TextMessage message) {
		if (!session.isOpen()) {
			return;
		}
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			logger.error("sendTextMessage to {} error: {}", Long.parseLong(session.getId(), 16), e.getMessage());
		}
	}

	/**
	 * 广播报警信息
	 * 
	 * @param alarmId
	 *            {@link Long} 报警ID
	 */
	public void broadcastAlarm(Long alarmId) {
		AlarmRecord alarmRecord = alarmRecordService.getRecordById(alarmId);
		String alarmMsg;
		try {
			alarmMsg = JSONUtil.stringify(alarmRecord);
		} catch (Exception e) {
			logger.error("AlarmRecord对象转为JSON字符串异常：{}", e.toString());
			return;
		}
		// 添加报警缓存
		ALARM_CACHE.put(alarmId, alarmMsg);
		// 添加报警时间缓存
		ALARM_TIME_CACHE.put(alarmId, System.currentTimeMillis());
		logger.info("broadcastAlarm: {}", alarmMsg);
		WEB_SOCKET_CLIENTS.values().parallelStream().forEach(session -> {
			if (session.isOpen()) {
				Long sessionId = Long.parseLong(session.getId(), 16);
				try {
					session.sendMessage(new TextMessage(alarmMsg));
					if (SESSION_USER_CACHE.containsKey(sessionId)) {
						// 用户报警提示更新为TRUE
						ALARM_TIP_CACHE.put(SESSION_USER_CACHE.get(sessionId), true);
					}
				} catch (IOException e) {
					logger.error("broadcastAlarm: {} to {} error: {}", alarmMsg, sessionId, e.getMessage());
				}
			}
		});
	}

	/**
	 * 广播消除报警
	 * 
	 * @param clearAlarmId
	 *            {@link Long} 消除报警的报警ID
	 */
	public void broadcastClearAlarm(Long clearAlarmId) {
		ALARM_CACHE.remove(clearAlarmId);
		ALARM_TIME_CACHE.remove(clearAlarmId);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append('{');
		// 报警业务类型 （100 消除报警，110 报警(default)，120 延时推送报警，130 忽略报警）
		strBuf.append("\"biz\":").append(ALARM_CLEAR).append(',');
		strBuf.append("\"id\":").append(clearAlarmId);
		strBuf.append('}');
		logger.info("broadcastClearAlarm: {}", clearAlarmId);
		WEB_SOCKET_CLIENTS.values().parallelStream().forEach(session -> {
			if (session.isOpen()) {
				Long sessionId = Long.parseLong(session.getId(), 16);
				try {
					session.sendMessage(new TextMessage(strBuf));
				} catch (IOException e) {
					logger.error("broadcastClearAlarm: {} to {} error: {}", clearAlarmId, sessionId, e.getMessage());
				}
			}
		});
	}

	/**
	 * 广播消除多条报警
	 *
	 * @param alarmIds
	 *            {@link Long} 消除报警的报警ID集合
	 */
    public void broadcastClearAlarms(List<Long> alarmIds) {
        alarmIds.forEach(alarmId -> broadcastClearAlarm(alarmId));
    }
}
