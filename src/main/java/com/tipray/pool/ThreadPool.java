package com.tipray.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 * 
 * @author chenlong
 * @version 1.0 2018-04-03
 *
 */
public class ThreadPool {
	/** 可缓存线程池 */
	public static final ExecutorService CACHED_THREAD_POOL = Executors.newCachedThreadPool();

}
