package com.tipray.bean;

import java.io.Serializable;

/**
 * Response信息
 * 
 * @author chenlong
 * @version 1.0 2018-03-15
 *
 */
public class ResponseMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 信息标志（0 成功；1 错误：可预期，具体错误；2 异常：不可预期，捕获异常获取错误信息） */
	private int id;
	/** 结果（id=0，result=success；id=1||2，result=error） */
	private String result;
	/** 错误标志位 */
	private byte tag;
	/** 错误码（成功时为0，失败时大于0） */
	private int code;
	/** 信息 */
	private Object msg;

	public ResponseMsg() {}

	public ResponseMsg(int id, String result, byte errorTag, int errorId, Object msg) {
		super();
		this.id = id;
		this.result = result;
		this.tag = errorTag;
		this.code = errorId;
		this.msg = msg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public byte getTag() {
		return tag;
	}

	public void setTag(byte tag) {
		this.tag = tag;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("ResponseMsg{");
		sb.append("id=").append(id);
		sb.append(", result='").append(result).append('\'');
		sb.append(", tag=").append(tag);
		sb.append(", code=").append(code);
		sb.append(", msg=").append(msg);
		sb.append('}');
		return sb.toString();
	}

}
