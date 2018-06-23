package com.tipray.bean.upgrade;

import java.io.Serializable;

/**
 * 车台升级信息
 *
 * @author chenlong
 * @version 1.0 2018-06-21
 */
public class TerminalUpgradeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 升级批次
     */
    private Long id;
    /**
     * 版本号（可为0）
     */
    private Integer ver;
    /**
     * 升级类型
     */
    private Byte type;
    /**
     * 升级文件地址
     */
    private String path;
    /**
     * 新版本信息流
     */
    private byte[] info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }
}
