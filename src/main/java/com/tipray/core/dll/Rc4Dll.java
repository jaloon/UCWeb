package com.tipray.core.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.tipray.constant.SystemPropertyConst;

/**
 * 引入C++动态库rc4.dll
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface Rc4Dll extends Library {
	Rc4Dll INSTANCE_DLL = (Rc4Dll) Native.loadLibrary(SystemPropertyConst.RC4_DLL_PATH, Rc4Dll.class);

	void RC4(byte[] data, int dataLen, byte[] key, int keyLen);
}