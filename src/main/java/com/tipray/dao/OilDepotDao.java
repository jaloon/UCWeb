package com.tipray.dao;

import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * OilDepotDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface OilDepotDao extends BaseDao<OilDepot> {

	/**
	 * 根据油库名称获取油库列表
	 * 
	 * @param oildepotName
	 * @return
	 */
    List<OilDepot> findByName(String oildepotName);

	/**
	 * 根据油库名称获取油库信息
	 * 
	 * @param oildepotName
	 * @return
	 */
    OilDepot getByName(String oildepotName);

	/**
	 * 根据油库编号获取油库ID
	 * 
	 * @param officialId
	 * @return
	 */
    Long getIdByOfficialId(String officialId);

	/**
	 * 根据油库编号获取油库信息
	 * 
	 * @param officialId
	 * @return
	 */
    OilDepot getByOfficialId(String officialId);

	/**
	 * 根据油库ID删除与该油库有关的所有卡信息
	 * 
	 * @param id
	 */
    void deleteOilDepotCardsById(Long id);

	/**
	 * 根据油库ID和卡ID添加与该油库有关的卡信息
	 * 
	 * @param map
	 */
    void addOilDepotCardsByIdAndCardIds(List<Map<String, Object>> list);

	/**
	 * 根据卡ID查询所属油库信息
	 * 
	 * @param cardId
	 * @return
	 */
    List<OilDepot> findOilDepotsByCardId(Long cardId);


	/**
	 * 批量添加油库
	 * 
	 * @param oilDepots
	 */
    void addOilDepots(List<OilDepot> oilDepots);

	/**
	 * 根据指定数量获取最近添加的油库列表
	 * 
	 * @param size
	 * @return
	 */
    List<OilDepot> findRecentOilDepotsBySize(Integer size);

	/**
	 * 获取所有油库的ID和名称
	 * 
	 * @return
	 */
    List<Map<String, Object>> findIdAndNameOfAllOilDepots();

    /**
     * 获取油库用于转发道闸通知的读卡器个数
     * @param oilDepotId 油库ID
     * @return
     */
    Integer barrierCount(Long oilDepotId);
}
