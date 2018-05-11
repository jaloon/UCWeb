package com.tipray.bean;

import com.tipray.core.base.BaseBean;

/**
 * 配置
 * 
 * @author chenlong
 * @version 1.0 2017-10-10
 *
 */
public class Config extends BaseBean {
	private static final long serialVersionUID = 1L;
	/** 配置编码 */
	private String code;
	/** 配置值 */
	private String value;
	/** 备注 说明 */
	private String remark;

	public Config() {
		super();
	}

	public Config(String code, String value, String remark) {
		super();
		this.code = code;
		this.value = value;
		this.remark = remark;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getRemark() {
		return remark;
	}

	@Override
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
