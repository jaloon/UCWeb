package com.tipray.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tipray.bean.ChangeInfo;
import com.tipray.bean.record.DistributionRecord;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * DistributionRecordDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface DistributionRecordDao extends BaseDao<DistributionRecord> {
	/**
	 * 更新配送状态为换站
	 * 
	 * @param transportId
	 *            {@link Long} 配送记录的ID
	 */
    void updateStatusToChanged(Long transportId);

	/**
	 * 更新配送状态
	 * 
	 * @param transportId
	 *            {@link Long} 配送ID
	 * @param status
	 *            {@link Integer} 配送状态：<br>
	 *            0 未知<br>
	 *            1 配送未完成<br>
	 *            2 配送完成<br>
	 *            3 换站<br>
	 *            4 配送信息过期失效（新的仓位配送进来或者超过一定时间（例如1天）则置为过期）
	 */
    void updateStatus(@Param("transportId") Long transportId, @Param("status") Integer status);

	/**
	 * 根据远程换站信息添加配送记录
	 * 
	 * @param changeInfo
	 *            {@link ChangeInfo} 远程换站信息
	 */
    void addByChangeInfo(ChangeInfo changeInfo);

	/**
	 * 根据配送单号获取最近的配送记录的ID
	 * 
	 * @param invoice
	 *            {@link String} 配送单号
	 * @return {@link Long} 配送单号对应的最近的配送记录的ID
	 */
    Long getRecentTransportIdByInvoice(String invoice);

	/**
	 * 根据配送单号获取最近的配送记录
	 * 
	 * @param invoice
	 *            {@link String} 配送单号
	 * @return {@link Map} 配送单号对应的最近的配送记录：<br>
	 *         {@link Long} id 配送ID，<br>
	 *         {@link Long} oildepot_id 油库ID，<br>
	 *         {@link Long} gasstation_id 加油站ID
	 */
    Map<String, Long> getRecentDistributionByInvoice(String invoice);

	/**
	 * 根据车辆ID获取最近的配送记录的配送状态
	 * 
	 * @param carId
	 *            {@link Long} 车辆ID
	 * @return {@link Map} 车辆ID对应的最近的配送记录：<br>
	 *         {@link Long} id 配送ID，<br>
	 *         {@link Long} status 配送状态
	 */
    Map<String, Long> getRecentDistributeStateByCarId(Long carId);

	/**
	 * 根据物流配送接口信息添加配送记录
	 * 
	 * @param distributionMap
	 *            {@link Map} 物流配送接口信息：<br>
	 *            {@link Long} carId 车辆ID，<br>
	 *            {@link Integer} terminalId 车台设备ID，<br>
	 *            {@link String} distributNO 配送单号，<br>
	 *            {@link String} effectDate 配送单据时间，<br>
	 *            {@link String} vehicNo 车号，<br>
	 *            {@link String} cardNo 配送卡号，<br>
	 *            {@link String} binNum 仓号，<br>
	 *            {@link String} deptId 收油机构编码，<br>
	 *            {@link String} deptName 收油机构名称，<br>
	 *            {@link String} depotNo 提油机构编码，<br>
	 *            {@link String} depotName 提油机构名称，<br>
	 *            {@link String} distributFlag 单据标志
	 */
    void addByDistributionMap(Map<String, Object> distributionMap);

	/**
	 * 更新换站前配送记录的触发信息
	 * 
	 * @param transportId
	 *            {@link Long} 换站前配送记录的ID
	 * @param changeId
	 *            {@link Long} 换站ID
	 */
    void updateTriggerInfo(@Param("transportId") Long transportId, @Param("changeId") Long changeId);

	/**
	 * 根据配送单号统计配送记录数量
	 * 
	 * @param invoice
	 *            {@link String} 配送单号
	 */
    Integer countInvoice(String invoice);

    /**
     * 根据配送单号统计待配送记录数量
     *
     * @param invoice
     *            {@link String} 配送单号
     */
    Integer countWaitInvoice(String invoice);

    /**
     * 相同配送信息的未完成配送记录数目
     * @param distributionMap
     * @return
     */
    Integer countSameWaitDistInfo(Map<String, Object> distributionMap);

	/**
	 * 根据车辆ID和仓号检查先前的配送记录，若有未完成或未知状态的记录，改为配送完成状态
	 * 
	 * @param carId
	 *            {@link Long} 车辆ID
	 * @param storeId
	 *            {@link Integer} 仓号
	 */
    void checkDistribute(@Param("carId") Long carId, @Param("storeId") Integer storeId);

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
    List<Map<String, Object>> findDistributionsByVehicle(@Param("carNumber") String carNumber,
                                                         @Param("storeId") Integer storeId);

}
