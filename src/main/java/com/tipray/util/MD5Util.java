package com.tipray.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MD5工具类
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class MD5Util {
	private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

	/**
	 * MD5 encoder
	 */
	public static String md5Encode(byte[] src) throws NoSuchAlgorithmException {
		StringBuilder sb = new StringBuilder();
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] dst = md.digest(src);

		for (int i = 0; i < dst.length; i++) {
			sb.append(Integer.toHexString((dst[i] >> 4) & 0xf));
			sb.append(Integer.toHexString(dst[i] & 0xf));
		}

		return sb.toString();
	}

	public static String md5Encode(String src) throws NoSuchAlgorithmException {
		if (src != null) {
			return md5Encode(src.getBytes());
		}
		return src;
	}

	/**
	 * 获取文件的MD5 encoder
	 */
	public static String md5Encode(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return md5Encode(is);
		} catch (FileNotFoundException e) {
			logger.error("md5Encode Exception:{}", e.toString());
			return null;
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * 根据输入流获得文件MD5
	 * 
	 * @param inputStream
	 * @return
	 */
	public static String md5Encode(InputStream inputStream) {
		try {
			if (inputStream instanceof FileInputStream) {
				return md5EncodeByNIO((FileInputStream) inputStream);
			}
			StringBuilder sb = new StringBuilder();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int numRead = 0;

			while ((numRead = inputStream.read(buffer)) > 0) {
				mdTemp.update(buffer, 0, numRead);
			}
			byte[] dst = mdTemp.digest();
			for (int i = 0; i < dst.length; i++) {
				sb.append(Integer.toHexString((dst[i] >> 4) & 0xf));
				sb.append(Integer.toHexString(dst[i] & 0xf));
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("md5Encode Exception:{}", e.toString());
			return null;
		}
	}

	public static String md5EncodeByNIO(FileInputStream is) {
		FileChannel isChannel = null;
		try {
			isChannel = is.getChannel();
			StringBuilder sb = new StringBuilder();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			ByteBuffer buf = ByteBuffer.allocate(1024);
			int numRead = 0;
			while ((numRead = isChannel.read(buf)) != -1) {
				buf.flip();
				mdTemp.update(buf.array(), 0, numRead);
				buf.clear();
			}
			byte[] dst = mdTemp.digest();
			for (int i = 0; i < dst.length; i++) {
				sb.append(Integer.toHexString((dst[i] >> 4) & 0xf));
				sb.append(Integer.toHexString(dst[i] & 0xf));
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("md5Encode Exception:{}", e.toString());
			return null;
		}
	}
}
