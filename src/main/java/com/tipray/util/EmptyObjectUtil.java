package com.tipray.util;

import java.util.List;
import java.util.Map;

/**
 * 空对象判断工具类
 * 
 * @author chenlong
 * @version 1.0 2018-01-12
 *
 */
public class EmptyObjectUtil {
	/**
	 * 判断空字符串
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static boolean isEmptyString(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * 判断空数组（byte）
	 * 
	 * @param bytes
	 *            byte数组
	 * @return
	 */
	public static boolean isEmptyArray(byte[] bytes) {
		return bytes == null || bytes.length == 0;
	}

	/**
	 * 判断空数组（char）
	 * 
	 * @param chars
	 *            char数组
	 * @return
	 */
	public static boolean isEmptyArray(char[] chars) {
		return chars == null || chars.length == 0;
	}

	/**
	 * 判断空数组（short）
	 * 
	 * @param shorts
	 *            short数组
	 * @return
	 */
	public static boolean isEmptyArray(short[] shorts) {
		return shorts == null || shorts.length == 0;
	}

	/**
	 * 判断空数组（int）
	 * 
	 * @param ints
	 *            int数组
	 * @return
	 */
	public static boolean isEmptyArray(int[] ints) {
		return ints == null || ints.length == 0;
	}

	/**
	 * 判断空数组（long）
	 * 
	 * @param longs
	 *            long数组
	 * @return
	 */
	public static boolean isEmptyArray(long[] longs) {
		return longs == null || longs.length == 0;
	}

	/**
	 * 判断空数组（float）
	 * 
	 * @param floats
	 *            float数组
	 * @return
	 */
	public static boolean isEmptyArray(float[] floats) {
		return floats == null || floats.length == 0;
	}

	/**
	 * 判断空数组（double）
	 * 
	 * @param doubles
	 *            double数组
	 * @return
	 */
	public static boolean isEmptyArray(double[] doubles) {
		return doubles == null || doubles.length == 0;
	}

	/**
	 * 判断空数组（封装类）
	 * 
	 * @param objs
	 *            封装类数组对象
	 * @return
	 */
	public static boolean isEmptyArray(Object[] objs) {
		return objs == null || objs.length == 0;
	}

	/**
	 * 判断空List
	 * 
	 * @param list
	 *            List对象
	 * @return
	 */
	public static boolean isEmptyList(List<?> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * 判断空Map
	 * 
	 * @param map
	 *            Map对象
	 * @return
	 */
	public static boolean isEmptyMap(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

}
