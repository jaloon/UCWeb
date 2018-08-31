package com.tipray.dao;

import com.tipray.bean.sqlite.InOutCard;
import com.tipray.bean.sqlite.InOutDev;
import com.tipray.bean.sqlite.ManageCard;
import com.tipray.bean.sqlite.OildepotInfo;
import com.tipray.bean.sqlite.UrgentCard;
import com.tipray.core.annotation.MyBatisAnno;

import java.util.List;

/**
 * @author chenlong
 * @version 1.0 2018-08-22
 */
@MyBatisAnno
public interface SqliteSyncDao {
    /**
     * 查询应急卡
     * @return
     */
    List<UrgentCard> findUrgentCard();

    /**
     * 查询管理卡
     * @return
     */
    List<ManageCard> findManageCard();

    /**
     * 查询出入库卡
     * @return
     */
    List<InOutCard> findInOutCard();

    /**
     * 查询出入库设备
     * @return
     */
    List<InOutDev> findInOutDev();

    /**
     * 查询油库
     * @return
     */
    List<OildepotInfo> findOildepot();

    /**
     * 查询版本号
     * @param paramName 参数名称
     * @return
     */
    Long getVersion(String paramName);
}
