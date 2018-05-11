package com.tipray.test;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Rc4Dll
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface Rc4Dll extends Library {
	Rc4Dll INSTANCE_DLL = (Rc4Dll) Native.loadLibrary("dll/rc4", Rc4Dll.class);

	void rC4(byte[] data, int dataLen, byte[] key, int keyLen);
}
