package com.tipray.service;

import java.util.List;
import java.util.Map;

import com.tipray.bean.record.DistributionRecord;
import com.tipray.core.exception.ServiceException;

/**
 * DistributionRecordService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface DistributionRecordService extends RecordService<DistributionRecord> {
	/**
	 * 根据物流配送接口信息添加配送记录
	 * 
	 * @param distributionMap
	 *            物流配送接口信息
	 * @throws ServiceException
	 */
    void addDistributionRecord(Map<String, Object> distributionMap) throws ServiceException;

	/**
	 * 根据配送单号统计配送记录数量
	 * 
	 * @param invoice
	 *            配送单号
	 */
    Integer countByInvoice(String invoice);

	/**
	 * 根据加油站ID获取未完成配送信息
	 * 
	 * @param gasStationId
	 *            {@link Long} 加油站ID
	 * @return
	 */
    List<Map<String, Object>> findDistributionsByGasStationId(Long gasStationId);

	/**
	 * 根据车牌号和仓号获取未完成配送信息
	 * 
	 * @param carNumber
	 *            车牌号
	 * @param storeId
	 *            仓号
	 * @return
	 */
    List<Map<String, Object>> findDistributionsByVehicle(String carNumber, Integer storeId);

}
