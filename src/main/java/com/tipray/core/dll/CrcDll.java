package com.tipray.core.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.tipray.constant.SystemPropertyConst;

/**
 * 引入C++动态库crc.dll
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
public interface CrcDll extends Library {
    CrcDll INSTANCE_DLL = (CrcDll) Native.loadLibrary(SystemPropertyConst.CRC_DLL_PATH, CrcDll.class);

    /**
     * 获取单字节CRC校验码
     *
     * @param data    {@link byte[]} 待校验字节数组
     * @param dataLen {@link Integer} 待校验的字节数
     * @return {@link Byte} CRC校验码
     */
    byte GetCRC(byte[] data, int dataLen);

    /**
     * 获取4字节CRC32校验码
     *
     * @param data    {@link byte[]} 待校验字节数组
     * @param dataLen {@link Integer} 待校验的字节数
     * @return {@link Integer} CRC32校验码
     */
    int GetCRC32(byte[] data, int dataLen);
}
