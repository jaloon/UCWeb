package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * APP设备信息
 *
 * @author chenlong
 * @version 1.0 2018-07-26
 */
public class AppDev extends BaseBean {
    private static final long serialVersionUID = 1L;
    /** 手机唯一码 */
    private String uuid;
    /** 手机系统（Android/iOS） */
    private String system;
    /** 手机型号 */
    private String model;
    /** 当前WebApp版本 */
    private String currentVer;
    /** 机主 */
    private String owner;
    /** 联系电话 */
    private String phone;
    /** 职务 */
    private String duty;
    /** 机构 */
    private String institution;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCurrentVer() {
        return currentVer;
    }

    public void setCurrentVer(String currentVer) {
        this.currentVer = currentVer;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (uuid != null) {
            sb.append(", uuid='").append(uuid).append('\'');
        }
        if (system != null) {
            sb.append(", system='").append(system).append('\'');
        }
        if (model != null) {
            sb.append(", model='").append(model).append('\'');
        }
        if (currentVer != null) {
            sb.append(", currentVer='").append(currentVer).append('\'');
        }
        if (owner != null) {
            sb.append(", owner='").append(owner).append('\'');
        }
        if (phone != null) {
            sb.append(", phone='").append(phone).append('\'');
        }
        if (duty != null) {
            sb.append(", duty='").append(duty).append('\'');
        }
        if (phone != null) {
            sb.append(", institution='").append(institution).append('\'');
        }
        if (getCreateDate() != null) {
            sb.append(", createDate=").append(getCreateDate());
        }
        if (sb.length() > 0) {
            sb.replace(0, 2, "AppDev{");
            sb.append('}');
        }
        return sb.toString();
    }
}
