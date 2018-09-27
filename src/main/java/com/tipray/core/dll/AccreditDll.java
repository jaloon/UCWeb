package com.tipray.core.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.tipray.constant.SystemPropertyConst;

/**
 * 引入C++动态库accredit.dll
 *
 * @author chenlong
 * @version 1.0 2018-06-22
 */
public interface AccreditDll extends Library {
    AccreditDll INSTANCE_DLL = (AccreditDll) Native.loadLibrary(SystemPropertyConst.ACCREDIT_DLL_PATH, AccreditDll.class);
    // 通过授权Key获取授权密码
    // v_iByteLen v_iPwdLen 必须等于6
    // 授权密钥生成成功的话接口会返回true

    /**
     * @param data    {@link byte[]}
     * @param dataLen {@link Integer}
     * @param pwd     {@link byte[]}
     * @param pwdLen  {@link Integer}
     * @return {@link Boolean}
     */
    boolean GetAccreditPassword(byte[] data, int dataLen, byte[] pwd, int pwdLen);
}
