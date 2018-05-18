package com.tipray.websocket;

import com.tipray.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket工具类
 *
 * @author chenlong
 * @version 1.0 2018-05-17
 */
public class WebSocketUtil {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketUtil.class);

    private WebSocketUtil() {}

    /**
     * 发送信息给指定客户端
     *
     * @param session {@link WebSocketSession}
     * @param message {@link Object} 待发送信息
     */
    public static void sendMsg(WebSocketSession session, Object message) {
        if (session == null || message == null) {
            return;
        }
        Long sessionId = Long.parseLong(session.getId(), 16);
        if (!session.isOpen()) {
            logger.warn("web socket connection {} is not still open", sessionId);
            return;
        }
        try {
            if (message instanceof byte[]) {
                session.sendMessage(new TextMessage((byte[]) message));
                return;
            }
            CharSequence payload;
            if (message instanceof CharSequence) {
                payload = (CharSequence) message;
            } else {
                payload = JSONUtil.stringify(message);
            }
            if (payload.length() == 0) {
                return;
            }
            session.sendMessage(new TextMessage(payload));
        } catch (Exception e) {
            logger.error("send message to {} error: {}", sessionId, e.getMessage());
            logger.debug("send message error stack: ", e);
        }
    }

    /**
     * 发送文本信息给指定客户端
     *
     * @param session {@link WebSocketSession}
     * @param message {@link CharSequence} 待发送文本信息
     */
    public static void sendTextMsg(WebSocketSession session, CharSequence message) {
        if (session == null || message == null || message.length() == 0) {
            return;
        }
        Long sessionId = Long.parseLong(session.getId(), 16);
        if (!session.isOpen()) {
            logger.warn("web socket connection {} is not still open", sessionId);
            return;
        }
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            logger.error("send text message to {} error: {}", sessionId, e.getMessage());
            logger.debug("send text message error stack: ", e);
        }
    }
}
