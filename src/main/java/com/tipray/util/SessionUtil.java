package com.tipray.util;

import com.tipray.bean.Session;
import com.tipray.bean.baseinfo.User;
import com.tipray.core.ThreadVariable;
import com.tipray.service.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户登录信息Session工具类
 * 
 * @1、每一个账号登录时，都会在此缓存住当前账号的sessionId
 * @2、当前请求的sessionId与工具类中缓存的sessionId不同时，说明账号已在异地登录
 * @3、登录后，如果长时间没有操作系统，系统将自动退出登录
 * @author chends
 *
 */
public class SessionUtil {
	public static final String LOGIN_SESSION_ID = "sid";
	public static final String OLD_LOGIN_SESSION_ID = "oldSid";
	public static final long SESSION_TIMEOUT_MILLISECOND = 30 * 60 * 1000L;
	public static final long WEBAPP_TIMEOUT_MILLISECOND = 6 * 60 * 1000L;

	private static final Map<String, Session> SESSION_LIST = new ConcurrentHashMap<String, Session>();
	private static final SessionService SESSION_SERVICE = SpringBeanUtil.getSessionService();

	/**
	 * 添加session到列表
	 * 
	 * @param session
	 * @return
	 * @exception NullPointerException
	 *                if session/sessionId is null.
	 */
	public static Session addSessionToList(Session session) {
		if (session == null) {
			throw new NullPointerException("session is null!");
		}
		String sessionId = session.getUuid();
		if (sessionId == null) {
			throw new NullPointerException("sessionId is null!");
		}
		return SESSION_LIST.put(sessionId, session);
	}

	/**
	 * 从列表移除session
	 * 
	 * @param sessionId
	 * @return
	 * @exception NullPointerException
	 *                if sessionId is null.
	 */
	public static Session removeSessionFromList(String sessionId) {
		if (sessionId == null) {
			throw new NullPointerException("sessionId is null!");
		}
		return SESSION_LIST.remove(sessionId);
	}

	/**
	 * 新增
	 * 
	 * @param session
	 */
	public static void cacheSession(HttpServletRequest request, HttpServletResponse response, Session session) {
		if (session != null) {
			// 避免浏览器禁用session，在request.session中也保存一份
			request.getSession().setAttribute(LOGIN_SESSION_ID, session.getUuid());
			CookieUtil.set(response, LOGIN_SESSION_ID, session.getUuid());
			addSessionToList(session);
		}
	}

	/**
	 * 获取session
	 * 
	 * @param request
	 * @return
	 */
	public static Session getSession(HttpServletRequest request) {
		String sessionId = getSessionId(request, LOGIN_SESSION_ID);
		return SESSION_SERVICE.getSessionByUUID(sessionId);
	}

	/**
	 * 获取session
	 * 
	 * @param userId
	 * @return
	 */
	public static Session getSession(Long userId) {
		return SESSION_SERVICE.getSessionByUser(userId);
	}

	/**
	 * 获取替换当前session的新session
	 * 
	 * @param request
	 * @return
	 */
	public static Session getOldSession(HttpServletRequest request) {
		String sessionId = getSessionId(request, LOGIN_SESSION_ID);
		return SESSION_SERVICE.getSessionByOldUuid(sessionId);
	}

	/**
	 * 获取sessionId
	 * 
	 * @param request
	 * @param key
	 *            session的关键字：LOGIN_SESSION_ID | OLD_LOGIN_SESSION_ID
	 * @return
	 */
	private static String getSessionId(HttpServletRequest request, String key) {
		String sessionId = null;
		if (request != null) {
			sessionId = request.getParameter(key);
			if (sessionId == null || sessionId.trim().isEmpty()) {
				sessionId = CookieUtil.get(request, key);
			}
			if (sessionId == null || sessionId.trim().isEmpty()) {
				sessionId = (String) request.getSession().getAttribute(key);
			}
		}
		return sessionId;
	}

	/**
	 * 获取登录时的Session ID
	 * @param request
	 * @return
	 */
	public static String getLoginSessionId(HttpServletRequest request) {
		String sessionId = getSessionId(request,LOGIN_SESSION_ID);
		if (sessionId == null) {
			sessionId = UUIDUtil.getHexUUID();
			synchronized (SESSION_LIST) {
				SESSION_LIST.clear();
				SESSION_LIST.putAll(SESSION_SERVICE.findSessions());
				while (SESSION_LIST.keySet().contains(sessionId)) {
					sessionId = UUIDUtil.getHexUUID();
				}
			}
		}
		return sessionId;
	}

	/**
	 * 获取退出时的Session ID
	 * @param request
	 * @return
	 */
	public static String getLogoutSessionId(HttpServletRequest request) {
		return getSessionId(request,LOGIN_SESSION_ID);
	}

	/**
	 * 判断是否登录超时
	 * 
	 * @param session
	 * @return
	 */
	public static boolean isLoginTimeout(Session session) {
		if (session != null) {
			long timeout = System.currentTimeMillis() - session.getOperateDate().getTime();
			boolean isApp = session.getIsApp() > 0;
			if (isApp) {
				return timeout > WEBAPP_TIMEOUT_MILLISECOND;
			}
			return timeout > SESSION_TIMEOUT_MILLISECOND;
		}
		return false;
	}

	/**
	 * 更新用户session
	 * 
	 * @param user
	 * @return
	 */
	public static void updateUser(User user) {
		if (user != null) {
			for (Session session : SESSION_LIST.values()) {
				if (session.getUser().getId() == user.getId()) {
					session.setUser(user);
				}
			}
		}
	}

	/**
	 * 更新session内容 更新session的操作时间，为超时退出做准备
	 * 
	 * @param request
	 */
	public static void updateSession(HttpServletRequest request) {
		if (request == null) {
			return;
		}
		Session session = getSession(request);
		if (session != null) {
			session.setOperateDate(new Date());
			request.getSession().setAttribute("sid", session.getUuid());
			ThreadVariable.setSession(session);
			SESSION_SERVICE.updateSession(session);
		}
	}

	/**
	 * 清除session信息
	 * 
	 * @param request
	 */
	public static void removeSession(HttpServletRequest request) {
		if (request == null) {
			return;
		}
		Session session = getSession(request);
		removeSession(session);
		// ThreadVariable.removeSession();
		request.getSession().invalidate();
	}

	/**
	 * 清除session信息
	 * 
	 * @param session
	 */
	public static void removeSession(Session session) {
		if (session != null) {
			String sessionId = session.getUuid();
			removeSession(sessionId);
		}
	}

	/**
	 * 清除session信息
	 * 
	 * @param sessionId
	 */
	public static void removeSession(String sessionId) {
		if (sessionId != null && !sessionId.trim().isEmpty()) {
			SESSION_SERVICE.deleteSessionByUUID(sessionId);
			removeSessionFromList(sessionId);
		}
	}

	/**
	 * 清除某个用户的session
	 * 
	 * @param userId
	 */
	public static void removeByUserId(Long userId) {
		if (userId == null) {
			return;
		}

		for (Iterator<Session> it = SESSION_LIST.values().iterator(); it.hasNext();) {
			Session session = it.next();
			if (session.getUser().getId() == userId) {
				it.remove();
			}
		}
	}

	// public static List<Session> getSessionList(String search, Page page){
	// cleanTimeoutSession();
	// List<Session> result = new ArrayList<Session>();
	// List<Session> list = new ArrayList<Session>(sessionList.values());
	// if(page.getSidx()!=null && !page.getSidx().equals("id")){
	// Collections.sort(list,new SortField(page.getSidx(),page.getSord()));
	// }
	// int startRow = (page.getPage()-1)*page.getRows();
	// int endRow = Math.min(page.getPage()*page.getRows(), list.size());
	// for (int i=startRow; i<endRow; i++) {
	// Session session = list.get(i);
	// if(PermissionUtil.isPermissionDept(session.getUser().getDeptId())){
	// if(StringUtil.isEmpty(search)
	// || (session.getUser().getName().toLowerCase().contains(search)
	// || session.getUser().getAccount().toLowerCase().contains(search))){
	// result.add(session);
	// }
	// }
	// }
	// page.setTotal(list.size());
	// return result;
	// }

	static class SortField implements Comparator<Session> {
		/** 排序字段 */
		private String sidx;
		/** 排序方式: asc desc */
		private String sord;

		public SortField(String sidx, String sord) {
			this.sidx = sidx;
			this.sord = sord;
		}

		@Override
		public int compare(Session u1, Session u2) {
			int result;
			if ("name".equals(sidx)) {
				result = u1.getUser().getName().compareTo(u2.getUser().getName());
			} else if ("account".equals(sidx)) {
				result = u1.getUser().getAccount().compareTo(u2.getUser().getAccount());
			} else if ("ip".equals(sidx)) {
				result = u1.getIp().compareTo(u2.getIp());
			} else if ("loginDate".equals(sidx)) {
				result = u1.getLoginDate().compareTo(u2.getLoginDate());
			} else if ("operateDate".equals(sidx)) {
				result = u1.getOperateDate().compareTo(u2.getOperateDate());
			} else {
				result = u1.getUuid().compareTo(u2.getUuid());
			}
			return "desc".equals(sord) ? -result : result;
		}
	}

}
