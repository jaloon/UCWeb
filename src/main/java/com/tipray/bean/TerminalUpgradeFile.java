package com.tipray.bean;

import com.tipray.constant.TerminalUpgradeFileConst;

import java.io.Serializable;

/**
 * 车台升级文件
 * 
 * @author chenlong
 * @version 1.0 2018-04-09
 * @see TerminalUpgradeFileConst
 */
public class TerminalUpgradeFile implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 文件类型
	 * <ul>
	 * <li>1：BOOTLOADER</li>
	 * <li>2：内核</li>
	 * <li>3：内核+文件系统</li>
	 * <li>4：BOOTLOADER+内核+文件系统</li>
	 * <li>20：APP</li>
	 * <li>21：CONFIG</li>
	 * </ul>
	 */
	private Integer type;
	/** 文件名 */
	private String name;
	/** 文件大小 */
	private Integer size;
	/** 文件CRC32（4字节16进制字符串） */
	private String crc;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("TerminalUpgradeFile{");
		sb.append("type=").append(type);
		sb.append(", name='").append(name).append('\'');
		sb.append(", size=").append(size);
		sb.append(", crc='").append(crc).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
