package com.tipray.bean;

import com.tipray.core.base.BaseBean;

/**
 * 车辆公共参数版本
 * 
 * @author chenlong
 * @version 1.0 2018-01-15
 *
 */
public class VehicleParamVer extends BaseBean {
	private static final long serialVersionUID = 1L;
	/** 参数名称 */
	private String param;
	/** 参数版本 */
	private Long ver;

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public Long getVer() {
		return ver;
	}

	public void setVer(Long ver) {
		this.ver = ver;
	}

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("VehicleParamVer{");
        sb.append("param='").append(param).append('\'');
        sb.append(", ver=").append(ver);
        sb.append('}');
        return sb.toString();
    }
}
