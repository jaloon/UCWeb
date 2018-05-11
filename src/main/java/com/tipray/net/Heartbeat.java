package com.tipray.net;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 心跳
 * 
 * @author chenlong
 * @version 1.0 2018-04-22
 *
 */
public class Heartbeat {
	/** 最后心跳时间 */
	private AtomicLong lastHeartbeat = new AtomicLong();
	/** 是否UDP通信异常报警 */
	private boolean alarm;

	/**
	 * 获取最后心跳时间
	 * @return {@link Long} 最后心跳时间毫秒数 
	 */
	public synchronized long getLastHeartbeat() {
		return lastHeartbeat.get();
	}

	/**
	 * 更新心跳时间
	 * @param lastHeartbeat {@link Long} 要更新的最后心跳时间毫秒数
	 * @return {@link Long} 更新前的最后心跳时间毫秒数
	 */
	public synchronized long updateHeartbeat(long lastHeartbeat) {
		return this.lastHeartbeat.getAndSet(lastHeartbeat);
	}

	/**
	 * 是否UDP通信异常报警
	 * @return {@link Boolean}
	 */
	public synchronized boolean isAlarm() {
		return alarm;
	}

	/**
	 * 设置UDP通信异常报警
	 * @param alarm {@link Boolean}
	 */
	public synchronized void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}
}
