package com.tipray.test.mq;

import com.tipray.mq.MyQueueElement;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * 出队列线程
 *
 * @author chenlong
 * @version 1.0 2018-09-04
 */
public class OutQueueThreadTest {
    /**
     * 线程安全链表队列
     */
    private static final ConcurrentLinkedQueue<MyQueueElement> LINKED_QUEUE = new ConcurrentLinkedQueue<>();
    static boolean stop = false;

    static class OutQueueThread extends Thread {
        private boolean isPark = true;
        int count = 0;
        private InQueueThread inQueueThread;

        public void setInQueueThread(InQueueThread inQueueThread) {
            this.inQueueThread = inQueueThread;
        }

        @Override
        public void run() {
            while (!stop) {
                if (isPark) {
                    LockSupport.park();
                }
                count++;
                synchronized (LINKED_QUEUE) {
                    System.out.println("第" + count + "次出队！");
                    int len = LINKED_QUEUE.size();
                    if (len == 0) {
                        System.out.println("无元素出队！");
                        park();
                        continue;
                    }
                    for (int i = 0; i < len; i++) {
                        MyQueueElement element = LINKED_QUEUE.poll();
                        System.out.println(element.getClientAddr());
                    }
                    System.out.println("出队完成！");
                   park();
                }
            }

        }

        public void park() {
            isPark = true;
        }

        public void unPark() {
            isPark = false;
            LockSupport.unpark(this);
        }
    }

    static class InQueueThread extends Thread {
        int count = 0;
        private OutQueueThread outQueueThread;

        public void setOutQueueThread(OutQueueThread outQueueThread) {
            this.outQueueThread = outQueueThread;
        }

        public void unPark() {
            LockSupport.unpark(this);
        }

        @Override
        public void run() {
            while (count < 10) {
                count++;
                synchronized (LINKED_QUEUE) {
                    for (int i = 0; i < 4; i++) {
                        MyQueueElement element = new MyQueueElement();
                        element.setClientAddr(new InetSocketAddress("localhost", count));
                        LINKED_QUEUE.offer(element);
                    }
                }

                System.out.println("第" + count + "次入队！");
                outQueueThread.unPark();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        OutQueueThread outQueueThread = new OutQueueThread();
        InQueueThread inQueueThread = new InQueueThread();
        outQueueThread.setInQueueThread(inQueueThread);
        inQueueThread.setOutQueueThread(outQueueThread);
        outQueueThread.start();
        inQueueThread.start();
        // outQueueThread.unPark();
    }
}
