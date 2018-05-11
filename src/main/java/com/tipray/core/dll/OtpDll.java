package com.tipray.core.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.tipray.constant.SystemPropertyConst;

/**
 * 引入C++动态库libotp.dll
 * 
 * @author chenlong
 * @version 1.0 2018-02-24
 *
 */
public interface OtpDll extends Library {
	OtpDll INSTANCE_DLL = (OtpDll) Native.loadLibrary(SystemPropertyConst.OTP_DLL_PATH, OtpDll.class);

	int genPassword(long cardId, long key, long random, short pwdLen);
}
