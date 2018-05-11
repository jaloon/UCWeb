package com.tipray.util;

import java.math.BigInteger;

/**
 * 字节数组与二进制、十六进制字符串转换工具类
 * 
 * @author chenlong
 * @version 1.0 2017-12-14
 *
 */
public class BytesUtil {
	/**
	 * 用于建立十六进制字符的输出的小写字符数组
	 */
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };
	/**
	 * 用于建立十六进制字符的输出的大写字符数组
	 */
	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	/**
	 * byte数组转十六进制字符串（stackoverflow提供的方法，貌似最快）
	 * 
	 * @param bytes
	 *            byte[]
	 * @param toLowerCase
	 *            <code>true</code> 转换成小写格式 ， <code>false</code> 转换成大写格式
	 * @return
	 */
	public static String bytesToHex(byte[] bytes, boolean toLowerCase) {
		char[] hexArray = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * byte转十六进制字符串
	 * 
	 * @param b
	 * @param toLowerCase
	 *            <code>true</code> 转换成小写格式 ， <code>false</code> 转换成大写格式
	 * @return
	 */
	public static String byteToHex(byte b, boolean toLowerCase) {
		char[] hexArray = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
		StringBuffer sb = new StringBuffer();
		int v = b & 0xFF;
		return sb.append(hexArray[v >>> 4]).append(hexArray[v & 0x0F]).toString();
	}

	/**
	 * byte数组转换为二进制字符串,每个字节以英文逗号","分隔
	 * 
	 * @param bytes
	 *            byte数组
	 * @return
	 */
	public static String bytesToBinaryString(byte[] bytes) {
		char[] binaryDigest = { '0', '1' };
		char[] binaryChars = new char[bytes.length * 9];
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			binaryChars[i * 9] = binaryDigest[(b >> 7) & 1];
			binaryChars[i * 9 + 1] = binaryDigest[(b >> 6) & 1];
			binaryChars[i * 9 + 2] = binaryDigest[(b >> 5) & 1];
			binaryChars[i * 9 + 3] = binaryDigest[(b >> 4) & 1];
			binaryChars[i * 9 + 4] = binaryDigest[(b >> 3) & 1];
			binaryChars[i * 9 + 5] = binaryDigest[(b >> 2) & 1];
			binaryChars[i * 9 + 6] = binaryDigest[(b >> 1) & 1];
			binaryChars[i * 9 + 7] = binaryDigest[b & 1];
			binaryChars[i * 9 + 8] = ',';
		}
		return new String(binaryChars, 0, binaryChars.length - 1);
	}

	/**
	 * byte转二进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToBinaryString(byte b) {
		char[] binaryDigest = { '0', '1' };
		char[] binaryChars = new char[8];
		binaryChars[0] = binaryDigest[(b >> 7) & 1];
		binaryChars[1] = binaryDigest[(b >> 6) & 1];
		binaryChars[2] = binaryDigest[(b >> 5) & 1];
		binaryChars[3] = binaryDigest[(b >> 4) & 1];
		binaryChars[4] = binaryDigest[(b >> 3) & 1];
		binaryChars[5] = binaryDigest[(b >> 2) & 1];
		binaryChars[6] = binaryDigest[(b >> 1) & 1];
		binaryChars[7] = binaryDigest[b & 1];
		return new String(binaryChars);
	}

	/**
	 * 二进制字符串转换为byte数组,每个字节以","隔开
	 **/
	public static byte[] binaryStringToBytes(String binaryStr) {
		String[] temp = binaryStr.split(",");
		byte[] b = new byte[temp.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = Long.valueOf(temp[i], 2).byteValue();
		}
		return b;
	}

	/**********************************
	 * 十六进制字符串小写形式 begin
	 ***********************************/

	/**
	 * 方案一：直接利用BigInteger的方法，应该是最简单的方案了。
	 * <p>
	 * 使用BigInteger将字节数组转化为2进制字符串，BigInteger会省略前面的几个0
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexString1(byte[] bytes) {
		// 第一个参数的解释，记得一定要设置为1
		// signum of the number (-1 for negative, 0 for zero, 1 for positive).
		BigInteger bigInteger = new BigInteger(1, bytes);
		return bigInteger.toString(16);
	}

	/**
	 * 方案二：将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexString2(byte[] bytes) {
		StringBuffer sb = new StringBuffer(), tmp = new StringBuffer();
		for (byte b : bytes) {
			// 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
			tmp.append(Integer.toHexString(0xFF & b));
			// 每个字节8位，转为16进制表示，2个16进制位
			if (tmp.length() == 1) {
				tmp.insert(0, 0);
			}
			sb.append(tmp);
		}

		return sb.toString();
	}

	/**
	 * 方案三：分别取出字节的高四位与低四位然后分别得出10进制0-15这样的值，再利用一个字符串数组完美完成。对于转化的理解，当然最推荐第三种方式了。
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexString3(byte[] bytes) {
		final String hex = "0123456789abcdef";
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			// 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
			sb.append(hex.charAt((b >> 4) & 0x0f));
			// 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
			sb.append(hex.charAt(b & 0x0f));
		}

		return sb.toString();
	}

	/**
	 * 十六进制字符串转为字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexStr) {
		if (hexStr == null || hexStr.trim().isEmpty()) {
			return new byte[0];
		}
		int len = hexStr.length() / 2;
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++) {
			String subStr = hexStr.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}
		return bytes;
	}

	/**********************************
	 * 十六进制字符串小写形式 end
	 *************************************/

	/**********************************
	 * 十六进制字符串大写形式 begin
	 ***********************************/

	/**
	 * byte数组转换为十六进制的字符串
	 **/
	public static String bytesToUpperCaseHexString(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 0x10) {
				result.append("0");
			}
			result.append(Long.toString(bytes[i] & 0xff, 16));
		}
		return result.toString().toUpperCase();
	}

	/**
	 * 十六进制的字符串转换为byte数组
	 **/
	public static byte[] upperCaseHexStringToBytes(String upperHexStr) {
		char[] c = upperHexStr.toCharArray();
		byte[] b = new byte[c.length / 2];
		for (int i = 0; i < b.length; i++) {
			int pos = i * 2;
			b[i] = (byte) ("0123456789ABCDEF".indexOf(c[pos]) << 4 | "0123456789ABCDEF".indexOf(c[pos + 1]));
		}
		return b;
	}
	/**********************************
	 * 十六进制字符串大写形式 end
	 *************************************/
}
