package com.tipray.core.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.tipray.constant.SystemPropertyConst;

/**
 * 引入C++动态库crc.dll
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface CrcDll extends Library {
	CrcDll INSTANCE_DLL = (CrcDll) Native.loadLibrary(SystemPropertyConst.CRC_DLL_PATH, CrcDll.class);

	byte GetCRC(byte[] data, int dataLen);
}
