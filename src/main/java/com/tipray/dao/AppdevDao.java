package com.tipray.dao;

import com.tipray.bean.baseinfo.AppDev;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisAnno
public interface AppdevDao extends BaseDao<AppDev> {
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
    void batchDelete(List<String> uuids);

    /**
     * 根据手机UUID更新手机型号和当前APP版本号
     *
     * @param model      手机型号
     * @param currentVer 当前APP版本号
     * @param uuid       手机UUID
     */
    void updateModelAndCurrentVerByUuid(@Param("model") String model,
                                        @Param("currentVer") String currentVer,
                                        @Param("uuid") String uuid);

    /**
     * 根据手机UUID获取APP配置信息
     *
     * @param uuid 手机UUID
     * @return APP配置信息
     */
    AppDev getByUuid(String uuid);

    /**
     * 统计UUID数目
     *
     * @param uuid
     * @return
     */
    Integer countByUuid(String uuid);

    /**
     * 根据UUID获取记录ID
     *
     * @param uuid
     * @return
     */
    List<Long> findIdsByUuid(String uuid);

    /**
     * 根据手机UUID删除记录
     *
     * @param uuid
     */
    void deleteByUuid(String uuid);

}
