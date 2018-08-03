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
     * APP设备信息
     */
    private List<AppDev> appdevs;

    public List<AppVer> getAppvers() {
        return appvers;
    }

    public void setAppvers(List<AppVer> appvers) {
        this.appvers = appvers;
    }

    public List<AppDev> getAppdevs() {
        return appdevs;
    }

    public void setAppdevs(List<AppDev> appdevs) {
        this.appdevs = appdevs;
    }
}
