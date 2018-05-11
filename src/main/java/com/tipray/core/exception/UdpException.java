package com.tipray.core.exception;

import java.io.IOException;

/**
 * UDP通信异常
 * 
 * @author chenlong
 * @version 1.0 2018-02-05
 *
 */
public class UdpException extends IOException {
	private static final long serialVersionUID = 1L;

	public UdpException() {
		super();
	}

	public UdpException(String message) {
		super(message);
	}

	public UdpException(Throwable cause) {
		super(cause);
	}

	public UdpException(String message, Throwable cause) {
		super(message, cause);
	}
}
