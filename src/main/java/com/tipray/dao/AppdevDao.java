package com.tipray.dao;

import com.tipray.bean.baseinfo.AppDev;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisAnno
public interface AppdevDao extends BaseDao<AppDev> {
    /**
     * 获取所有ID
     * @return
     */
    List<Long> findAllIds();

    /**
     * 获取所有UUID
     * @return
     */
    List<String> findAllUuids();

    /**
     * 批量添加
     * @param list
     */
    void batchAdd(List<AppDev> list);

    /**
     * 批量更新
     * @param list
     */
    void batchUpdate(List<AppDev> list);

    /**
     * 批量删除
     * @param uuids
     */
    void batchDelete(List<Long> ids);

    /**
     * 批量删除
     * @param uuids
     */
    void batchDeleteByUuids(List<String> uuids);

    /**
     * 根据手机UUID和appid更新手机型号和当前APP版本号
     *
     * @param model      手机型号
     * @param currentVer 当前APP版本号
     * @param uuid       手机UUID
     * @param appid      应用标识
     */
    void updateModelAndCurrentVerByUuidAndAppid(@Param("model") String model,
                                                @Param("currentVer") String currentVer,
                                                @Param("uuid") String uuid,
                                                @Param("appid") String appid);


    /**
     * 统计UUID数目
     *
     * @param uuid
     * @return
     */
    Integer countByUuid(String uuid);

    /**
     * 统计UUID、appid数目
     *
     * @param uuid  手机UUID
     * @param appid 应用标识
     * @return
     */
    Integer countByUuidAndAppid(@Param("uuid") String uuid, @Param("appid") String appid);

    /**
     * 根据UUID获取记录ID
     *
     * @param uuid  手机UUID
     * @param appid 应用标识
     * @return
     */
    List<Long> findIdsByUuidAndAppid(@Param("uuid") String uuid, @Param("appid") String appid);

    /**
     * 根据手机UUID删除记录
     *
     * @param uuid  手机UUID
     * @param appid 应用标识
     */
    void deleteByUuidAndAppid(@Param("uuid") String uuid, @Param("appid") String appid);


}
