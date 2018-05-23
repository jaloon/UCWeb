package com.tipray.service;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.TransportCard;
import com.tipray.core.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * TransportCardService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface TransportCardService {
	/**
	 * 新增配送卡
	 * 
	 * @param transportCard
	 * @throws ServiceException
	 * @return
	 */
    TransportCard addTransportCard(TransportCard transportCard) throws ServiceException;

	/**
	 * 修改配送卡信息
	 * 
	 * @param transportCard
	 * @throws ServiceException
	 * @return
	 */
    TransportCard updateTransportCard(TransportCard transportCard) throws ServiceException;

	/**
	 * 根据Id删除配送卡
	 * 
	 * @param id
	 * @throws ServiceException
	 */
    void deleteTransportCardById(Long id) throws ServiceException;

	/**
	 * 根据Id获取配送卡信息
	 * 
	 * @param id
	 * @return
	 */
    TransportCard getTransportCardById(Long id);

	/**
	 * 查询所有的配送卡信息列表
	 * 
	 * @param
	 * @return
	 */
    List<TransportCard> findAllTransportCards();

	/**
	 * 获取配送卡数目
	 * 
	 * @param transportCard
	 * @return
	 */
    long countTransportCard(TransportCard transportCard);

	/**
	 * 分页查询配送卡信息
	 * 
	 * @param transportCard
	 * @param page
	 * @return
	 */
    List<TransportCard> findByPage(TransportCard transportCard, Page page);

	/**
	 * 分页查询配送卡信息
	 * 
	 * @param transportCard
	 * @param page
	 * @return
	 */
    GridPage<TransportCard> findTransportCardsForPage(TransportCard transportCard, Page page);

	/**
	 * 获取未使用的配送卡
	 * 
	 * @return
	 */
    List<TransportCard> findUnusedTranscards();

	/**
	 * 根据配送卡ID获取配送卡相关信息
	 *
	 * @param transportCardId 配送卡ID
	 * @return 配送卡相关信息
	 */
    Map<String, Object> getByTransportCardId(Long transportCardId);
}