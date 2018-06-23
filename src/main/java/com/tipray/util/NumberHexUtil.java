package com.tipray.util;

/**
 * 16进制数转换工具类
 *
 * @author chenlong
 * @version 1.0 2018-06-22
 */
public class NumberHexUtil {

    /**
     * 1字节转为2个16进制字符
     *
     * @param b byte 1字节
     * @return 2个16进制字符
     */
    public static String byteToHex(byte b) {
        return String.format("%02x", b);
    }

    /**
     * 2字节短整形数转为4个16进制字符
     *
     * @param s short 2字节
     * @return 4个16进制字符
     */
    public static String shortToHex(short s) {
        return String.format("%04x", s);
    }

    /**
     * 4字节整形数转为8个16进制字符
     *
     * @param i int 4字节
     * @return 8个16进制字符
     */
    public static String intToHex(int i) {
        return String.format("%08x", i);
    }

    /**
     * 8字节长整型数转为16个16进制字符
     *
     * @param l long 8字节
     * @return 16个16进制字符
     */
    public static String longToHex(long l) {
        return String.format("%016x", l);
    }
}
