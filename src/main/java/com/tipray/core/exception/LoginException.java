package com.tipray.core.exception;

/**
 * 登录的异常抛出
 * @author chends
 *
 */
public class LoginException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public LoginException() {
		super();
	}
	
	public LoginException(String message) {
		super(message);
    }
	
	public LoginException(Throwable cause) {
        super(cause);
    }
	
	public LoginException(String message, Throwable cause) {
		super(message, cause);
    }
}
