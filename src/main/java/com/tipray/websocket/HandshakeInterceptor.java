package com.tipray.websocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * SpringWebSocket握手拦截器
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(HandshakeInterceptor.class);
	/** The name of the attribute under which the HTTP session is exposed. */
	public static final String HTTP_SESSION_ATTR_NAME = "HTTP.SESSION";

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		logger.debug("Before {} Handshake：{}", wsHandler, request);
		// 解决The extension [x-webkit-deflate-frame] is not supported问题
		if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
			request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
		}
		// 获取HttpSession
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
			if (httpSession != null) {
				logger.debug("HttpSession of this connect is {}", httpSession.getId());
				attributes.put(HTTP_SESSION_ATTR_NAME, httpSession);
			} else {
				logger.warn("this connect cannot get HttpSession");
			}
		}
		return super.beforeHandshake(request, response, wsHandler, attributes);
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		logger.debug("After {} Handshake：{} {}", wsHandler, request, exception);
		super.afterHandshake(request, response, wsHandler, exception);
	}

}
