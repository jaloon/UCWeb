package com.tipray.core.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.tipray.constant.SystemPropertyConst;

/**
 * IC卡读写
 * 引入C++动态库uml.dll
 *
 * @author chenlong
 * @version 1.0 2018-05-14
 */
public interface UmfDll extends Library {
    UmfDll INSTANCE_DLL = (UmfDll) Native.loadLibrary(SystemPropertyConst.UMF_DLL_PATH, UmfDll.class);

    /**
     * 初始化通讯口
     *
     * @param port {@link Integer} 取值为 0～99 时，表示串口 1～100；取值为 101~M(M 表示大于 101 的一个数)时，表示串口 101~M；
     *             为 100 时，表示 USB 口通讯，此时波特率无效。
     * @param baud {@link Long} 为通讯波特率（ 9600～115200），若设备为 USB 接口此参数无效，可设任意long 型值
     * @return {@link Integer} 成功则返回串口标识符值，失败返回 -1
     */
    int fw_init(int port, long baud);

    /**
     * 关闭端口
     *
     * <b>注：</b>在 WIN32 环境下 icdev 为端口的设备句柄，必须释放后才可以再次连接。
     *
     * @param icdev {@link Integer} 通讯设备标识符（即fw_init方法的返回值）
     * @return {@link Integer} 成功返回 0
     */
    int fw_exit(int icdev);

    /**
     * 寻卡，能返回在工作区域内某张卡的序列号(该函数包含了fw_request,fw_anticoll,fw_select的整体功能)
     *
     * @param icdev  {@link Integer} 通讯设备标识符（即fw_init方法的返回值）
     * @param mode   {@link Integer} 寻卡模式（0 表示 IDLE 模式， 一次只对一张卡操作；1 表示 ALL 模式， 一次可对多张卡操作；）
     * @param cardId {@link long} 返回的卡序列号（长整形变量）
     * @return {@link Integer} 成功返回 0
     */
    int fw_card(int icdev, int mode, long[] cardId);

    /**
     * 寻卡，能返回在工作区域内某张卡的序列号(16进制形式字符串)
     *
     * @param icdev {@link Integer} 通讯设备标识符（即fw_init方法的返回值）
     * @param mode  {@link Integer} 寻卡模式（0 表示 IDLE 模式， 一次只对一张卡操作；1 表示 ALL 模式， 一次可对多张卡操作；）
     * @param hexId {@link byte[]} 返回的表示 16 进制字符串卡号（ 8 个字节）的字节数组
     * @return {@link Integer} 成功返回 0
     */
    int fw_card_hex(int icdev, int mode, byte[] hexId);
}
