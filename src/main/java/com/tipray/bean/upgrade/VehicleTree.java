package com.tipray.bean.upgrade;

import java.io.Serializable;

/**
 * 车辆树
 *
 * @author chenlong
 * @version 1.0 2018-06-22
 */
public class VehicleTree implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private Long pId;
    private Integer ver;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }
}
