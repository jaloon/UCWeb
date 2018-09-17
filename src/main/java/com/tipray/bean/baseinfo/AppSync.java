package com.tipray.bean.baseinfo;

import java.io.Serializable;
import java.util.List;

/**
 * App设备和版本同步信息
 *
 * @author chenlong
 * @version 1.0 2018-08-02
 */
public class AppSync implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * APP版本信息
     */
    private List<AppVer> appvers;
    /**
     * APP归属信息
     */
    private List<CenterDev> centerDevs;

    public List<AppVer> getAppvers() {
        return appvers;
    }

    public void setAppvers(List<AppVer> appvers) {
        this.appvers = appvers;
    }

    public List<CenterDev> getCenterDevs() {
        return centerDevs;
    }

    public void setCenterDevs(List<CenterDev> centerDevs) {
        this.centerDevs = centerDevs;
    }
}
