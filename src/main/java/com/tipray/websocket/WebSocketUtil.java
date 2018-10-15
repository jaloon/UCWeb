package com.tipray.websocket;

import com.tipray.util.JSONUtil;
import com.tipray.websocket.protocol.WebSocketProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * WebSocket工具类
 *
 * @author chenlong
 * @version 1.0 2018-05-17
 */
public class WebSocketUtil {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketUtil.class);
    private static final Map<String, Long> SOCKJS_SESSION_ID_CACHE = new ConcurrentHashMap<>();

    private WebSocketUtil() {
    }

    /**
     * 获取session id
     *
     * @param session {@link WebSocketSession}
     * @return {@link Long} session id
     */
    public static long getSessionId(WebSocketSession session) {
        try {
            return Long.parseLong(session.getId(), 16);
        } catch (Exception e) {
            Long sessionId = SOCKJS_SESSION_ID_CACHE.get(session.getId());
            if (sessionId == null) {
                sessionId = System.currentTimeMillis();
                SOCKJS_SESSION_ID_CACHE.put(session.getId(), sessionId);
            }
            return sessionId;
        }
    }

    /**
     * 关闭WebSocket连接
     *
     * @param session {@link WebSocketSession}
     * @return {@link Long} session id
     */
    public static long closeSession(WebSocketSession session) {
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("关闭传输错误的WebSocket连接异常：{}", e.getMessage());
                }
            }
        }
        Long sessionId = SOCKJS_SESSION_ID_CACHE.get(session.getId());
        if (sessionId == null) {
            try {
                sessionId = Long.parseLong(session.getId(), 16);
            } catch (Exception e) {
                // session id 缓存已被移除
                return -1L;
            }
        } else {
            SOCKJS_SESSION_ID_CACHE.remove(session.getId());
        }
        return sessionId;
    }

    /**
     * 使用并发WebSocketSession装饰器装饰WebSocketSession
     *
     * @param session {@link WebSocketSession}
     * @return {@link ConcurrentWebSocketSessionDecorator}
     */
    public static ConcurrentWebSocketSessionDecorator decoratorSession(WebSocketSession session) {
        return new ConcurrentWebSocketSessionDecorator(session, 15000, 1000000);
    }

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
        if (!session.isOpen()) {
            if (logger.isDebugEnabled()) {
                logger.debug("WebSocket connection is not still open!");
            }
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
            logger.error("send message error！", e);
        }
    }

    /**
     * 发送文本信息给指定客户端
     *
     * @param session {@link WebSocketSession}
     * @param message {@link CharSequence} 待发送文本信息
     */
    public static void sendTextMsg(WebSocketSession session, CharSequence message) {
        // 给session加锁，防止WebSocket多线程推送出错[TEXT_PARTIAL_WRITING] （不能完全避免，推荐使用sendConcurrentMsg()方法）
        // java.lang.IllegalStateException: The remote endpoint was in state [TEXT_PARTIAL_WRITING] which is an invalid state for called method
        // The problem is that several thread try to write in same time on the socket so
        if (session == null || message == null || message.length() == 0) {
            return;
        }

        synchronized (session) {
            if (!session.isOpen()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("WebSocket connection is not still open!");
                }
                return;
            }
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                logger.error("send text message error！", e);
            }
        }
    }

    /**
     * 并发发送文本信息给指定客户端
     *
     * @param session {@link ConcurrentWebSocketSessionDecorator}
     * @param message {@link CharSequence} 待发送文本信息
     */
    public static void sendConcurrentMsg(ConcurrentWebSocketSessionDecorator session, CharSequence message) {
        if (session == null || session.getDelegate() == null || message == null || message.length() == 0) {
            return;
        }
        if (!session.isOpen()) {
            if (logger.isDebugEnabled()) {
                logger.debug("WebSocket connection is not still open!");
            }
            return;
        }
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            logger.error("send text message error stack！", e);
        }
    }

    /**
     * 并发发送文本信息给指定客户端，并移除连接关闭的客户端
     *
     * @param sessionId  {@link Long} session id
     * @param session    {@link ConcurrentWebSocketSessionDecorator}
     * @param message    {@link CharSequence} 待发送文本信息
     * @param removeFunc {@link Consumer} 移除session缓存的函数
     */
    public static void sendConcurrentMsg(Long sessionId,
                                         ConcurrentWebSocketSessionDecorator session,
                                         CharSequence message,
                                         Consumer<Long> removeFunc) {
        if (session == null || session.getDelegate() == null || message == null || message.length() == 0) {
            return;
        }
        if (!session.isOpen()) {
            if (logger.isDebugEnabled()) {
                logger.debug("WebSocket connection is not still open!");
            }
            removeFunc.accept(sessionId);
            return;
        }
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            logger.error("send text message error stack！", e);
        }
    }

    /**
     * 并发发送WebSocket通信业务信息给指定客户端
     *
     * @param session  {@link ConcurrentWebSocketSessionDecorator}
     * @param protocol {@link WebSocketProtocol} WebSocket通信业务信息
     */
    public static void sendConcurrentMsg(ConcurrentWebSocketSessionDecorator session, WebSocketProtocol protocol) {
        if (session == null || session.getDelegate() == null || protocol == null) {
            return;
        }
        if (!session.isOpen()) {
            if (logger.isDebugEnabled()) {
                logger.debug("WebSocket connection is not still open!");
            }
            return;
        }
        try {
            String message = JSONUtil.stringify(protocol);
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            logger.error("send text message error stack!", e);
        }
    }

    /**
     * 并发发送WebSocket通信业务信息给指定客户端，并移除连接关闭的客户端
     *
     * @param sessionId  {@link Long} session id
     * @param session    {@link ConcurrentWebSocketSessionDecorator}
     * @param protocol   {@link WebSocketProtocol} WebSocket通信业务信息
     * @param removeFunc {@link Consumer} 移除session缓存的函数
     */
    public static void sendConcurrentMsg(Long sessionId,
                                         ConcurrentWebSocketSessionDecorator session,
                                         WebSocketProtocol protocol,
                                         Consumer<Long> removeFunc) {
        if (session == null || session.getDelegate() == null || protocol == null) {
            return;
        }
        if (!session.isOpen()) {
            if (logger.isDebugEnabled()) {
                logger.debug("WebSocket connection is not still open!");
            }
            removeFunc.accept(sessionId);
            return;
        }
        try {
            String message = JSONUtil.stringify(protocol);
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            logger.error("send text message error stack!", e);
        }
    }
}