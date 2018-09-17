package com.tipray.dao;

import com.tipray.bean.baseinfo.AppVer;
import com.tipray.bean.baseinfo.CenterDev;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenlong
 * @version 1.0 2018-09-12
 */
public interface CenterDevDao extends BaseDao<CenterDev> {

    /**
     * 根据APP设备UUID获取用户中心ID列表
     *
     * @param uuid APP设备UUID
     * @return 用户中心ID列表
     */
    List<Long> findCenterIdsByUuid(String uuid);

    /**
     * 根据用户中心ID获取uuid归属信息列表
     * @param centerId 用户中心ID
     * @return uuid归属信息
     */
    List<CenterDev> findCenterDevsByCenterId(Long centerId);

    /**
     * 统计uuid归属记录数目
     * @param uuid APP设备UUID
     * @param centerId 用户中心ID
     * @return uuid归属记录数目
     */
    Integer countByUuidAndCenterId(@Param("uuid") String uuid, @Param("centerId") Long centerId);

    /**
     * 删除全部数据
     */
    void deleteAll();

    /**
     * 获取所有ID
     * @return
     */
    List<Long> findAllIds();

    /**
     * 批量添加
     * @param list
     */
    void batchAdd(List<CenterDev> list);

    /**
     * 批量更新
     * @param list
     */
    void batchUpdate(List<CenterDev> list);

    /**
     * 批量删除
     * @param uuids
     */
    void batchDelete(List<Long> ids);

}
