package com.tipray.constant.reply;

/**
 * 登录错误枚举
 *
 * @author chenlong
 * @version 1.0 2018-05-21
 */
public enum LoginErrorEnum {
    /** 账号错误 */
    ACCOUNT_ERROR(1, "账号错误!"),
    /** 密码错误 */
    PASSWORD_ERROR(2, "密码错误！"),
    /** 登录失效或未登录 */
    LOGIN_INVALID(3, "登录失效或未登录！"),
    /** 登录超时 */
    LOGIN_TIME_OUT(4, "登录超时！"),
    /** 异地登录 */
    OFFSITE_LOGIN(5, "异地登录！"),
    /** 手机未认证 */
    MOBILE_NOT_CERTIFY(6, "手机未认证！"),
    /** UUID为空 */
    MOBILE_UUID_NULL(7, "UUID为空！"),
    /** 系统类型为空 */
    MOBILE_SYSTEM_NULL(8, "系统类型为空！"),
    /** 版本号为空 */
    APP_VERSION_NULL(9, "版本号为空！"),
    /** APP最低版本未配置或同步失败 */
    APP_VERSION_CONFIG_INVALID(10, "APP最低版本未配置或同步失败！"),
    /** APP版本太低 */
    APP_VERSION_OUTDATED(11, "APP版本太低！");

    private int code;
    private String msg;

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    LoginErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据code获取登录错误枚举
     *
     * @param code
     *            {@link int}
     * @return
     */
    public static LoginErrorEnum getByCode(int code) {
        for (LoginErrorEnum loginError : values()) {
            if (loginError.code == code) {
                return loginError;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return msg;
    }
}
