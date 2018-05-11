package com.tipray.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * UDP服务器测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class UDPServer {
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("UDPServer start...");
		DatagramSocket server = new DatagramSocket(39862);
		byte[] recvBuf = new byte[100];
		while (true) {
			DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
			server.receive(recvPacket);
			byte[] recv = Arrays.copyOf(recvPacket.getData(), recvPacket.getLength());
			// String recvStr = new String(recv, 0, recvPacket.getLength());
			System.out.println("Hello World!" + Arrays.toString(recv));
			Thread.sleep(60000);
			int port = recvPacket.getPort();
			InetAddress addr = recvPacket.getAddress();
			// String sendStr = "Hello ! I'm Server";
			byte[] sendBuf = { 1, 2, 3 };
			// sendBuf = sendStr.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
			server.send(sendPacket);
		}
		// server.close();
	}
}
