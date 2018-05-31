package com.tipray.mq;

import com.tipray.util.BytesConverterByLittleEndian;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * 轨迹队列
 *
 * @author chenlong
 * @version 1.0 2018-05-29
 */
public class TrackQueue {
    private static final Logger logger = LoggerFactory.getLogger(TrackQueue.class);
    private final LinkedList<Integer> TERMINAL_IDS_QUEUE = new LinkedList<>();
    private final LinkedList<byte[]> RECEIVE_BUF_QUEUE = new LinkedList<>();
    private int size = 0;

    /**
     * 入队列
     *
     * @param element {@link TrackQueueElement} 轨迹队列元素
     * @return {@link boolean} 入队列是否成功
     */
    public synchronized boolean offer(TrackQueueElement element) {
        int terminalId = element.getTerminalId();
        byte[] receiveBuf = element.getReceiveBuf();
        return offer(terminalId, receiveBuf);
    }

    /**
     * 入队列
     *
     * @param receiveBuf {@link byte[]} 接收到的轨迹数据
     * @return {@link boolean} 入队列是否成功
     */
    public synchronized boolean offer(byte[] receiveBuf) {
        try {
            // 车台设备ID
            byte[] terminalIdDWord = Arrays.copyOfRange(receiveBuf, 1, 5);
            int terminalId = BytesConverterByLittleEndian.getInt(terminalIdDWord);
            int index = TERMINAL_IDS_QUEUE.indexOf(terminalId);
            if (index > -1) {
                TERMINAL_IDS_QUEUE.remove(index);
                RECEIVE_BUF_QUEUE.remove(index);
                size--;
            }
            TERMINAL_IDS_QUEUE.add(terminalId);
            RECEIVE_BUF_QUEUE.add(receiveBuf);
            size++;
            return true;
        } catch (Exception e) {
            logger.error("in track queue error: {}", e.toString());
            logger.debug("in track queue error stack info: ", e);
            return false;
        }
    }

    /**
     * 入队列
     *
     * @param terminalId {@link int} 车台设备ID
     * @param receiveBuf {@link byte[]} 接收到的轨迹数据
     * @return {@link boolean} 入队列是否成功
     */
    public synchronized boolean offer(int terminalId, byte[] receiveBuf) {
        try {
            int index = TERMINAL_IDS_QUEUE.indexOf(terminalId);
            if (index > -1) {
                TERMINAL_IDS_QUEUE.remove(index);
                RECEIVE_BUF_QUEUE.remove(index);
                size--;
            }
            TERMINAL_IDS_QUEUE.add(terminalId);
            RECEIVE_BUF_QUEUE.add(receiveBuf);
            size++;
            return true;
        } catch (Exception e) {
            logger.error("in track queue error: {}", e.toString());
            logger.debug("in track queue error stack info: ", e);
            return false;
        }
    }

    /**
     * 出队列
     *
     * @return {@link TrackQueueElement} 轨迹队列元素
     */
    public synchronized TrackQueueElement poll() {
        if (size == 0) {
            return null;
        }
        int terminalId = TERMINAL_IDS_QUEUE.poll();
        byte[] receiveBuf = RECEIVE_BUF_QUEUE.poll();
        if (receiveBuf != null) {
            size--;
        }
        return new TrackQueueElement(terminalId, receiveBuf);
    }

    /**
     * 出队列
     *
     * @return {@link byte[]} 接收到的轨迹数据
     */
    public synchronized byte[] pollTrack() {
        if (size == 0) {
            return null;
        }
        TERMINAL_IDS_QUEUE.poll();
        byte[] receiveBuf = RECEIVE_BUF_QUEUE.poll();
        if (receiveBuf != null) {
            size--;
        }
        return receiveBuf;
    }

    /**
     * 队列元素数量
     *
     * @return {@link int} 队列元素数量
     */
    public synchronized int size() {
        return size;
    }
}
