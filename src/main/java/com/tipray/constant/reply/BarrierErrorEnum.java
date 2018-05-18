package com.tipray.constant.reply;

/**
 * 道闸接口车位枚举
 *
 * @author chenlong
 * @version 1.0 2018-05-17
 */
public enum BarrierErrorEnum {
    /** 油库不存在 */
    OILDEPOT_INVALID(1, "油库不存在！"),
    /** 配送卡不存在 */
    CARD_INVALID(2, "配送卡不存在！"),
    /** 配送卡未与车辆绑定 */
    CARD_UNBINDED(3, "配送卡未与车辆绑定！"),
    /** 进出闸标志无效 */
    SIGN_INVALID(4, "进出闸标志无效！"),
    /** 车载终端离线 */
    TERMINAL_OFFLINE(5, "车载终端离线！"),
    /** 系统内部通信错误 */
    UDP_COMM_ERROR(6, "系统内部通信错误！"),
    /** 请求超时 */
    TIME_OUT(7, "请求超时！"),
    /** 异常 */
    EXCEPTON(8, "异常！");

    private int code;
    private String msg;

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    BarrierErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据code获取设备绑定错误枚举
     *
     * @param code
     *            {@link int}
     * @return
     */
    public static BarrierErrorEnum getByCode(int code) {
        for (BarrierErrorEnum barrierError : values()) {
            if (barrierError.code == code) {
                return barrierError;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return msg;
    }
}
