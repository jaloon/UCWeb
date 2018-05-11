package com.tipray.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tipray.net.UdpClient;
import com.tipray.net.UdpProtocol;
import com.tipray.net.constant.UdpBizId;
import com.tipray.util.CRCUtil;
import com.tipray.util.DateUtil;
import com.tipray.util.FileUtil;
import com.tipray.util.JSONUtil;
import com.tipray.util.RC4Util;

public class UdpTest {
	@Test
	public void testUdpProtocol() throws Exception {
		int carId = 1;
		UdpProtocol udpProtocol = new UdpProtocol(carId, UdpBizId.REMOTE_ALARM_ELIMINATE_REQUEST, (short) 1, null);
		System.out.println(udpProtocol.getRandom());
		int interval = 3;
		Date date = DateUtil.convertDateStrToDate("2018-01-09 20:30:00", "yyyy-MM-dd hh:mm:ss");
		long timeStamp = date.getTime();
		long duration = timeStamp - System.currentTimeMillis();
		int durationMinutes = (int) (duration / 60000);
		Map<String, Integer> map = new HashMap<>();
		map.put("interval", interval);
		map.put("duration", durationMinutes);
		String json = JSONUtil.stringify(map);
		byte[] data = json.getBytes();
		byte[] key = RC4Util.createBinaryKey();// [101, 123, 89, 102, 82, -63, -10, -19]
		byte[] encryptData = RC4Util.rc4(data, key);
		byte[] crcData = CRCUtil.addCrcToBytes(encryptData);
		System.out.println(Arrays.toString(crcData));
		boolean crcCheck = CRCUtil.checkCrc(crcData);
		if (crcCheck) {
			byte[] encryptData1 = Arrays.copyOf(crcData, crcData.length - 1);
			byte[] decryptData = RC4Util.rc4(encryptData1, key);
			String decryptJson = new String(decryptData);
			System.out.println(decryptJson);
		} else {
			System.err.println("error");
		}
	}

	@Test
	public void testUdpTimeout() throws IOException {
		byte[] sendBuf = { 4, 5, 6 };
		UdpClient client = new UdpClient(sendBuf);
		client.send();
		byte[] b = client.receive();
		// byte[] b=client.sendAndReceive();
		if (b != null) {
			System.out.println(Arrays.toString(b));
		} else {
			System.out.println("null");
		}
	}

	
	
	@Test
	public void testSerialNo() {
		System.out.println(FileUtil.getWebClassesPath());
		System.out.println(FileUtil.getWebInfPath());
		System.out.println(FileUtil.getWebRoot());
	}
}
