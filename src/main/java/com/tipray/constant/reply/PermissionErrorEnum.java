package com.tipray.constant.reply;

/**
 * 权限错误枚举
 * 
 * @author chenlong
 * @version 1.0 2018-04-17
 *
 */
public enum PermissionErrorEnum {
	/** 权限不足 */
	PERMISSION_DENIED(1, "权限不足！");

	private int code;
	private String msg;

	public int code() {
		return code;
	}

	public String msg() {
		return msg;
	}

	PermissionErrorEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	/**
	 * 根据code获取权限错误枚举
	 * 
	 * @param code
	 *            {@link int}
	 * @return
	 */
	public static PermissionErrorEnum getByCode(int code) {
		for (PermissionErrorEnum permissionError : values()) {
			if (permissionError.code == code) {
				return permissionError;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return msg;
	}
}
