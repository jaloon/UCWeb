package com.tipray.bean;

import java.io.Serializable;

/**
 * 消息
 * 
 * @author chenlong
 * @version 1.0 2017-10-10
 *
 */
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 信息内容 */
	private String msg;
	/** 操作数据ID */
	private String dataId;
	/** 异常信息 */
	private String e;

	public static Message success() {
		Message message = new Message();
		message.setMsg("success");
		return message;
	}

	public static Message success(String dataId) {
		Message message = new Message();
		message.setMsg("success");
		message.setDataId(dataId);
		return message;
	}

	public static Message error(String e) {
		Message message = new Message();
		message.setMsg("error");
		message.setE(e);
		return message;
	}

	public static Message error(Exception e) {
		Message message = new Message();
		message.setMsg("error");
		message.setE(e.getMessage());
		return message;
	}

	public Message() {
		super();
	}

	public Message(String msg) {
		this.msg = msg;
	}

	public Message(String msg, Object dataId) {
		super();
		this.msg = msg;
		this.dataId = dataId + "";
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getE() {
		return e;
	}

	public void setE(String e) {
		this.e = e;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Message{");
		sb.append("msg='").append(msg).append('\'');
		sb.append(", dataId='").append(dataId).append('\'');
		sb.append(", e='").append(e).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
