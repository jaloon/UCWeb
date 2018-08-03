package com.tipray.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.tipray.constant.CenterConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tipray.mq.MyQueue;

/**
 * 阻塞式UDP服务端
 * 
 * @author chenlong
 * @version 1.0 2018-03-20
 *
 */
public class UdpServer extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);
	// 缓冲数组的大小
	public static final int BUFFER_SIZE = 1024;
	private static final int SERVER_PORT = CenterConst.UDP_LOCAL_PORT;
	private DatagramSocket serverSocket = null;
	private DatagramPacket receivePacket = null;
	private boolean enabled = true;
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public boolean getEnabled(){
		return enabled;
	}

	public void init() {
		byte inBuf[] = new byte[BUFFER_SIZE]; // 接收数据的缓冲数组
		try {
			serverSocket = new DatagramSocket(SERVER_PORT);
			receivePacket = new DatagramPacket(inBuf, BUFFER_SIZE);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void run() {
		while (enabled) {
			if (null == serverSocket) {
				init();
			} else {
				try {
					serverSocket.receive(receivePacket);
					byte[] data = receivePacket.getData();
					int len = receivePacket.getLength();
					byte[] receiveBuf = Arrays.copyOf(data, len);
					InetSocketAddress clientAddr = (InetSocketAddress) receivePacket.getSocketAddress();
					MyQueue.inQueue(clientAddr, receiveBuf);
				} catch (Exception e) {
					logger.error(e.getMessage());
					close();
				}
			}
		}
	}

	public void close() {
		if (null != serverSocket) {
			serverSocket.close();
			serverSocket = null;
		}
	}

}
