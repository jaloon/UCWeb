package com.tipray.service;

import com.tipray.bean.baseinfo.AppDev;
import com.tipray.bean.baseinfo.AppSync;
import com.tipray.bean.baseinfo.AppVer;
import com.tipray.core.exception.ServiceException;

public interface AppService {
    /**
     * 新增APP设备信息
     *
     * @param appDev APP设备信息
     * @throws ServiceException
     */
    AppDev addAppdev(AppDev appDev) throws ServiceException;

    /**
     * 修改APP设备信息
     *
     * @param appDev APP设备信息
     * @throws ServiceException
     */
    AppDev updateAppdev(AppDev appDev) throws ServiceException;

    /**
     * 根据手机UUID更新手机型号和当前APP版本号
     *
     * @param model      手机型号
     * @param currentVer 当前APP版本号
     * @param uuid       手机UUID
     * @throws ServiceException
     */
    void updateModelAndCurrentVerByUuid(String model, String currentVer, String uuid) throws ServiceException;

    /**
     * 根据Id删除APP设备信息
     *
     * @param id APP设备信息ID
     */
    void deleteAppdevById(Long id) throws ServiceException;

    /**
     * UUID是否已存在
     *
     * @param uuid
     * @return
     */
    boolean isAppdevExist(String uuid);

    /**
     * 新增APP版本信息
     * @param appVer APP版本信息
     * @return
     */
    AppVer addAppver(AppVer appVer);

    /**
     * 修改APP版本信息
     * @param appVer APP版本信息
     * @return
     */
    AppVer updateAppver(AppVer appVer);

    /**
     * 删除APP版本信息
     * @param id
     */
    void deleteAppverById(Long id);

    /**
     * 根据系统类型获取最低版本
     * @param system
     * @return
     */
    String getMinverBySystem(String system);

    /**
     * APP设备和版本信息同步
     * @param appSync
     */
    void sync(AppSync appSync);

}
