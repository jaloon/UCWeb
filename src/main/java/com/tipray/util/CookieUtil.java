package com.tipray.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class CookieUtil {
	/**
	 * 设置cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 */
	public static void set(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

    /**
     * 设置cookie
     *
     * @param response
     * @param name
     * @param value
     * @param secure
     */
	public static void set(HttpServletResponse response, String name, String value, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setSecure(secure);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

	/**
	 * 设置Cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 *            生存周期，单位秒
	 */
	public static void set(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	/**
	 * 获取cookie
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static String get(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 删除cookie
	 * 
	 * @param response
	 * @param name
	 */
	public static void del(HttpServletResponse response, String name) {
		set(response, name, null, 0);
	}
}
