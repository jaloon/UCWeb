package com.tipray.websocket.handler;

import com.tipray.bean.alarm.AlarmInfo;
import com.tipray.service.AlarmRecordService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.JSONUtil;
import com.tipray.websocket.WebSocketCloseStatusEnum;
import com.tipray.websocket.WebSocketUtil;
import com.tipray.websocket.protocol.WebSocketBizId;
import com.tipray.websocket.protocol.WebSocketProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import javax.annotation.Resource;
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
    @Resource
    private AlarmRecordService alarmRecordService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        long sessionId = WebSocketUtil.getSessionId(session);
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
            if (logger.isDebugEnabled()) {
                logger.debug("handle message：{}", msgText);
            }
            WebSocketProtocol protocol;
            try {
                protocol = JSONUtil.parseToObject(msgText, WebSocketProtocol.class);
            } catch (Exception e) {
                logger.error("报警业务：解析浏览器发送的消息异常：{}", e.getMessage());
                return;
            }
            int biz = protocol.getBiz();
            switch (biz) {
                case WebSocketBizId.WEB_SOCKET_OPEN:
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
        long sessionId = WebSocketUtil.getSessionId(session);
        ConcurrentWebSocketSessionDecorator sessionDecorator = WEB_SOCKET_CLIENTS.get(sessionId);
        try {
            WebSocketProtocol protocol = new WebSocketProtocol(WebSocketBizId.CACHE_ALARM, alarmRecordsCache);
            String alarmCacheMsg = JSONUtil.stringify(protocol);
            sessionDecorator.sendMessage(new TextMessage(alarmCacheMsg));
        } catch (Exception e) {
            logger.error("send cache alarm msg error！", e);
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        long sessionId = WebSocketUtil.closeSession(session);
        dealConnectionClose(sessionId);
        logger.error("alarm connection {} transport error: {}", sessionId, exception.toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        long sessionId = WebSocketUtil.closeSession(session);
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
    private void dealConnectionClose(long sessionId) {
        if (sessionId > -1) {
            // 移除WebSocket客户端缓存
            WEB_SOCKET_CLIENTS.remove(sessionId);
        }
    }

    /**
     * 广播报警信息
     *
     * @param alarmId {@link Long} 报警ID
     */
    public void broadcastAlarm(Long alarmId) {
        AlarmInfo alarmInfo = alarmRecordService.getAlarmInfoByAlarmId(alarmId);
        if (alarmInfo == null) {
            logger.warn("alarm record is null at id: {}", alarmId);
            return;
        }
        logger.info("broadcast alarm: {}", alarmInfo);
        WebSocketProtocol protocol = new WebSocketProtocol(WebSocketBizId.NEW_ALARM, alarmInfo);
        WEB_SOCKET_CLIENTS.forEach((sessionId, session) -> sendConcurrentMsg(sessionId, session, protocol));
    }

    /**
     * 广播消除报警
     *
     * @param clearAlarmId {@link Long} 消除报警的报警ID
     */
    public void broadcastClearAlarm(Long clearAlarmId) {
        logger.info("broadcast eliminate alarm: {}", clearAlarmId);
        upAlarmInfos();
        // StringBuffer strBuf = new StringBuffer();
        // strBuf.append('{');
        // strBuf.append("\"biz\":").append(WebSocketBizId.ELIMINATE_ALARM).append(',');
        // strBuf.append("\"id\":").append(clearAlarmId);
        // strBuf.append('}');
        // WEB_SOCKET_CLIENTS.values().forEach(session -> WebSocketUtil.sendConcurrentMsg(session, strBuf));
    }

    /**
     * 广播消除多条报警
     *
     * @param alarmIds {@link Long} 消除报警的报警ID集合
     */
    public void broadcastClearAlarms(List<Long> alarmIds) {
        logger.info("broadcast eliminate alarms: {}", alarmIds);
        upAlarmInfos();
    }

    /**
     * 更新报警信息
     */
    private void upAlarmInfos() {
        List<AlarmInfo> alarmInfos = alarmRecordService.findNotElimitedAlarmInfo();
        pushAlarmIfos(alarmInfos);
    }

    /**
     * 推送最新报警信息集合
     *
     * @param alarmInfos {@link AlarmInfo} 最新未消除报警信息集合
     */
    public void pushAlarmIfos(List<AlarmInfo> alarmInfos) {
        WebSocketProtocol protocol = new WebSocketProtocol(WebSocketBizId.ELIMINATE_ALARM, alarmInfos);
        WEB_SOCKET_CLIENTS.forEach((sessionId, session) -> sendConcurrentMsg(sessionId, session, protocol));
    }

    /**
     * 并发发送WebSocket通信业务信息给指定客户端，并移除连接关闭的客户端
     *
     * @param sessionId {@link Long} session id
     * @param session   {@link ConcurrentWebSocketSessionDecorator}
     * @param protocol  {@link WebSocketProtocol} WebSocket通信业务信息
     */
    private void sendConcurrentMsg(Long sessionId,
                                   ConcurrentWebSocketSessionDecorator session,
                                   WebSocketProtocol protocol) {
        WebSocketUtil.sendConcurrentMsg(sessionId, session, protocol, this::dealConnectionClose);
    }
}
