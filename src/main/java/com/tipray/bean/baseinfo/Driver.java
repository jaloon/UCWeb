package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 司机信息表
 *
 * @author chenlong
 * @version 1.0 2017-11-06
 */
public class Driver extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 姓名
     */
    private String name;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 身份证号
     */
    private String identityCard;
    /**
     * 联系地址
     */
    private String address;
    /**
     * 所属公司
     */
    private TransCompany transCompany;
    /**
     * 车牌号
     */
    private String carNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public TransCompany getTransCompany() {
        return transCompany;
    }

    public void setTransCompany(TransCompany transCompany) {
        this.transCompany = transCompany;
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
        if (name != null) {
            sb.append(", name='").append(name).append('\'');
        }
        if (phone != null) {
            sb.append(", phone='").append(phone).append('\'');
        }
        if (identityCard != null) {
            sb.append(", identityCard='").append(identityCard).append('\'');
        }
        if (address != null) {
            sb.append(", address='").append(address).append('\'');
        }
        if (transCompany != null) {
            sb.append(", transCompany=").append(transCompany);
        }
        if (carNumber != null) {
            sb.append(", carNumber='").append(carNumber).append('\'');
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"Driver{");
            sb.append('}');
        }
        return sb.toString();
    }
}
