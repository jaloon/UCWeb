package com.tipray.dao;

import com.tipray.bean.baseinfo.TransportCard;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * TransportCardDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface TransportCardDao extends BaseDao<TransportCard> {
    /**
     * 统计卡ID数目
     * @param cardId
     * @return
     */
	Integer countCardByCardId(Long cardId);

    /**
     * 根据卡ID更新配送卡
     * @param card
     */
	void updateByCardId(TransportCard card);

    /**
     * 根据卡ID删除配送卡
     * @param cardId
     */
	void deleteByCardId(Long cardId);

	/**
	 * 获取未使用的配送卡
	 */
	List<TransportCard> findUnusedTranscards();

	/**
	 * 根据配送卡序号获取配送卡ID
	 * 
	 * @param id
	 *            配送卡序号
	 * @return 配送卡ID
	 */
	Long getTransportCardIdById(Long id);

	/**
	 * 根据配送卡序号获取相关车辆的车台设备ID
	 * 
	 * @param id
	 *            配送卡序号
	 * @return 车台设备ID
	 */
	Integer getTerminalIdById(Long id);

	/**
	 * 根据配送卡ID删除相关车辆的配送卡
	 * 
	 * @param transportCardId
	 *            配送卡ID
	 */
	void deleteVehicleCardId(Long transportCardId);

    /**
     * 根据配送卡ID获取配送卡相关信息
     *
     * @param transportCardId 配送卡ID
     * @return 配送卡相关信息
     */
    Map<String, Object> getByTransportCardId(Long transportCardId);
}
