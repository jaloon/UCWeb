package com.tipray.test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tipray.cache.AsynUdpCommCache;
import com.tipray.util.BytesConverterByBigEndian;

public class SchedulTest {
//	static int i = 0;
	
	public static void main(String[] args) {
		long cacheId = AsynUdpCommCache.buildCacheId((short)0x1201, (short)1);
		int userId = (int) (cacheId >>> (4 * 8));
		byte [] b1 = {0, 0, 0, 1, 18, 1, 0, 1};
		byte [] b2 = BytesConverterByBigEndian.getBytes(cacheId);
		byte [] b3 = BytesConverterByBigEndian.getBytes(userId);
		System.out.println("cacheId: \t" + cacheId);
		System.out.println("userId: \t" + userId);
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		// 参数：1、任务体 2、首次执行的延时时间 3、任务执行间隔 4、间隔时间单位
//		service.scheduleAtFixedRate(()->{
//			if (i > 3) {
//				service.shutdown();
//				return;
//			}
//			System.out.println(i);
//			i++;
//			
//		}, 2, 2, TimeUnit.SECONDS);
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
		Runnable task = new Runnable() {
			private int i = 0;
			@Override
			public void run() {
				if (i > 3) {
					executor.remove(this);
					return;
				}
				System.out.println(i);
				i++;
				
			}
		};
		executor.scheduleAtFixedRate(task, 2, 2, TimeUnit.SECONDS);
		
		
		List< Integer> list = new CopyOnWriteArrayList<>();
		for (int i = 0; i < 5; i++) {
			list.add(i);
		}
		list.parallelStream().forEach(i->{
			if (i==1) {
				System.out.println("i==1");
				return;
			}
			System.out.println(i);
		});
	}
}
