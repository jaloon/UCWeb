package com.tipray.util;

import com.tipray.core.dll.UmfDll;

/**
 * IC卡读写器读卡工具类
 *
 * @author chenlong
 * @version 1.0 2018-05-14
 */
public class UMFUtil {
    private static final UmfDll UMF_DLL = UmfDll.INSTANCE_DLL;
    private static final byte[] CARD_HEX = new byte[16];

    /**
     * 初始化通讯口
     *
     * @param port {@link Integer} 取值为 0～99 时，表示串口 1～100；取值为 101~M(M 表示大于 101 的一个数)时，表示串口 101~M；
     *             为 100 时，表示 USB 口通讯，此时波特率无效。
     * @param baud {@link Long} 为通讯波特率（ 9600～115200），若设备为 USB 接口此参数无效，可设任意long 型值
     * @return {@link Integer} 成功则返回串口标识符值，失败返回 -1
     */
    public static int init(int port, long baud) {
        return UMF_DLL.fw_init(port, baud);
    }

    /**
     * 初始化 USB 口
     * @return {@link Integer} 成功则返回通讯设备标识符值，失败返回 -1
     */
    public static int initUSB() {
        return init(100, 0);
    }

    /**
     * 关闭端口
     * <p>
     * <b>注：</b>在 WIN32 环境下 icdev 为端口的设备句柄，必须释放后才可以再次连接。
     *
     * @param icdev {@link Integer} 通讯设备标识符（即init方法的返回值）
     * @return {@link Integer} 成功返回 0
     */
    public static int exit(int icdev) {
        return UMF_DLL.fw_exit(icdev);
    }



    public static int card(int icdev, int mode, long[] cardId) {
        int code = UMF_DLL.fw_card(icdev,mode,cardId);
        return code;
    }

    /**
     * 寻卡，能返回在工作区域内某张卡的序列号(16进制形式字符串)
     * @param icdev {@link Integer} 通讯设备标识符（即fw_init方法的返回值）
     * @param mode  {@link Integer} 寻卡模式（0 表示 IDLE 模式，一次只对一张卡操作；1 表示 ALL 模式，一次可对多张卡操作；）
     * @return {@link String} 卡的序列号(16进制形式字符串)
     * @throws IllegalStateException if result code is not 0.
     */
    public static String cardHex(int icdev, int mode) {
        int code = __cardHex(icdev, mode, CARD_HEX);
        if (code  == 0 ){
            return new String(CARD_HEX);
        } else {
            throw new IllegalStateException("寻卡失败！错误码：" + code);
        }

    }

    /**
     * 寻卡，能返回在工作区域内某张卡的序列号(16进制形式字符串)
     *
     * @param icdev {@link Integer} 通讯设备标识符（即fw_init方法的返回值）
     * @param mode  {@link Integer} 寻卡模式（0 表示 IDLE 模式， 一次只对一张卡操作；1 表示 ALL 模式， 一次可对多张卡操作；）
     * @param hexId {@link byte[]} 返回的表示 16 进制字符串卡号（ 8 个字节）的字节数组
     * @return {@link Integer} 成功返回 0
     */
    private static int __cardHex(int icdev, int mode, byte[] hexId) {
        return UMF_DLL.fw_card_hex(icdev, mode, hexId);
    }


    public static void main(String[] args) {
        int icdev = initUSB();
        byte[] pSnr = new byte[8];
       System.out.println(cardHex(icdev, 1));
        card(icdev,1,new long[8]);
        System.out.println(exit(icdev));
    }
}
