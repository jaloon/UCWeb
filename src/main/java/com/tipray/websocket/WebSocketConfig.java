package com.tipray.websocket;

import com.tipray.websocket.handler.AlarmWebSocketHandler;
import com.tipray.websocket.handler.MonitorWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * SpringWebSocket配置
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(alarmHandler(), "/alarm").addInterceptors(interceptor());
		registry.addHandler(alarmHandler(), "/sockjs/alarm").setAllowedOrigins("*").addInterceptors(interceptor())
				.withSockJS();
		registry.addHandler(trackHandler(), "/track").addInterceptors(interceptor());
		registry.addHandler(trackHandler(), "/sockjs/track").setAllowedOrigins("*").addInterceptors(interceptor())
				.withSockJS();
	}

	@Bean
	public WebSocketHandler alarmHandler() {
		return new AlarmWebSocketHandler();
	}

	@Bean
	public WebSocketHandler trackHandler() {
		return new MonitorWebSocketHandler();
	}

	@Bean
	public HandshakeInterceptor interceptor() {
		return new HandshakeInterceptor();
	}
}
