package com.tipray.util;

import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UUID工具类
 * 
 * @author chenlong
 * @version 1.0 2018-04-28
 *
 */
public class UUIDUtil {
	/** UUID验证正则 */
	public static final String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	/** UUID验证正则的编译表示 */
	public static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);

	/**
	 * UUID校验
	 * 
	 * @param uuid
	 *            {@link String} UUID字符串
	 * @return {@link Boolean} 是否UUID字符串
	 */
	public static boolean isUUID(String uuid) {
		if (uuid == null || uuid.trim().isEmpty()) {
			return false;
		}
		Matcher matcher = UUID_PATTERN.matcher(uuid.toLowerCase());
		return matcher.matches();
	}

	/**
	 * UUID令牌验证（是否有效请求）
	 * 
	 * @param token
	 *            {@link String} UUID令牌
	 * @param session
	 *            {@link HttpSession}
	 * @return {@link Boolean} 请求是否有效
	 */
	public static boolean verifyUUIDToken(String token, HttpSession session) {
		if (!UUIDUtil.isUUID(token)) {
			return false;
		}
		String oldToken = (String) session.getAttribute("token");
		if (oldToken == null) {
			return true;
		}
		return !oldToken.equalsIgnoreCase(token);
	}

	/**
	 * 获取一个随机UUID字符串
	 * 
	 * @return {@link String} UUID字符串
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 获取一个随机UUID的16进制字符串
	 * 
	 * @return {@link String} UUID的16进制字符串
	 */
	public static String getHexUUID() {
		return getHexUUIDByUUID(getUUID());
	}

	/**
	 * 获取UUID的16进制字符串形式
	 * 
	 * @param uuid
	 *            {@link String} UUID字符串
	 * @return {@link String} UUID的16进制字符串
	 */
	public static String getHexUUIDByUUID(String uuid) {
		return uuid.replace("-", "");
	}

}
