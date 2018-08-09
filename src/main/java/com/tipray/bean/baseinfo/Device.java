/*
 * Device.java
 * Copyright(C) 厦门天锐科技有限公司
 * All rights reserved.
 * -----------------------------------------------
 * 2017-11-06 Created
 */
package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 设备信息表
 *
 * @author chenlong
 * @version 1.0 2017-11-06
 */
public class Device extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 设备类型值
     */
    private Integer type;
    /**
     * 设备类型
     */
    private String typeName;
    /**
     * 设备版本
     */
    private Integer ver;
    /**
     * 用户中心ID
     */
    private Integer centerId;
    /**
     * 用户中心名称
     */
    private String centerName;
    /**
     * 型号
     */
    private String model;
    /**
     * 出厂时间
     */
    private String produce;
    /**
     * 发货时间
     */
    private String delivery;

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public Integer getCenterId() {
        return centerId;
    }

    public void setCenterId(Integer centerId) {
        this.centerId = centerId;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduce() {
        return produce;
    }

    public void setProduce(String produce) {
        this.produce = produce;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (deviceId != null) {
            sb.append(", deviceId=").append(deviceId);
        }
        if (type != null) {
            sb.append(", type=").append(type);
        }
        if (typeName != null) {
            sb.append(", typeName='").append(typeName).append('\'');
        }
        if (ver != null) {
            sb.append(", ver=").append(ver);
        }
        if (centerId != null) {
            sb.append(", centerId=").append(centerId);
        }
        if (centerName != null) {
            sb.append(", centerName='").append(centerName).append('\'');
        }
        if (model != null) {
            sb.append(", model='").append(model).append('\'');
        }
        if (produce != null) {
            sb.append(", produce='").append(produce).append('\'');
        }
        if (delivery != null) {
            sb.append(", delivery='").append(delivery).append('\'');
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"Device{");
            sb.append('}');
        }
        return sb.toString();
    }
}