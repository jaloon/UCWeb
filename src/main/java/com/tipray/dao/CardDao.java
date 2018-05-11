package com.tipray.dao;

import java.util.List;
import java.util.Map;

import com.tipray.bean.baseinfo.Card;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * CardDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface CardDao extends BaseDao<Card> {
	/**
	 * 根据油库ID查询卡信息
	 * 
	 * @param oilDepotId
	 * @return
	 */
    List<Card> findByOilDepotId(Long oilDepotId);

	/**
	 * 根据油库ID查询出入库卡
	 * 
	 * @param oilDepotId
	 * @return
	 */
    List<Card> findInOutCardsByOilDepotId(Long oilDepotId);

	/**
	 * 根据加油站ID查询卡信息
	 * 
	 * @param gasStationId
	 * @return
	 */
    List<Card> findByGasStationId(Long gasStationId);

	/**
	 * 根据卡ID查询卡信息
	 * 
	 * @param cardId
	 * @return
	 */
    Card getByCardId(Long cardId);

	/**
	 * 根据卡类型、油库ID、加油站ID查询未使用的卡
	 * 
	 * @param map
	 * @return
	 */
    List<Long> findUnusedCards(Map<String, Object> map);

	/**
	 * 根据卡ID删除与油库相关信息
	 * 
	 * @param cardId
	 */
    void deleteOilDepotCardByCardId(Long cardId);

	/**
	 * 根据卡ID删除与加油站相关信息
	 * 
	 * @param cardId
	 */
    void deleteGasStationCardByCardId(Long cardId);
}
