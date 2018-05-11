package com.tipray.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tipray.bean.Session;
import com.tipray.bean.baseinfo.Permission;
import com.tipray.bean.baseinfo.Role;
import com.tipray.bean.baseinfo.User;
import com.tipray.util.CacheUtil;

/**
 * 线程变量
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ThreadVariable {
	private static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();

	public static void clear() {
		threadLocal.remove();
	}

	private static Map getMap() {
		if (threadLocal.get() == null) {
			threadLocal.set(new HashMap());
		}
		return (Map) threadLocal.get();
	}

	public static void setSession(Session session) {
		// 此处session是根据sessionId获取的。
		getMap().put(Session.class.getName(), session);
	}

	public static Session getSession() {
		return (Session) getMap().get(Session.class.getName());
	}

	public static void removeSession() {
		getMap().remove(Session.class.getName());
	}

	public static User getUser() {
		Session session = getSession();
		return session != null ? session.getUser() : null;
	}

	public static Role getRole() {
		User user = getUser();
		return user != null ? CacheUtil.getCacheRole(user.getRole().getId()) : null;
	}

	public static Role getAppRole() {
		User user = getUser();
		return user != null ? CacheUtil.getCacheRole(user.getAppRole().getId()) : null;
	}

	public static List<Permission> getPermissions() {
		Role role = getRole();
		return role != null ? role.getPermissions() : null;
	}

	public static List<Permission> getAppPermissions() {
		Role appRole = getAppRole();
		return appRole != null ? appRole.getPermissions() : null;
	}

}
