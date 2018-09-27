package com.tipray.test;

import com.tipray.util.ArraysUtil;
import com.tipray.util.BytesConverterByBigEndian;
import com.tipray.util.BytesConverterByLittleEndian;
import com.tipray.util.BytesUtil;
import org.junit.Test;

import java.nio.ByteBuffer;

public class ProtocolTest {

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
