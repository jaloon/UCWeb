package com.tipray.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.tipray.constant.reply.PermissionErrorEnum;

/**
 * 权限异常抛出
 * 
 * @author chends
 *
 */
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "权限不足！")
public class PermissionException extends Exception {
	private static final long serialVersionUID = 1L;

	public PermissionException() {
		super();
	}

	public PermissionException(String message) {
		super(message);
	}

	public PermissionException(PermissionErrorEnum permissionError) {
		super(permissionError.msg());
	}

	public PermissionException(Throwable cause) {
		super(cause);
	}

	public PermissionException(String message, Throwable cause) {
		super(message, cause);
	}
}
