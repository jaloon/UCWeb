package com.tipray.core.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tipray.bean.baseinfo.User;
import com.tipray.core.ThreadVariable;
import com.tipray.util.SessionUtil;

/**
 * 退出登录监听器
 * 
 * @author chenlong
 * @version 1.0 2018-04-17
 *
 */
public class LogoutListener implements HttpSessionListener {
	private static final Logger logger = LoggerFactory.getLogger(LogoutListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent se) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		User user = ThreadVariable.getUser();
		if (user == null) {
			return;
		}
		ThreadVariable.removeSession();
		String sessionId = se.getSession().getId();
		// HttpSesssionCache.HTTP_SESSION_CACHE.remove(sessionId);
		SessionUtil.removeSession(sessionId);
		logger.info("操作员{}（{}）退出登录！",user.getName(), user.getAccount());
	}

}
