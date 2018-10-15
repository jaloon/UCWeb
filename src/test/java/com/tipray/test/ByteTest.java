package com.tipray.test;

import com.tipray.bean.VehicleTerminalConfig;
import com.tipray.bean.baseinfo.User;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.util.*;
import com.tipray.websocket.protocol.WebSocketProtocol;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 字节相关测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@SuppressWarnings({ "unused" })
public class ByteTest {

	@Test
	public void testByteBinary() {
	    byte b = -113;
        System.out.println(BytesUtil.byteToBinaryString(b));
        byte[] bytes = {64,64,-113,64,64};
        System.out.println(BytesUtil.bytesToBinaryString(bytes, ' '));
    }

	@Test
	public void appsync() throws IOException {
        String json = OkHttpUtil.get("https://www.pltone.com:3003/api/appSync.do?id=1");
	}

	@Test
    public void testJson() {
        String json = "{\"biz\":1,\"msg\":{\"a\":1,\"b\":\"abc\"}}";
        try {
            WebSocketProtocol protocol = JSONUtil.parseToObject(json, WebSocketProtocol.class);
            System.out.println(protocol.getBiz());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test01(){
        Set<Long> set = new CopyOnWriteArraySet<>();
        set.add(1L);
        set.add(2L);
        set.add(3L);
        set.add(4L);
        set.add(5L);
        set.add(1L);
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(6L);
        set.removeAll(list);
        System.out.println(Arrays.toString(set.toArray()));
    }

     @Test
     public void test0() {
         VehicleTerminalConfig config = new VehicleTerminalConfig();
         config.setScope(0);
         config.setScanInterval(10);
         config.setUploadInterval(30);
         config.setGenerateDistance(12);
         Integer scope = config.getScope();
         if (scope == null
                 || (scope == 1 && StringUtil.isEmpty(config.getCarNumber()))
                 || config.getScanInterval() == null
                 || config.getUploadInterval() == null
                 || config.getGenerateDistance() == null) {
             System.out.println("null");
         }
     }

	@Test
	public void test1() {
		System.out.println("\u5B9A\u4E49\u8F93\u51FA\u7EA7\u522B\u548C\u8F93\u51FA\u5E73\u53F0");
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("abcd");
		strBuf.insert(0, "1234");
		strBuf.deleteCharAt(strBuf.length() - 1);
		strBuf.append("5e");
		System.out.println(strBuf);
	}
	
	@Test
	public void test() {
		VehicleManageLog log = new VehicleManageLog();
		log.setId(1L);
		log.setType(1);
		User user = new User();
		user.setId(1L);
		user.setName("a");
		log.setUser(user);
		log = null;
		System.out.println(log);
		
//		short a = 0x1201, b = 0x1281;
//		System.out.println("a=" + a + ",\tb=" + b);
//		System.out.println(a^b);
//		System.out.println(a^128);
//		System.out.println(b^128);
	}
	
	@Test
	public void testHex1() {
		System.out.println(StandardCharsets.UTF_8.name());
//		Integer a = 128;
//		Long b = 128L;
//		boolean flag = a.equals(b.intValue());
//		System.out.println(flag);
//		if (flag) {
//			return;
//		}
//		int[] bs = {59, -75, 32, 97, -56, 92, 72, -28, 113, 29, -123, 31, 45, -8, 44, 121};
//		List<Integer> bs1 = new ArrayList<>();
//		for (int i : bs) {
//			bs1.add(i);
//		}
//		String json = JSONUtil.convertObjectToJson(bs1);
//		List<Integer> bs2 = JSONUtil.parseJsonToList(json, Integer.class);
//		System.out.println(BytesUtil.bytesToHex(bs, false));
	}
	@Test
	public void testHex2() {
		short s1 = 1, s2 = Short.MAX_VALUE;
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putShort(s1);
		buffer.putShort(s2);
		int i = BytesConverterByBigEndian.getInt(buffer.array(), 0);
		System.out.println(i);
	}
	@Test
	public void testHex() {
//		Long id1 = Long.parseLong("F5", 16);
//		Long id2 = Long.parseLong("F5",16);
//		System.out.println(id1 == id2);
		char c0 = '0';
		char c1 = '1';
		System.out.println((int)c0);
		System.out.println((int)c1);
		Random random =new Random();
		byte[] bytes = {0|0b00001000|0b00010000|0b00000100,2};
		bytes = new byte[100];
		random.nextBytes(bytes);
		for (int i = 0; i < 100; i++) {
			System.out.println(i+1+": ");
//			BytesUtil.bytesToBinaryString(bytes);
//			BytesUtil.bytesToBinaryString1(bytes);
			System.out.println();
		}
//		System.out.println(BytesUtil.bytesToBinaryString(bytes));
//		System.out.println(BytesUtil.bytesToBinaryString1(bytes));
	}
		@Test
		public void testByte() {
		byte b = (byte) 0x81;
		int i = 0x81;
		System.out.println(b);
		System.out.println(i);
		System.out.println(1 << 15 - 1);
		System.out.println(1 << 14);
		System.out.println((short) (1 << 15));
		System.out.println((1 << 15) - 1);
		System.out.println((short) (32767 + 1));
		System.out.println((short) (32767 + 1 + 32767 * 2));
		
	}

	@Test
	public void testBytesEquals() {
		byte[] b1 = { (byte) 1, (byte) 2 };
		byte[] b2 = { (byte) 1, (byte) 2 };
		System.out.println(b1 == b2);
		System.out.println(b1.equals(b2));
		List<Object> list1 = new ArrayList<Object>();
		List<Object> list2 = new ArrayList<Object>();
		list1.add(b1);
		list2.add(b2);
		System.out.println(list1.equals(list2));
		List<Integer> list3 = new ArrayList<>();
		list3.add(3);
		list3.add(5);
		list3.add(2);
		list3.add(1);
		System.out.println(list3.contains(2));
	}

	@Test
	public void testBinary() {
		int i = 128;
		String s = Integer.toBinaryString(i);
		System.out.println(s);
		byte b = (byte) 0b11111111;
		System.out.println(b);

		byte[] locks = { (byte) 0b11010001, (byte) 0b00001000, (byte) 0b00000000 };
		System.out.println(VehicleAlarmUtil.getLockAlarmInfo(locks));
		Map<Integer, String> alarmMap = VehicleAlarmUtil.getLockAlarmMap(locks);
		Map<Integer, String> lockMap = VehicleAlarmUtil.getLockMap(locks);
		byte[] empty = {};
		System.out.println(empty != null);
		System.out.println(0 == 0.0000);
	}

	@Test
	public void testByteBuffer() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(6);
		byteBuffer.put((byte) 3);

		// byteBuffer.position(0); //设置position到0位置，这样读数据时就从这个位置开始读
		// byteBuffer.limit(1); //设置limit为1，表示当前bytebuffer的有效数据长度是1

		byte bs = byteBuffer.get(5);
		System.out.println(byteBuffer);
	}

	@Test
	public void byteCode() {
		Integer id = 0x10000001;
		// id&0x10000000 > 0
		// id&(0x01 << 24) > 0
		// Byte idByte=id.byteValue();
		// id=idByte.intValue();
		Byte[] idCode = new Byte[4];
		String str = Integer.toHexString(id >> 24);

		Integer id1 = id >> 24;
		Byte b = id1.byteValue();
		System.out.println(b);
		idCode[0] = new Byte(str);
		idCode[1] = (byte) (id >> 16);
		idCode[2] = (byte) (id >> 8);
		idCode[3] = id.byteValue();
		// String idStr=new String(idCode);
		// id=new Integer(idStr);
		// System.out.println(id);
	}

	@Test
	public void md5() throws NoSuchAlgorithmException {
		String pwd = MD5Util.md5Encode("123456");
	}

	@Test
	public void floatGroup() {
		// points.add(new Point(118.105013, 24.438171));
		// points.add(new Point(118.106199, 24.438303));
		// points.add(new Point(118.107888, 24.439125));
		// points.add(new Point(118.111445, 24.437678));
		// points.add(new Point(118.114356, 24.436658));
		// points.add(new Point(118.117446, 24.435572));
		// points.add(new Point(118.120895, 24.434914));
		// points.add(new Point(118.124776, 24.434848));
		// points.add(new Point(118.128621, 24.432874));
		// points.add(new Point(118.130992, 24.4309));
		// points.add(new Point(118.133615, 24.429946));
		// points.add(new Point(118.13728, 24.430012));
		// points.add(new Point(118.140838, 24.428729));
		// points.add(new Point(118.143676, 24.428894));
		// points.add(new Point(118.145617, 24.43067));
		// points.add(new Point(118.147305, 24.435144));
		// points.add(new Point(118.149677, 24.437579));
		// points.add(new Point(118.155318, 24.438994));
		// points.add(new Point(118.158049, 24.44156));
		// points.add(new Point(118.161391, 24.444783));
		// points.add(new Point(118.164697, 24.446395));
		// points.add(new Point(118.164697, 24.446395));
		// points.add(new Point(118.167032, 24.448237));
		// points.add(new Point(118.167032, 24.448237));
		Float x1 = 118.105013f;
		float y1 = 24.438171f;
		float x2 = 118.106199f;
		float y2 = 24.438303f;
		float x3 = 118.107888f;
		float y3 = 24.439125f;
		float x4 = 118.111445f;
		float y4 = 24.437678f;
		float x5 = 118.114356f;
		float y5 = 24.436658f;

		Integer i = 1568;

		String s = i.toString();
		String bs = Integer.toBinaryString(i);// 110 0010 0000
		String hs = Integer.toHexString(i);

		byte[] hsb = ("00000" + hs).getBytes();// [48, 48, 48, 48, 48, 54, 50, 48]

		byte[] ib1 = BytesConverterByBigEndian.getBytes(i);// [0, 0, 6, 32]
		byte[] ib2 = BytesConverterByLittleEndian.getBytes(i);// [32, 6, 0, 0]

		byte bx1 = x1.byteValue();
		System.out.println(bx1);
		String str = x1 + "";
		String str2 = y1 + "";
		String hexStr = Double.toHexString(x1);
		// 118.10501
		// [49, 49, 56, 46, 49, 48, 53, 48, 49]
		byte[] a1 = hexStr.getBytes();
		byte[] a2 = str.getBytes();
		float b1 = Float.valueOf(new String(a1));
		float b2 = Float.valueOf(new String(a2));

	}
}
