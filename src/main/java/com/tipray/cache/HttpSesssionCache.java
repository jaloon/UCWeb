package com.tipray.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

/**
 * HttpSesssion缓存
 * 
 * @author chenlong
 * @version 1.0 2018-04-18
 *
 */
public class HttpSesssionCache {
	/** HttpSesssion缓存 Map&lt;sessionId, HttpSesssion&gt; */
	public static final Map<String, HttpSession> HTTP_SESSION_CACHE = new ConcurrentHashMap<>();
}
