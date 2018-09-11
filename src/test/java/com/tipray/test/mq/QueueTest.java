package com.tipray.test.mq;

import java.util.concurrent.locks.LockSupport;

/**
 * @author chenlong
 * @version 1.0 2018-09-04
 */
public class QueueTest {
    public static void main(String[] args) {

        MyThread mt = new MyThread();

        mt.start();

        try {

            Thread.currentThread().sleep(10);

        } catch(InterruptedException e) {

            e.printStackTrace();

        }

        mt.park();

        System.out.println("canyou get here?");

        try {

            Thread.currentThread().sleep(3000);

        } catch(InterruptedException e) {

            e.printStackTrace();

        }

        mt.unPark();

    }



    static class MyThread extends Thread {

        private boolean isPark=false;

        public void run() {

            while(true) {

                if(isPark)

                    LockSupport.park();

                System.out.println("running....");

            }

        }

        public void park(){

            isPark=true;

        }

        public void unPark(){

            isPark=false;

            LockSupport.unpark(this);

        }

    }

}
