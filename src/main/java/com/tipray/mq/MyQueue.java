package com.tipray.mq;

import com.tipray.net.UdpProtocol;
import com.tipray.pool.ThreadPool;
import com.tipray.util.BytesConverterByLittleEndian;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * 自定义消息队列
 *
 * @author chenlong
 * @version 1.0 2018-03-20
 */
public class MyQueue {
    private static final UdpProtocol UDP_PROTOCOL = new UdpProtocol();

    /**
     * 线程安全链表队列
     */
    private static final ConcurrentLinkedQueue<MyQueueElement> LINKED_QUEUE = new ConcurrentLinkedQueue<>();
    /**
     * 轨迹队列
     */
    private static final ConcurrentSkipListMap<Integer, byte[]> TRACK_QUEUE = new ConcurrentSkipListMap<>();

    /**
     * 出队列任务体
     */
    private static final Runnable OUT_QUEUE_TASK = () -> {
        synchronized (LINKED_QUEUE) {
            int len = LINKED_QUEUE.size();
            if (len == 0) {
                return;
            }
            for (int i = 0; i < len; i++) {
                MyQueueElement element = LINKED_QUEUE.poll();
                UDP_PROTOCOL.dealProtocolOfReceive(element);
            }
        }
    };

    /**
     * 轨迹队列出队任务体
     */
    private static final Runnable OUT_TRACK_QUEUE_TASK = () -> {
        synchronized (TRACK_QUEUE) {
            int len = TRACK_QUEUE.size();
            if (len == 0) {
                return;
            }
            for (int i = 0; i < len; i++) {
                UDP_PROTOCOL.dealProtocolForTrackUpload(TRACK_QUEUE.pollFirstEntry());
            }
        }
    };

    /**
     * 入队列
     *
     * @param element {@link MyQueueElement} 队列元素
     */
    public static void inQueue(MyQueueElement element) {
        LINKED_QUEUE.offer(element);
    }

    /**
     * 入队列
     *
     * @param clientAddr {@link InetSocketAddress} UDP客户端地址
     * @param receiveBuf {@link byte[]} 接收到的客户端数据
     */
    public static void inQueue(InetSocketAddress clientAddr, byte[] receiveBuf) {
        LINKED_QUEUE.offer(new MyQueueElement(clientAddr, receiveBuf));
    }

    /**
     * 入队列（轨迹队列）
     *
     * @param receiveBuf {@link byte[]} 接收到的轨迹数据
     */
    public static void inTrackQueue(byte[] receiveBuf) {
        // 车台设备ID
        byte[] terminalIdDWord = Arrays.copyOfRange(receiveBuf, 1, 5);
        int terminalId = BytesConverterByLittleEndian.getInt(terminalIdDWord);
        TRACK_QUEUE.put(terminalId, receiveBuf);
    }

    /**
     * 入队列（轨迹队列）
     *
     * @param terminalId {@link int} 车台设备ID
     * @param receiveBuf {@link byte[]} 接收到的轨迹数据
     */
    public static void inTrackQueue(int terminalId, byte[] receiveBuf) {
        TRACK_QUEUE.put(terminalId, receiveBuf);
    }

    /**
     * 出队列（定时任务）
     *
     * @param initialDelay {@link Long} the time to delay first execution 首次执行的延时时间
     * @param period       {@link Long} the period between successive executions 任务执行间隔
     * @return {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService outQueue(long initialDelay, long period) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 参数：1、任务体 2、首次执行的延时时间 3、任务执行间隔 4、间隔时间单位
        service.scheduleAtFixedRate(OUT_QUEUE_TASK, initialDelay, period, TimeUnit.SECONDS);
        return service;
    }

    /**
     * 队列任务（入队列，同时启用另一线程出队列）
     *
     * @param element {@link MyQueueElement} 队列元素
     */
    public static void queueTask(MyQueueElement element) {
        LINKED_QUEUE.offer(element);
        ThreadPool.CACHED_THREAD_POOL.execute(OUT_QUEUE_TASK);
    }

    /**
     * 队列任务（入队列，同时启用另一线程出队列）
     *
     * @param clientAddr {@link InetSocketAddress} UDP客户端地址
     * @param receiveBuf {@link byte[]} 接收到的客户端数据
     */
    public static void queueTask(InetSocketAddress clientAddr, byte[] receiveBuf) {
        queueTask(new MyQueueElement(clientAddr, receiveBuf));
    }

    /**
     * 队列任务（轨迹队列：入队列，同时启用另一线程出队列）
     *
     * @param receiveBuf {@link byte[]} 接收到的轨迹数据
     */
    public static void queueTaskForTrack(byte[] receiveBuf) {
        inTrackQueue(receiveBuf);
        ThreadPool.CACHED_THREAD_POOL.execute(OUT_TRACK_QUEUE_TASK);
    }

    /**
     * 队列任务（轨迹队列：入队列，同时启用另一线程出队列）
     *
     * @param terminalId {@link int} 车台设备ID
     * @param receiveBuf {@link byte[]} 接收到的轨迹数据
     */
    public static void queueTaskForTrack(int terminalId, byte[] receiveBuf) {
        inTrackQueue(terminalId, receiveBuf);
        ThreadPool.CACHED_THREAD_POOL.execute(OUT_TRACK_QUEUE_TASK);
    }

    private MyQueue() {
    }

}
