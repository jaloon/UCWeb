package com.tipray.dao;

import com.tipray.bean.baseinfo.Card;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

import java.util.List;

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
	 * @param oilDepotId 油库ID
	 * @return
	 */
    List<Card> findByOilDepotId(Long oilDepotId);

	/**
	 * 根据油库ID查询出入库卡
	 * 
	 * @param oilDepotId 油库ID
	 * @return 出入库卡集合
	 */
    List<Card> findInOutCardsByOilDepotId(Long oilDepotId);

    /**
     * 根据油库ID查询出入库卡ID集合
     * @param oilDepotId 油库ID
     * @return 出入库卡ID集合
     */
    List<Long> findInOutCardIdsByOilDepotId(Long oilDepotId);

	/**
	 * 根据加油站ID查询卡信息
	 * 
	 * @param gasStationId 加油站ID
	 * @return 卡信息
	 */
    List<Card> findByGasStationId(Long gasStationId);

	/**
	 * 根据卡ID查询卡信息
	 * 
	 * @param cardId 卡ID
	 * @return 卡信息
	 */
    Card getByCardId(Long cardId);

	/**
	 * 根据卡类型查询未使用的卡
	 * @param cardType 卡类型
	 * @return 未使用的卡
	 */
    List<Long> findUnusedCards(Integer cardType);

	/**
	 * 根据卡ID删除与油库相关信息
	 * 
	 * @param cardId 卡ID
	 */
    void deleteOilDepotCardByCardId(Long cardId);

	/**
	 * 根据卡ID删除与加油站相关信息
	 * 
	 * @param cardId 卡ID
	 */
    void deleteGasStationCardByCardId(Long cardId);
}
