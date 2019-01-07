package com.tipray.dao;

import com.tipray.bean.record.SealRecord;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * SealRecordDao
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@MyBatisAnno
public interface SealRecordDao extends BaseDao<SealRecord> {

    /**
     * 按车辆查询最新施解封记录
     *
     * @param carId 车辆ID
     * @return 施解封记录
     */
    Map<String, Object> getLastSealRecord(Long carId);

    /**
     * 按车辆查询最新n条施解封记录
     *
     * @param carId     车辆ID
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 施解封记录
     */
    List<Map<String, Object>> findSealRecords(@Param("carId") Long carId,
                                              @Param("beginTime") String beginTime,
                                              @Param("endTime") String endTime);
}
