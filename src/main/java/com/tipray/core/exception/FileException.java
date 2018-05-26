package com.tipray.core.exception;

/**
 * 文件操作异常
 * 
 * @author chenlong
 * @version 1.0 2018-02-01
 *
 */
public class FileException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public static final String PATH_UNSPECIFIED_EXCEPTION = "文件路径未指定";
	public static final String FILE_NOT_FOUND_EXCEPTION = "文件不存在";
	public static final String FILE_NAME_NULL_EXCEPTION = "文件名为空";
	public static final String FILE_DOWNLOAD_EXCEPTION = "文件名为空";
	public static final String BAD_FILE_FORMAT_EXCEPTION = "Excel文件格式不正确";

	public FileException() {
		super();
	}

	public FileException(String message) {
		super(message);
	}

	public FileException(Throwable cause) {
		super(cause);
	}

	public FileException(String message, Throwable cause) {
		super(message, cause);
	}
}
