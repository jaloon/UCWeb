package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 配送卡信息表
 *
 * @author chenlong
 * @version 1.1 2017-12-18
 */
public class TransportCard extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 配送卡ID
     */
    private Long transportCardId;
    /**
     * 车牌号
     */
    private String carNumber;

    public Long getTransportCardId() {
        return transportCardId;
    }

    public void setTransportCardId(Long transportCardId) {
        this.transportCardId = transportCardId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (transportCardId != null) {
            sb.append(", transportCardId=").append(transportCardId);
        }
        if (carNumber != null) {
            sb.append(", carNumber='").append(carNumber).append('\'');
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"TransportCard{");
            sb.append('}');
        }
        return sb.toString();
    }
}
