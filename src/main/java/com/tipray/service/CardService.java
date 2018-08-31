package com.tipray.service;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Card;
import com.tipray.core.exception.ServiceException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * CardService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface CardService {
	/**
	 * 新增卡
	 * 
	 * @param Card
	 * @throws ServiceException
	 */
    Card addCard(Card card) throws ServiceException, SQLException;

	/**
	 * 修改卡信息
	 * 
	 * @param Card
	 */
    Card updateCard(Card card) throws ServiceException, SQLException;

	/**
	 * 根据Id删除卡
	 * 
	 * @param id
	 */
    void deleteCardById(Long id) throws ServiceException, SQLException;

	/**
	 * 根据Id获取卡信息
	 * 
	 * @param id
	 * @return
	 */
    Map<String, Object> getCardById(Long id);

	/**
	 * 根据油库ID查询卡信息
	 * 
	 * @param id
	 * @return
	 */
    List<Card> findByOilDepotId(Long oilDepotId);

	/**
	 * 根据加油站ID查询卡信息
	 * 
	 * @param gasStationId
	 * @return
	 */
    List<Card> findByGasStationId(Long gasStationId);

	/**
	 * 查询所有的卡信息列表
	 * 
	 * @param
	 * @return
	 */
    List<Card> findAllCards();

	/**
	 * 获取卡数目
	 * 
	 * @return
	 */
    long countCard(Card card);

	/**
	 * 分页查询卡信息
	 * 
	 * @param card
	 * @param page
	 * @return
	 */
    List<Card> findByPage(Card card, Page page);

	/**
	 * 分页查询卡信息
	 * 
	 * @param card
	 * @param page
	 * @return
	 */
    GridPage<Card> findCardsForPage(Card card, Page page);

	/**
	 * 根据卡ID查询卡是否存在
	 * 
	 * @param cardId
	 * @return
	 */
    Card isCardExist(Long cardId);

	/**
	 * 根据卡类型查询未使用的卡
	 * 
	 * @param cardType
	 * @return
	 */
    List<Long> findUnusedCard(Integer cardType);
}
