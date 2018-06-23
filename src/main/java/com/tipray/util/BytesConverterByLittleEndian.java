package com.tipray.util;

/**
 * Java基本类型与byte数组之间相互转换
 * <p>
 * <p>
 * Little-endian：将低序字节存储在起始地址（低位编址）<br>
 * 本文采用的是“小端顺序”的byte数组
 *
 * @author chenlong
 */
public class BytesConverterByLittleEndian {
    /**
     * char转换为byte[2]数组
     *
     * @param c 字符
     * @return byte[2]数组
     */
    public static byte[] getBytes(char c) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (c);
        bytes[1] = (byte) (c >> 8);
        return bytes;
    }

    /**
     * short转换为byte[2]数组
     *
     * @param s 短整型数
     * @return byte[2]数组
     */
    public static byte[] getBytes(short s) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (s & 0xff);
        bytes[1] = (byte) ((s & 0xff00) >> 8);
        return bytes;
    }

    /**
     * int转换为byte[4]数组
     *
     * @param i 整形数
     * @return byte[4]数组
     */
    public static byte[] getBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i & 0xff);
        bytes[1] = (byte) ((i & 0xff00) >> 8);
        bytes[2] = (byte) ((i & 0xff0000) >> 16);
        bytes[3] = (byte) ((i & 0xff000000) >> 24);
        return bytes;
    }

    /**
     * long转换为byte[8]数组
     *
     * @param l 长整型数
     * @return byte[8]数组
     */
    public static byte[] getBytes(long l) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (l & 0xff);
        bytes[1] = (byte) ((l >> 8) & 0xff);
        bytes[2] = (byte) ((l >> 16) & 0xff);
        bytes[3] = (byte) ((l >> 24) & 0xff);
        bytes[4] = (byte) ((l >> 32) & 0xff);
        bytes[5] = (byte) ((l >> 40) & 0xff);
        bytes[6] = (byte) ((l >> 48) & 0xff);
        bytes[7] = (byte) ((l >> 56) & 0xff);
        return bytes;
    }

    /**
     * float转换为byte[4]数组
     *
     * @param 浮点数
     * @return byte[4]数组
     */
    public static byte[] getBytes(float f) {
        int intBits = Float.floatToIntBits(f);
        return getBytes(intBits);
    }

    /**
     * double转换为byte[8]数组
     *
     * @param d 双精度数
     * @return byte[8]数组
     */
    public static byte[] getBytes(double d) {
        long intBits = Double.doubleToLongBits(d);
        return getBytes(intBits);
    }

    /**
     * byte[2]数组转换为char
     *
     * @param bytes byte[2]数组
     * @return 字符
     */
    public static char getChar(byte[] bytes) {
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    /**
     * byte[2]数组转换为short
     *
     * @param bytes byte[2]数组
     * @return 短整型数
     */
    public static short getShort(byte[] bytes) {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    /**
     * byte[4]数组转换为int
     *
     * @param bytes byte[4]数组
     * @return 整型数
     */
    public static int getInt(byte[] bytes) {
        return (0xff & bytes[0])
                | (0xff00 & (bytes[1] << 8))
                | (0xff0000 & (bytes[2] << 16))
                | (0xff000000 & (bytes[3] << 24));
    }

    /**
     * byte[8]数组转换为long
     *
     * @param bytes byte[8]数组
     * @return 长整型数
     */
    public static long getLong(byte[] bytes) {
        return (0xffL & (long) bytes[0])
                | (0xff00L & ((long) bytes[1] << 8))
                | (0xff0000L & ((long) bytes[2] << 16))
                | (0xff000000L & ((long) bytes[3] << 24))
                | (0xff00000000L & ((long) bytes[4] << 32))
                | (0xff0000000000L & ((long) bytes[5] << 40))
                | (0xff000000000000L & ((long) bytes[6] << 48))
                | (0xff00000000000000L & ((long) bytes[7] << 56));
    }

    /**
     * byte[4]数组转换为float
     *
     * @param bytes byte[4]数组
     * @return 浮点数
     */
    public static float getFloat(byte[] bytes) {
        return Float.intBitsToFloat(getInt(bytes));
    }

    /**
     * byte[8]数组转换为double
     *
     * @param bytes byte[8]数组
     * @return 双精度数
     */
    public static double getDouble(byte[] bytes) {
        return Double.longBitsToDouble(getLong(bytes));
    }

}
