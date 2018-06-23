package com.tipray.bean.upgrade;

import java.io.Serializable;

/**
 * 车台升级记录
 *
 * @author chenlong
 * @version 1.0 2018-06-21
 */
public class TerminalUpgradeRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 更新批次
     */
    private Long upgradeId;
    /**
     * 版本号
     */
    private Integer ver;
    /**
     * 是否已更新 {0：未更新 | 1：正在更新 | 2：已更新 }
     */
    private Integer hasUpgrade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Long getUpgradeId() {
        return upgradeId;
    }

    public void setUpgradeId(Long upgradeId) {
        this.upgradeId = upgradeId;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public Integer getHasUpgrade() {
        return hasUpgrade;
    }

    public void setHasUpgrade(Integer hasUpgrade) {
        this.hasUpgrade = hasUpgrade;
    }
}
