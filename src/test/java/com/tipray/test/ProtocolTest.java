package com.tipray.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.tipray.core.exception.UdpException;
import com.tipray.net.UdpClient;
import com.tipray.util.ArraysUtil;
import com.tipray.util.BytesConverterByBigEndian;
import com.tipray.util.BytesConverterByLittleEndian;
import com.tipray.util.BytesUtil;

public class ProtocolTest {
	@Test
	public void testCarBind() {
		int terminalId  = 16777217; //车载终端设备ID
		String carNumber  = "桂A12345"; // 车牌号
		byte storeNum  = 4; // 仓数
		boolean reply;
		try {
			reply = UdpClient.udpForCarBind(terminalId, carNumber, storeNum);
			System.out.println(reply);
		} catch (UdpException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testByteBuffer() {
		byte[] bs = {59, -75, 32, 97, -56, 92, 72, -28, 113, 29, -123, 31, 45, -8, 44, 121};
		ByteBuffer buffer = ByteBuffer.allocate(18);
		buffer.put(bs);
		byte[] b2 = {12,45};
		ByteBuffer buffer2 = ByteBuffer.allocate(2);
		buffer2.put(b2);
		buffer.put(buffer2);
	}
	
	@Test
	public void testUdpBizId() {
		byte[] replyBizIdBytes = {(byte) 132, 18};
		short replyBizId = BytesConverterByLittleEndian.getShort(replyBizIdBytes);
		String replyBizIdHex = BytesUtil.bytesToHex(ArraysUtil.reverse(replyBizIdBytes), false);
		System.out.println(replyBizIdHex);
		short bizId = 0x1204;
		int result = bizId^replyBizId;
		System.out.println(result);
		byte[] bizBytes = BytesConverterByBigEndian.getBytes(bizId);
		StringBuffer bizBytesHexBuf = new StringBuffer(BytesUtil.bytesToHex(bizBytes, false));
		bizBytesHexBuf.setCharAt(2, '8');
	}
	

}
