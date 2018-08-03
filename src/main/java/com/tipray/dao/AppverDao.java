package com.tipray.dao;

import com.tipray.bean.baseinfo.AppVer;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisAnno
public interface AppverDao extends BaseDao<AppVer> {

    /**
     * 统计用户中心APP版本信息数
     *
     * @param centerId 用户中心ID
     * @param system   手机系统
     * @return APP版本信息数
     */
    Integer countCenterAppVer(@Param("centerId") Long centerId, @Param("system") String system);

    /**
     * 根据用户中心ID获取APP版本信息
     * @param centerId 用户中心ID
     * @return
     */
    List<AppVer> findByCenterId(Long centerId);

    /**
     * 根据用户中心ID和系统类型获取指定版本号
     * @param centerId 用户中心ID
     * @param system   手机系统
     * @return 指定版本号
     */
    String getAssignVerByCenterIdAndSystem(@Param("centerId") Long centerId, @Param("system") String system);

    /**
     * 根据用户中心ID和系统类型获取最低版本号
     * @param centerId 用户中心ID
     * @param system   手机系统
     * @return 最低版本号
     */
    String getMinverByCenterIdAndSystem(@Param("centerId") Long centerId, @Param("system") String system);

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
    void batchAdd(List<AppVer> list);

    /**
     * 批量更新
     * @param list
     */
    void batchUpdate(List<AppVer> list);

    /**
     * 批量删除
     * @param uuids
     */
    void batchDelete(List<Long> ids);

}
