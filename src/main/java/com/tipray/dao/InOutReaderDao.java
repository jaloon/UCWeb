package com.tipray.dao;

import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * InOutReaderDao
 *
 * @author chenlong
 * @version 1.0 2018-01-15
 */
@MyBatisAnno
public interface InOutReaderDao extends BaseDao<InOutReader> {
    /**
     * 根据油库ID查询出入库读卡器信息
     *
     * @param oilDepotId
     * @return
     */
    List<InOutReader> findByOilDepotId(Long oilDepotId);

    /**
     * 根据油库ID查询出入库读卡器ID
     * @param oilDepotId
     * @return
     */
    List<Integer> findReaderIdsByOilDepotId(Long oilDepotId);

    /**
     * 根据油库ID删除出入库读卡器信息
     *
     * @param oilDepotId
     */
    void deleteByOilDepotId(Long oilDepotId);

    /**
     * 根据出入库读卡器设备ID获取读卡器信息
     *
     * @param devId
     * @return
     */
    InOutReader getByDevId(Integer devId);

    /**
     * 获取未使用的读卡器信息
     *
     * @return
     */
    List<InOutReader> findUnusedReader();

    /**
     * 批量添加读卡器信息
     *
     * @param readers
     */
    void addReaderList(List<InOutReader> list);

    /**
     * 根据油库ID查找由于转发道闸通知的读卡器ID
     *
     * @param oilDepotId  油库ID
     * @param barrierType 道闸类型（1 进，2 出）
     * @return 读卡器ID
     */
    List<Integer> findBarrierReaderIdByDepotId(@Param("oilDepotId") Integer oilDepotId,
                                               @Param("barrierType") Integer barrierType);
}
