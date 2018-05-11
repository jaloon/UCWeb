package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 车辆信息表
 *
 * @author chenlong
 * @version 1.1 2017-11-30
 */
public class Vehicle extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 仓数
     */
    private Integer storeNum;
    /**
     * SIM卡号
     */
    private String sim;
    /**
     * 配送卡
     */
    private TransportCard transportCard;
    /**
     * 车台
     */
    private Device vehicleDevice;
    /**
     * 所属公司
     */
    private TransCompany transCompany;
    /**
     * 车辆类型
     */
    private Integer type;
    /**
     * 车辆类型
     */
    private String typeName;

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Integer getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(Integer storeNum) {
        this.storeNum = storeNum;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public TransportCard getTransportCard() {
        return transportCard;
    }

    public void setTransportCard(TransportCard transportCard) {
        this.transportCard = transportCard;
    }

    public Device getVehicleDevice() {
        return vehicleDevice;
    }

    public void setVehicleDevice(Device vehicleDevice) {
        this.vehicleDevice = vehicleDevice;
    }

    public TransCompany getTransCompany() {
        return transCompany;
    }

    public void setTransCompany(TransCompany transCompany) {
        this.transCompany = transCompany;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (carNumber != null) {
            sb.append(", carNumber='").append(carNumber).append('\'');
        }
        if (storeNum != null) {
            sb.append(", storeNum=").append(storeNum);
        }
        if (sim != null) {
            sb.append(", sim='").append(sim).append('\'');
        }
        if (transportCard != null) {
            sb.append(", transportCard=").append(transportCard);
        }
        if (vehicleDevice != null) {
            sb.append(", vehicleDevice=").append(vehicleDevice);
        }
        if (transCompany != null) {
            sb.append(", transCompany=").append(transCompany);
        }
        if (type != null) {
            sb.append(", type=").append(type);
        }
        if (typeName != null) {
            sb.append(", typeName='").append(typeName).append('\'');
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"Vehicle{");
            sb.append('}');
        }
        return sb.toString();
    }
}
