package com.tipray.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tipray.bean.ChangeInfo;
import com.tipray.bean.record.ChangeRecord;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * ChangeRecordDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface ChangeRecordDao extends BaseDao<ChangeRecord> {
	/**
	 * 根据远程换站信息添加换站记录
	 * 
	 * @param changeInfo
	 *            远程换站信息
	 */
    void addByChangeInfo(ChangeInfo changeInfo);

	/**
	 * 根据变更的物流配送信息添加换站记录
	 * 
	 * @param distributionMap
	 *            变更的物流配送信息
	 */
    void addByChangedDistribution(Map<String, Object> distributionMap);

	/**
	 * 根据原配送ID和变更后的配送ID获取换站ID
	 * 
	 * @param transportId
	 *            原配送ID
	 * @param changedTransportId
	 *            变更后的配送ID
	 * @return 换站ID
	 */
    long getChangeIdByTransportIdAndChangedTransportId(
            @Param("transportId") long transportId,
            @Param("changedTransportId") long changedTransportId);

	/**
	 * 根据换站ID更新换站状态
	 * 
	 * @param changeId
	 *            换站ID
	 * @param changeStatus
	 *            换站状态（0 ：未完成 | 1：远程换站请求中 | 2：远程换站完成）
	 */
    void updateChangeStatus(@Param("changeId") Long changeId, @Param("changeStatus") Integer changeStatus);
}
