package com.tipray.constant;

/**
 * HTTP状态码
 * 
 * @author chenlong
 * @version 1.0 2018-04-28
 * @see java.net.HttpURLConnection
 */
public final class HttpStatusConst {
	private HttpStatusConst() {
	}
	
	/* 2XX: generally "OK" */
	
	/**
	 * HTTP Status-Code 200: OK.
	 */
	public static final int HTTP_200_OK = 200;

	/**
	 * HTTP Status-Code 201: Created.
	 */
	public static final int HTTP__201_CREATED = 201;

	/**
	 * HTTP Status-Code 202: Accepted.
	 */
	public static final int HTTP_202_ACCEPTED = 202;

	/**
	 * HTTP Status-Code 203: Non-Authoritative Information.
	 */
	public static final int HTTP_203_NOT_AUTHORITATIVE = 203;

	/**
	 * HTTP Status-Code 204: No Content.
	 */
	public static final int HTTP_204_NO_CONTENT = 204;

	/**
	 * HTTP Status-Code 205: Reset Content.
	 */
	public static final int HTTP_205_RESET = 205;

	/**
	 * HTTP Status-Code 206: Partial Content.
	 */
	public static final int HTTP_206_PARTIAL = 206;

	/* 3XX: relocation/redirect */

	/**
	 * HTTP Status-Code 300: Multiple Choices.
	 */
	public static final int HTTP_300_MULT_CHOICE = 300;

	/**
	 * HTTP Status-Code 301: Moved Permanently.
	 */
	public static final int HTTP_301_MOVED_PERM = 301;

	/**
	 * HTTP Status-Code 302: Temporary Redirect.
	 */
	public static final int HTTP_302_MOVED_TEMP = 302;

	/**
	 * HTTP Status-Code 303: See Other.
	 */
	public static final int HTTP_303_SEE_OTHER = 303;

	/**
	 * HTTP Status-Code 304: Not Modified.
	 */
	public static final int HTTP_304_NOT_MODIFIED = 304;

	/**
	 * HTTP Status-Code 305: Use Proxy.
	 */
	public static final int HTTP_305_USE_PROXY = 305;

	/* 4XX: client error */

	/**
	 * HTTP Status-Code 400: Bad Request.
	 */
	public static final int HTTP_400_BAD_REQUEST = 400;

	/**
	 * HTTP Status-Code 401: Unauthorized.
	 */
	public static final int HTTP_401_UNAUTHORIZED = 401;

	/**
	 * HTTP Status-Code 402: Payment Required.
	 */
	public static final int HTTP_402_PAYMENT_REQUIRED = 402;

	/**
	 * HTTP Status-Code 403: Forbidden.
	 */
	public static final int HTTP_403_FORBIDDEN = 403;

	/**
	 * HTTP Status-Code 404: Not Found.
	 */
	public static final int HTTP_404_NOT_FOUND = 404;

	/**
	 * HTTP Status-Code 405: Method Not Allowed.
	 */
	public static final int HTTP_405_BAD_METHOD = 405;

	/**
	 * HTTP Status-Code 406: Not Acceptable.
	 */
	public static final int HTTP_406_NOT_ACCEPTABLE = 406;

	/**
	 * HTTP Status-Code 407: Proxy Authentication Required.
	 */
	public static final int HTTP_407_PROXY_AUTH = 407;

	/**
	 * HTTP Status-Code 408: Request Time-Out.
	 */
	public static final int HTTP_408_CLIENT_TIMEOUT = 408;

	/**
	 * HTTP Status-Code 409: Conflict.
	 */
	public static final int HTTP_409_CONFLICT = 409;

	/**
	 * HTTP Status-Code 410: Gone.
	 */
	public static final int HTTP_410_GONE = 410;

	/**
	 * HTTP Status-Code 411: Length Required.
	 */
	public static final int HTTP_411_LENGTH_REQUIRED = 411;

	/**
	 * HTTP Status-Code 412: Precondition Failed.
	 */
	public static final int HTTP_412_PRECON_FAILED = 412;

	/**
	 * HTTP Status-Code 413: Request Entity Too Large.
	 */
	public static final int HTTP_413_ENTITY_TOO_LARGE = 413;

	/**
	 * HTTP Status-Code 414: Request-URI Too Large.
	 */
	public static final int HTTP_414_REQ_TOO_LONG = 414;

	/**
	 * HTTP Status-Code 415: Unsupported Media Type.
	 */
	public static final int HTTP_415_UNSUPPORTED_TYPE = 415;

	/* 5XX: server error */

	/**
	 * HTTP Status-Code 500: Internal Server Error.
	 */
	public static final int HTTP_500_INTERNAL_ERROR = 500;

	/**
	 * HTTP Status-Code 501: Not Implemented.
	 */
	public static final int HTTP_501_NOT_IMPLEMENTED = 501;

	/**
	 * HTTP Status-Code 502: Bad Gateway.
	 */
	public static final int HTTP_502_BAD_GATEWAY = 502;

	/**
	 * HTTP Status-Code 503: Service Unavailable.
	 */
	public static final int HTTP_503_UNAVAILABLE = 503;

	/**
	 * HTTP Status-Code 504: Gateway Timeout.
	 */
	public static final int HTTP_504_GATEWAY_TIMEOUT = 504;

	/**
	 * HTTP Status-Code 505: HTTP Version Not Supported.
	 */
	public static final int HTTP_505_VERSION = 505;

	/* 6XX: login error（自定义） */

    /**
     * 登录失效或未登录
     */
    public static final int HTTP_601_LOGIN_INVALID = 601;
    /**
     * 登录超时
     */
	public static final int HTTP_602_LOGIN_TIME_OUT = 602;
    /**
     * 异地登录
     */
	public static final int HTTP_603_OFFSITE_LOGIN = 603;
}
