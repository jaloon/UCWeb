package com.tipray.service;

import com.tipray.bean.baseinfo.AppDev;
import com.tipray.bean.baseinfo.AppSync;
import com.tipray.bean.baseinfo.AppVer;
import com.tipray.bean.baseinfo.CenterDev;
import com.tipray.core.exception.ServiceException;

public interface AppService {
    /**
     * 添加或更新APP设备信息
     *
     * @param uuid   手机UUID
     * @param appid  应用标识
     * @param system 手机系统
     * @param ver    当前版本
     * @return {@link AppDev}
     */
    AppDev addOrUpdateAppdev(String uuid, String appid, String system, String ver);

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
     * @param appid      应用标识
     * @throws ServiceException
     */
    void updateModelAndCurrentVerByUuidAndAppid(String model, String currentVer, String uuid, String appid) throws ServiceException;

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
     * UUID是否已存在
     *
     * @param uuid
     * @param appid
     * @return
     */
    boolean isAppdevExist(String uuid, String appid);

    /**
     * 新增APP版本信息
     *
     * @param appVer APP版本信息
     * @return
     */
    AppVer addAppver(AppVer appVer);

    /**
     * 修改APP版本信息
     *
     * @param appVer APP版本信息
     * @return
     */
    AppVer updateAppver(AppVer appVer);

    /**
     * 删除APP版本信息
     *
     * @param id
     */
    void deleteAppverById(Long id);

    /**
     * 根据系统类型获取最低版本
     *
     * @param appid  应用标识
     * @param system 手机系统
     * @return
     */
    String getMinverByAppidAndSystem(String appid, String system);

    /**
     * 新增APP归属信息
     *
     * @param centerDev APP归属信息
     * @return
     */
    CenterDev addCenterdev(CenterDev centerDev);

    /**
     * 修改APP归属信息
     *
     * @param centerDev APP归属信息
     * @return
     */
    CenterDev updateCenterdev(CenterDev centerDev);

    /**
     * 删除APP归属信息
     *
     * @param id
     */
    void deleteCenterdevById(Long id);

    /**
     * uuid归属记录是否存在
     *
     * @param uuid APP设备UUID
     * @return
     */
    boolean isCenterdevExist(String uuid);

    /**
     * APP设备和版本信息同步
     *
     * @param appSync
     */
    void sync(AppSync appSync);

}
