/*
 * Department.java
 * Copyright(C) 厦门天锐科技有限公司
 * All rights reserved.
 * -----------------------------------------------
 * 2016-01-13 Created
 */
package com.tipray.bean;

import com.tipray.core.base.BaseBean;

/**
 * 部门表
 * 
 * @author chends
 * @version 1.0 2016-01-13
 */
public class Department extends BaseBean {
	private static final long serialVersionUID = 1L;
	/** 部门名称 */
	private String name;
	/** 父部门 */
	private Department parent;
	/** 部门编号 */
	private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Department getParent() {
		return parent;
	}

	public void setParent(Department parent) {
		this.parent = parent;
	}

}