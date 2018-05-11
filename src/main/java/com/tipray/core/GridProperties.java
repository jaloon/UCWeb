package com.tipray.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GridProperties
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class GridProperties {
	private static Logger logger = LoggerFactory.getLogger(GridProperties.class);
	private static Properties properties = new Properties();

	private static Properties getGridProperties() {
		if (properties.size() == 0) {
			synchronized (properties) {
				properties = new Properties();
				try {
					properties.load(GridProperties.class.getClassLoader().getResourceAsStream("grid.properties"));
				} catch (IOException e) {
					logger.error("加载grid.properties出错！\n{}", e.toString());
				}
			}
		}
		return properties;
	}

	private static String getValue(String key) {
		return getGridProperties().getProperty(key);
	}

	/** 数据库重置 */
	public static final boolean IS_DB_RESET = toBool(getValue("db.reset"), false);
	/** 是否是开发环境 */
	public static final boolean IS_WEB_DEVELOPMENT = toBool(getValue("web.development"), false);
	/** 不用检测是否登录的url */
	public static final List<String> NOT_VALIDATE_PATH = toList(getValue("not.validate.path"), ";");

	/**
	 * 布尔字符串装bool
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static boolean toBool(String str, boolean defaultValue) {
		if (str == null || str.trim().isEmpty()) {
			return defaultValue;
		}
		str = str.trim();
		return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || "1".equals(str.trim());
	}

	public static List<String> toList(String str, String split) {
		List<String> list = new ArrayList<String>();
		if (str != null && !str.isEmpty()) {
			String[] strArray = str.split(split);
			for (String s : strArray) {
				if (s != null && !s.isEmpty()) {
					list.add(s);
				}
			}
		}
		return list;
	}
}
