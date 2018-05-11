package com.tipray.net;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tipray.bean.ResponseMsg;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.SpringBeanUtil;

/**
 * 超时任务
 * 
 * @author chenlong
 * @version 1.0 2018-04-08
 *
 */
public class TimeOutTask {
	private static final NioUdpServer UDP_SERVER = (NioUdpServer) SpringBeanUtil.getBean("udpServer");
	private ScheduledExecutorService service;
	private int count = 2;
	private ByteBuffer src;
	private int cacheId;
	private long timeout = 60L;

	/**
	 * 超时任务构造器
	 * 
	 * @param src
	 *            {@link ByteBuffer} 待发送数据
	 * @param cacheId
	 *            {@link Integer} 缓存ID
	 */
	public TimeOutTask(ByteBuffer src, int cacheId) {
		super();
		this.src = src;
		this.cacheId = cacheId;
	}

	/**
	 * 超时任务构造器
	 * 
	 * @param src
	 *            {@link ByteBuffer} 待发送数据
	 * @param cacheId
	 *            {@link Integer} 缓存ID
	 * @param timeout
	 *            {@link Long} 超时时长（秒）
	 */
	public TimeOutTask(ByteBuffer src, int cacheId, long timeout) {
		super();
		this.src = src;
		this.cacheId = cacheId;
		this.timeout = timeout;
	}

	/**
	 * 执行远程操作超时任务
	 */
	public void executeRemoteControlTask() {
		service = Executors.newSingleThreadScheduledExecutor();
		Runnable task = () -> {
			synchronized (AsynUdpCommCache.class) {
				ResponseMsg reply = AsynUdpCommCache.getResultCache(cacheId);
                if (reply != null) {
                    // 已经收到UDP应答，移除缓存
					AsynUdpCommCache.removeResultCache(cacheId);
					AsynUdpCommCache.removeTaskCache(cacheId);
                } else {
                    // 未收到应答
                    ResponseMsg msg = ResponseMsgUtil.error(ErrorTagConst.UDP_COMMUNICATION_ERROR_TAG, 180, "UDP应答超时！");
                    short bizId = (short) (cacheId >>> 16 ^ 128);
                    UdpReceiveResultHandler.handleReplyForRemoteControl(bizId, cacheId, msg);
                }
                // 关闭定时任务
                service.shutdown();
			}
		};
		// 参数：1、任务体 2、首次执行的延时时间 3、任务执行间隔 4、间隔时间单位
		service.scheduleAtFixedRate(task, timeout, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 执行信息下发（车台公共配置，配送卡等）超时任务
	 */
	public void executeInfoIssueTask() {
		service = Executors.newSingleThreadScheduledExecutor();
		Runnable task = () -> {
			synchronized (AsynUdpCommCache.class) {
				ResponseMsg reply = AsynUdpCommCache.getResultCache(cacheId);
				// 已经收到UDP应答
				if (reply != null) {
					AsynUdpCommCache.removeResultCache(cacheId);
					service.shutdown();
					return;
				}
				
				// UDP应答超时！
				if (count > 3) {
					service.shutdown();
					return;
				}
				
				// 单次超时重发
				UDP_SERVER.send(src);
				count++;
			}
		};
		// 参数：1、任务体 2、首次执行的延时时间 3、任务执行间隔 4、间隔时间单位
		service.scheduleAtFixedRate(task, timeout, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 执行轨迹重点监控重发任务
	 */
	public void executeFocusRepeatTask() {
		service = Executors.newSingleThreadScheduledExecutor();
		Runnable task = () -> {
			synchronized (AsynUdpCommCache.class) {
				ResponseMsg reply = AsynUdpCommCache.getResultCache(cacheId);
				// 已经收到UDP应答
				if (reply != null) {
					AsynUdpCommCache.removeResultCache(cacheId);
					service.shutdown();
					return;
				}
				
				ResponseMsg msg = ResponseMsgUtil.error(ErrorTagConst.UDP_COMMUNICATION_ERROR_TAG, 180, "UDP应答超时！");
				short bizId = (short) (cacheId >>> 16 ^ 128);
				UdpReceiveResultHandler.handleReplyForRemoteControl(bizId, cacheId, msg);
			}
		};
		// 参数：1、任务体 2、首次执行的延时时间 3、任务执行间隔 4、间隔时间单位
		service.scheduleAtFixedRate(task, timeout, timeout, TimeUnit.SECONDS);
	}
}
