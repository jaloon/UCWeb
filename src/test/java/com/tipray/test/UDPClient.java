package com.tipray.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP客户端测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class UDPClient {
	public static void main(String[] args) throws Exception {
		System.out.println("UDPClient start...");
		DatagramSocket client = new DatagramSocket();

		String sendStr = "Hello!";
		// byte[] sendBuf;
		byte[] sendBuf = new byte[4];
		// sendBuf = sendStr.getBytes();
		sendBuf[0] = 0;
		sendBuf[1] = 1;
		sendBuf[2] = 2;
		sendBuf[3] = 3;

		byte[] addre = "192.138.3.225".getBytes();

		// InetAddress addr = InetAddress.getByName("127.0.0.1");
		InetAddress addr = InetAddress.getByName("192.168.3.242");
		// InetAddress addr = InetAddress.getByName("192.168.3.155");
		// int port = 15050;
		int port = 7001;
		DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
		client.send(sendPacket);

		byte[] recvBuf = new byte[100];
		DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
		client.receive(recvPacket);
		byte[] recv = recvPacket.getData();
		String recvStr = new String(recv, 0, recvPacket.getLength());
		System.out.println("收到:" + recvStr);
		client.close();
	}
}
