package com.tipray.cache;

import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列号缓存
 * <p>
 * 序列号2个字节，且不为0
 * </p>
 * 
 * @author chenlong
 * @version 1.0 2018-01-12
 *
 */
public final class SerialNumberCache {
	private static final Logger logger = LoggerFactory.getLogger(SerialNumberCache.class);
	private static final Map<Short, Short> SERIAL_NUMBER_MAP = new ConcurrentHashMap<>();
	private static final File CACHE_FILE = new File(FileUtil.getWebClassesPath() + "com/tipray/cache/serialNumber.cache");

	private SerialNumberCache() {
	}

	/**
	 * 根据UDP通信业务ID获取相关业务的当前序列号
	 * 
	 * @param udpBizId
	 *            {@link Short} UDP通信业务ID
	 * @return 相关业务的当前序列号
	 */
	public synchronized static short getSerialNumber(short udpBizId) {
		short serialNumber = 0;
		if (EmptyObjectUtil.isEmptyMap(SERIAL_NUMBER_MAP) && CACHE_FILE.exists()) {
			readObjFromFile();
		}
		if (SERIAL_NUMBER_MAP.containsKey(udpBizId)) {
			serialNumber = SERIAL_NUMBER_MAP.get(udpBizId);
		}
		return serialNumber;
	}

	/**
	 * 根据UDP通信业务ID获取相关业务的下一个序列号
	 * 
	 * @param udpBizId
	 *            {@link Short} UDP通信业务ID
	 * @return 相关业务的下一个序列号
	 */
	public synchronized static short getNextSerialNumber(short udpBizId) {
		short serialNumber = 1;
		if (EmptyObjectUtil.isEmptyMap(SERIAL_NUMBER_MAP) && CACHE_FILE.exists()) {
			readObjFromFile();
		}
		if (SERIAL_NUMBER_MAP.containsKey(udpBizId)) {
			serialNumber = getNextSerialNumberByCurrent(SERIAL_NUMBER_MAP.get(udpBizId));
		}
		SERIAL_NUMBER_MAP.put(udpBizId, serialNumber);
		writeObjToFile();
		return serialNumber;
	}

	/**
	 * 根据当前序列号的值获取下一个序列号
	 * 
	 * @param serialNumber
	 *            {@link short} 序列号的值
	 * @return 下一个序列号
	 */
	private static short getNextSerialNumberByCurrent(short serialNumber) {
		if (serialNumber == -1) {
			serialNumber = 1;
		} else {
			serialNumber = (short) (serialNumber + 1);
		}
		return serialNumber;
	}

	/**
	 * 向文件中写入对象
	 */
	private static void writeObjToFile() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))){
			if (!CACHE_FILE.exists()) {
				CACHE_FILE.createNewFile();
			}
			out.writeObject(SERIAL_NUMBER_MAP);
			out.flush();
		} catch (IOException e) {
			logger.error("向文件中写入对象异常！\n{}", e.toString());
		}
	}

	/**
	 * 从文件中读取对象
	 */
	@SuppressWarnings("unchecked")
	private static void readObjFromFile() {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(CACHE_FILE))) {
			Map<Short, Short> temp = (Map<Short, Short>) in.readObject();
			SERIAL_NUMBER_MAP.putAll(temp);
		} catch (Exception e) {
			logger.error("从文件中读取对象异常！\n{}", e.toString());
		}
	}
}
