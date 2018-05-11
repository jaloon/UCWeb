package com.tipray.mq;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * 自定义队列元素
 * 
 * @author chenlong
 * @version 1.0 2018-03-20
 *
 */
public class MyQueueElement implements Serializable {
	private static final long serialVersionUID = 1L;
	/** UDP客户端地址 */
	private InetSocketAddress clientAddr;
	/** 接收到的客户端数据 */
	private byte[] receiveBuf;

	public InetSocketAddress getClientAddr() {
		return clientAddr;
	}

	public void setClientAddr(InetSocketAddress clientAddr) {
		this.clientAddr = clientAddr;
	}

	public byte[] getReceiveBuf() {
		return receiveBuf;
	}

	public void setReceiveBuf(byte[] receiveBuf) {
		this.receiveBuf = receiveBuf;
	}

	public MyQueueElement() {
		super();
	}

	public MyQueueElement(InetSocketAddress clientAddr, byte[] receiveBuf) {
		super();
		this.clientAddr = clientAddr;
		this.receiveBuf = receiveBuf;
	}
}
