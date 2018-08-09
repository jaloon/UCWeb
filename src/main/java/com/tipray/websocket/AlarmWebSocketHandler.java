package com.tipray.websocket;

import com.tipray.bean.alarm.AlarmInfo;
import com.tipray.bean.record.AlarmRecord;
import com.tipray.service.AlarmRecordService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SpringWebSocket报警业务处理
 * <p>
 * 报警业务类型 <br>
 * 0 websocket关闭 <br>
 * 1 websocket启动 <br>
 * 100 消除报警 <br>
 * 101 消除报警回复 <br>
 * 110 报警(default) <br>
 * 111 缓存报警
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
public class AlarmWebSocketHandler implements WebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(AlarmWebSocketHandler.class);
    private static final Map<Long, ConcurrentWebSocketSessionDecorator> WEB_SOCKET_CLIENTS = new HashMap<>();
    // private static final int WEB_SOCKET_CLOSE = 0;
    private static final int WEB_SOCKET_OPEN = 1;
    private static final int ALARM_CLEAR = 100;
    // private static final int ALARM_CLEAR_REPLY = 101;
    // private static final int ALARM_DEFAULT = 110;
    private static final int ALARM_CACHE = 111;

    @Resource
    private AlarmRecordService alarmRecordService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long sessionId = Long.parseLong(session.getId(), 16);
        ConcurrentWebSocketSessionDecorator sessionDecorator = WebSocketUtil.decoratorSession(session);
        WEB_SOCKET_CLIENTS.put(sessionId, sessionDecorator);
        logger.info("alarm connection {} established.", sessionId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        // IE浏览器不间断发送0字节的PongMessage以维持WebSocket连接，服务端忽略此消息；服务端只处理文本消息
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String msgText = textMessage.getPayload();
            logger.debug("handle message：{}", msgText);
            Map<String, Object> msgMap;
            try {
                msgMap = JSONUtil.parseToMap(msgText);
            } catch (Exception e) {
                logger.error("报警业务：解析浏览器发送的消息异常：{}", e.getMessage());
                return;
            }
            int biz = (int) msgMap.get("biz");
            switch (biz) {
                case WEB_SOCKET_OPEN:
                    // WebSocket启动
                    dealWebSocketOpen(session);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理WebSocket启动业务
     *
     * @param session {@link WebSocketSession}
     */
    private void dealWebSocketOpen(WebSocketSession session) {
        // 报警业务类型 （100 消除报警，110 报警(default)，111 缓存报警）
        List<AlarmInfo> alarmRecordsCache = alarmRecordService.findNotElimitedAlarmInfo();
        if (EmptyObjectUtil.isEmptyList(alarmRecordsCache)) {
            return;
        }
        Long sessionId = Long.parseLong(session.getId(), 16);
        ConcurrentWebSocketSessionDecorator sessionDecorator = WEB_SOCKET_CLIENTS.get(sessionId);
        try {
            Map<String, Object> alarmCacheMap = new HashMap<>();
            alarmCacheMap.put("biz", ALARM_CACHE);
            alarmCacheMap.put("cacheAlarm", alarmRecordsCache);
            String alarmCacheMsg = JSONUtil.stringify(alarmCacheMap);
            sessionDecorator.sendMessage(new TextMessage(alarmCacheMsg));
        } catch (Exception e) {
            logger.error("send cache alarm msg error！", e);
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                logger.error("报警业务：关闭传输错误的客户端异常：{}", e.getMessage());
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
        //     Long userId = SESSION_USER_CACHE.get(sessionId).longValue();
        //     Session userSession = SessionUtil.getSession(userId);
        //     SessionUtil.removeSession(userSession);
        //     Map<String, Object> attributes = session.getAttributes();
        //     HttpSession httpSession = (HttpSession) attributes.get(HandshakeInterceptor.HTTP_SESSION_ATTR_NAME);
        //     httpSession.invalidate();
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
     * @param sessionId {@link WebSocketSession#getId()}
     */
    private void dealConnectionClose(Long sessionId) {
        // 移除WebSocket客户端缓存
        WEB_SOCKET_CLIENTS.remove(sessionId);
    }

    /**
     * 广播报警信息
     *
     * @param alarmId {@link Long} 报警ID
     */
    public void broadcastAlarm(Long alarmId) {
        AlarmRecord alarmRecord = alarmRecordService.getRecordById(alarmId);
        if (alarmRecord == null) {
            logger.warn("alarm record is null at id: {}", alarmId);
            return;
        }
        logger.info("broadcast alarm: {}", alarmRecord);
        String alarmMsg;
        try {
            alarmMsg = JSONUtil.stringify(alarmRecord);
        } catch (Exception e) {
            logger.error("AlarmRecord对象转为JSON字符串异常：{}", e.toString());
            return;
        }
        WEB_SOCKET_CLIENTS.values().forEach(session -> WebSocketUtil.sendConcurrentMsg(session, alarmMsg));
    }

    /**
     * 广播消除报警
     *
     * @param clearAlarmId {@link Long} 消除报警的报警ID
     */
    public void broadcastClearAlarm(Long clearAlarmId) {
        logger.info("broadcast eliminate alarm: {}", clearAlarmId);
        StringBuffer strBuf = new StringBuffer();
        strBuf.append('{');
        strBuf.append("\"biz\":").append(ALARM_CLEAR).append(',');
        strBuf.append("\"id\":").append(clearAlarmId);
        strBuf.append('}');
        WEB_SOCKET_CLIENTS.values().forEach(session -> WebSocketUtil.sendConcurrentMsg(session, strBuf));
    }

    /**
     * 广播消除多条报警
     *
     * @param alarmIds {@link Long} 消除报警的报警ID集合
     */
    public void broadcastClearAlarms(List<Long> alarmIds) {
        alarmIds.forEach(alarmId -> broadcastClearAlarm(alarmId));
    }
}
