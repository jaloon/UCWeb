package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 运输公司信息表
 *
 * @author chenlong
 * @version 1.0 2017-11-06
 */
public class TransCompany extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 公司名称
     */
    private String name;
    /**
     * 负责人
     */
    private String director;
    /**
     * 联系号码
     */
    private String phone;
    /**
     * 联系地址
     */
    private String address;
    /**
     * 上级公司
     */
    private TransCompany superior;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public TransCompany getSuperior() {
        return superior;
    }

    public void setSuperior(TransCompany superior) {
        this.superior = superior;
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
        if (director != null) {
            sb.append(", director='").append(director).append('\'');
        }
        if (phone != null) {
            sb.append(", phone='").append(phone).append('\'');
        }
        if (address != null) {
            sb.append(", address='").append(address).append('\'');
        }
        if (superior != null) {
            sb.append(", superior=").append(superior);
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"TransCompany{");
            sb.append('}');
        }
        return sb.toString();
    }
}
