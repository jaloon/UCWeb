package com.tipray.core.exception;

/**
 * 参数校验异常
 *
 * @author chenlong
 * @version 1.0 2018-12-14
 */
public class ArgsCheckException extends RuntimeException {
    private static final long serialVersionUID = 8290370439200672363L;

    private int code = 99;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        if (code > 0) {
            this.code = code;
        }
    }

    public ArgsCheckException() {
        super();
    }

    public ArgsCheckException(String message) {
        super(message);
    }

    public ArgsCheckException(Throwable cause) {
        super(cause);
    }

    public ArgsCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgsCheckException(String message, int code) {
        super(message);
        setCode(code);
    }
}
