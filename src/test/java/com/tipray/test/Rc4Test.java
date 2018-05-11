package com.tipray.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

import com.tipray.util.BytesUtil;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * rc4测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class Rc4Test {
	public static void main(String[] args) throws IOException {
		// Rc4Dll rc4Dll = Rc4Dll.instanceDll;
		// String a="sbdfjhbgdfjsghirtgi";
		// byte[] abt=a.getBytes();
		// String b="abcd";
		// String c=RC4Util.HloveyRC4(a, b);
		// byte[] cbt=c.getBytes();
		// System.out.println(RC4Util.HloveyRC4(a, b));
		// rc4Dll.RC4(abt, a.length(), b.getBytes(), b.length());
		// System.out.println(new String(abt));
		// rc4Dll.RC4(abt, a.length(), b.getBytes(), b.length());
		// System.out.println(new String(abt));
		byte[] b = { -91, 11, 35, 36, -19, 40, 77, 56, 106, -15, 94, 75, 9, 41, 77, 96, -62, 65, -23 };
		String str = new String(b, "ISO-8859-1");
		byte[] b0 = str.getBytes();
		// String str=new String(b,"ISO-8859-1");
		// byte[] b0=str.getBytes("ISO-8859-1");
		String str1 = "你好马墨白桂好地方给和给欧克垃圾斗";
		byte[] b1 = str1.getBytes("ISO-8859-1");
		String str2 = new String(b1, "UTF-8");
		byte[] b2 = str2.getBytes();

		String baseStr = Base64.encodeBase64String(b);
		byte[] baseB0 = Base64.decodeBase64(baseStr);
		// StandardCharsets
		BASE64Encoder base64Encoder = new BASE64Encoder();
		String baseStr1 = base64Encoder.encode(b);
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] baseB1 = base64Decoder.decodeBuffer(baseStr1);

		byte[] bb = { 0, 0, -128, 127, -91, 11, 35, 36, -19, 40, 77, 0, 56, 106, -15, 94, 75, 9, 41, -62, 65, -23, 0 };
		String byteHexString = BytesUtil.bytesToHexString1(bb);
		String byteHexString2 = BytesUtil.bytesToHexString2(bb);
		String byteHexString3 = BytesUtil.bytesToHexString3(bb);
		byte bb2 = -128;
		int unsignedByte = Byte.toUnsignedInt(bb2);
		String hexString = Integer.toHexString(unsignedByte);
	}

}
