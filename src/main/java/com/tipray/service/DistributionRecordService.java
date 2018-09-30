package com.tipray.service;

import com.tipray.bean.record.DistributionRecord;
import com.tipray.core.exception.ServiceException;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * DistributionRecordService
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
public interface DistributionRecordService extends RecordService<DistributionRecord> {
    /**
     * 根据物流配送接口信息添加配送记录
     *
     * @param distributionMap 物流配送接口信息
     * @return 配送单新增业务数据体
     * @throws ServiceException
     */
    ByteBuffer addDistributionRecord(Map<String, Object> distributionMap) throws ServiceException;

    /**
     * 根据配送单号统计配送记录数量
     *
     * @param invoice 配送单号
     */
    Integer countInvoice(String invoice);

    /**
     * 根据配送单号统计配送记录数量
     *
     * @param invoice 配送单号
     */
    Integer countWaitInvoice(String invoice);

    /**
     * 相同配送信息的未完成配送记录数目
     * @param distributionMap
     * @return
     */
    Integer countSameWaitDistInfo(Map<String, Object> distributionMap);

    /**
     * 根据加油站ID获取未完成配送信息
     *
     * @param gasStationId {@link Long} 加油站ID
     * @return
     */
    List<Map<String, Object>> findDistributionsByGasStationId(Long gasStationId);

    /**
     * 根据车牌号和仓号获取未完成配送信息
     *
     * @param carNumber 车牌号
     * @param storeId   仓号
     * @return
     */
    List<Map<String, Object>> findDistributionsByVehicle(String carNumber, Integer storeId);

}
