package com.tipray.bean.upgrade;

import java.io.Serializable;

/**
 * 取消升级的车辆信息
 *
 * @author chenlong
 * @version 1.0 2018-06-22
 */
public class UpgradeCancelVehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 升级记录ID
     */
    private Long id;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 车台ID
     */
    private Integer terminalId;
    /**
     * 当前版本
     */
    private Integer ver;
    /**
     * 当前版本
     */
    private String verStr;
    /**
     * 待升级版本
     */
    private Integer upVer;
    /**
     * 待升级版本
     */
    private String upVerStr;
    /**
     * 升级类型
     */
    private Integer upType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Integer getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Integer terminalId) {
        this.terminalId = terminalId;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public String getVerStr() {
        return verStr;
    }

    public void setVerStr(String verStr) {
        this.verStr = verStr;
    }

    public Integer getUpVer() {
        return upVer;
    }

    public void setUpVer(Integer upVer) {
        this.upVer = upVer;
    }

    public String getUpVerStr() {
        return upVerStr;
    }

    public void setUpVerStr(String upVerStr) {
        this.upVerStr = upVerStr;
    }

    public Integer getUpType() {
        return upType;
    }

    public void setUpType(Integer upType) {
        this.upType = upType;
    }
}
