package com.tipray.websocket;

/**
 * WebSocket关闭状态码枚举
 * 
 * @author chenlong
 * @version 1.0 2018-03-02
 *
 */
public enum WebSocketCloseStatusEnum {
	/** 正常关闭; 无论为何目的而创建, 该链接都已成功完成任务。 */
	NORMAL(1000),
	/** 终端离开, 可能因为服务端错误, 也可能因为浏览器正从打开连接的页面跳转离开。 */
	GOING_AWAY(1001),
	/** 由于协议错误而中断连接。 */
	PROTOCOL_ERROR(1002),
	/** 由于接收到不允许的数据类型而断开连接 (如仅接收文本数据的终端接收到了二进制数据)。 */
	NOT_ACCEPTABLE(1003),
	/** 保留。 表示没有收到预期的状态码。 */
	NO_STATUS_CODE(1005),
	/** 保留。 用于期望收到状态码时连接非正常关闭 (也就是说, 没有发送关闭帧)。 */
	ABNORMAL(1006),
	/** 由于收到了格式不符的数据而断开连接 (如文本消息中包含了非 UTF-8 数据)。 */
	BAD_DATA(1007),
	/** 由于收到不符合约定的数据而断开连接。 这是一个通用状态码, 用于不适合使用 1003 和 1009 状态码的场景。 */
	POLICY_VIOLATION(1008),
	/** 由于收到过大的数据帧而断开连接。 */
	MESSAGE_TOO_BIG(1009),
	/** 客户端期望服务器商定一个或多个拓展, 但服务器没有处理, 因此客户端断开连接。 */
	MISSING_EXTENSION(1010),
	/** 客户端由于遇到没有预料的情况阻止其完成请求, 因此服务端断开连接。 */
	INTERNAL_ERROR(1011),
	/** 服务器由于重启而断开连接。 [Ref] */
	SERVICE_RESTARTED(1012),
	/** 服务器由于临时原因断开连接, 如服务器过载因此断开一部分客户端连接。 [Ref] */
	SERVICE_OVERLOAD(1013),
	/** 服务器充当网关或代理，并收到来自上游服务器的无效响应。这与502 HTTP状态码类似。 */
	BAD_GATEWAY(1014),
	/** 保留。 表示连接由于无法完成 TLS 握手而关闭 (例如无法验证服务器证书)。 */
	TLS_HANDSHAKE_FAILURE(1015),
	/**
	 * 一个用于Spring WebSocket框架内的状态码，表明一个会话变得不可靠（例如在发送消息时超时），
	 * 应该格外小心，例如避免在正常关机时向客户机发送更多的数据。
	 */
	SESSION_NOT_RELIABLE(4500);

	public static final int[] CLOSE_STATUS_CODES = { 1000, 1001, 1002, 1003, 1005, 1006, 1007, 1008, 1009, 1010, 1011,
			1012, 1013, 1014, 1015, 4500 };
	private int code;

	WebSocketCloseStatusEnum(int code) {
		this.code = code;
	}

	public int code() {
		return code;
	}

	/**
	 * 根据关闭状态码获取WebSocket关闭状态
	 * 
	 * @param code
	 *            {@link int} 关闭状态码
	 * @return {@link WebSocketCloseStatusEnum} WebSocket关闭状态
	 */
	public static WebSocketCloseStatusEnum getByCode(int code) {
		for (WebSocketCloseStatusEnum closeStatus : WebSocketCloseStatusEnum.values()) {
			if (closeStatus.code == code) {
				return closeStatus;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return new StringBuffer(super.toString()).append('(').append(code).append(')').toString();
	}

}
