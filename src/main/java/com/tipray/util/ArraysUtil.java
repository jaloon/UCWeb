package com.tipray.util;

/**
 * 数组处理工具类
 * 
 * @author chenlong
 * @version 1.0 2018-03-13
 *
 */
public class ArraysUtil {
	/**
	 * 封装类数组反转
	 * 
	 * @param array
	 *            封装类数组
	 * @return 封装类反转数组
	 */
	public static <T> T[] reverse(T[] array) {
		int len = array.length;
		T[] reverseArray = array;
		for (int i = 0; i < len; i++) {
			// 反转后数组的第一个元素等于源数组的最后一个元素
			reverseArray[i] = array[len - i - 1];
		}
		return reverseArray;
	}

	/**
	 * byte数组反转
	 * 
	 * @param bytes
	 *            byte数组
	 * @return byte反转数组
	 */
	public static byte[] reverse(byte[] bytes) {
		int len = bytes.length;
		byte[] reverseArray = new byte[len];
		for (int i = 0; i < len; i++) {
			reverseArray[i] = bytes[len - i - 1];
		}
		return reverseArray;
	}

	/**
	 * char数组反转
	 * 
	 * @param chars
	 *            char数组
	 * @return char反转数组
	 */
	public static char[] reverse(char[] chars) {
		int len = chars.length;
		char[] reverseArray = new char[len];
		for (int i = 0; i < len; i++) {
			reverseArray[i] = chars[len - i - 1];
		}
		return reverseArray;
	}

	/**
	 * short数组反转
	 * 
	 * @param shorts
	 *            short数组
	 * @return short反转数组
	 */
	public static short[] reverse(short[] shorts) {
		int len = shorts.length;
		short[] reverseArray = new short[len];
		for (int i = 0; i < len; i++) {
			reverseArray[i] = shorts[len - i - 1];
		}
		return reverseArray;
	}

	/**
	 * int数组反转
	 * 
	 * @param ints
	 *            int数组
	 * @return int反转数组
	 */
	public static int[] reverse(int[] ints) {
		int len = ints.length;
		int[] reverseArray = new int[len];
		for (int i = 0; i < len; i++) {
			reverseArray[i] = ints[len - i - 1];
		}
		return reverseArray;
	}

	/**
	 * long数组反转
	 * 
	 * @param longs
	 *            long数组
	 * @return long反转数组
	 */
	public static long[] reverse(long[] longs) {
		int len = longs.length;
		long[] reverseArray = new long[len];
		for (int i = 0; i < len; i++) {
			reverseArray[i] = longs[len - i - 1];
		}
		return reverseArray;
	}

	/**
	 * float数组反转
	 * 
	 * @param floats
	 *            float数组
	 * @return float反转数组
	 */
	public static float[] reverse(float[] floats) {
		int len = floats.length;
		float[] reverseArray = new float[len];
		for (int i = 0; i < len; i++) {
			reverseArray[i] = floats[len - i - 1];
		}
		return reverseArray;
	}

	/**
	 * double数组反转
	 * 
	 * @param doubles
	 *            double数组
	 * @return double反转数组
	 */
	public static double[] reverse(double[] doubles) {
		int len = doubles.length;
		double[] reverseArray = new double[len];
		for (int i = 0; i < len; i++) {
			reverseArray[i] = doubles[len - i - 1];
		}
		return reverseArray;
	}

}
