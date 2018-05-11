package com.tipray.net;

/**
 * UDP工具类
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class UdpUtil {
	public static int getDWord(byte[] buf) {
		long ret = 0;
		ret += (buf[0] & 0xff) << 24;
		ret += (buf[1] & 0xff) << 16;
		ret += (buf[2] & 0xff) << 8;
		ret += (buf[3] & 0xff);

		return (int) ret;
	}

	public static short getWord(byte[] buf) {
		long ret = 0;
		ret += (buf[0] & 0xff) << 8;
		ret += (buf[1] & 0xff);

		return (short) ret;
	}

	public static byte[] getBytes(int value) {
		byte[] buf = new byte[4];
		buf[0] = (byte) (value / 256 / 256 / 256);
		buf[1] = (byte) (value / 256 / 256);
		buf[2] = (byte) (value / 256);
		buf[3] = (byte) (value % 256);
		return buf;
	}

	public static byte[] getBytes(short value) {
		byte[] buf = new byte[2];
		buf[0] = (byte) (value / 256);
		buf[1] = (byte) (value % 256);
		return buf;
	}
}
