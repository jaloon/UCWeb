package com.tipray.service;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.core.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * OilDepotService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface OilDepotService {
	/**
	 * 新增油库
	 * 
	 * @param oilDepot
	 * @throws ServiceException
	 */
    OilDepot addOilDepot(OilDepot oilDepot) throws ServiceException;

	/**
	 * 修改油库信息
	 * 
	 * @param oilDepot
	 *            油库
	 * @param readersJson
	 *            油库出入库读卡器集合JSON字符串
	 * @param cardIds
	 *            油库相关卡ID集合（类似：“1,2”，ID之间以英文逗号相连）
	 * @return
	 * @throws Exception
	 */
    OilDepot updateOilDepot(OilDepot oilDepot, String readersJson, String cardIds) throws Exception;

	/**
	 * 根据Id删除油库
	 * 
	 * @param id
	 */
    void deleteOilDepotById(Long id) throws ServiceException;

	/**
	 * 根据Id获取油库信息
	 * 
	 * @param id
	 * @return
	 */
    OilDepot getOilDepotById(Long id);

	/**
	 * 根据名称查询油库信息列表
	 * 
	 * @param oildepotName
	 * @return
	 */
    List<OilDepot> findByName(String oildepotName);

    /**
     * 根据油库编号获取油库ID
     *
     * @param officialId
     * @return
     */
    Long getIdByOfficialId(String officialId);

	/**
	 * 查询所有的油库信息列表
	 * 
	 * @param
	 * @return
	 */
    List<OilDepot> findAllOilDepots();

	/**
	 * 获取油库数目
	 * 
	 * @return
	 */
    long countOilDepot(OilDepot oilDepot);

	/**
	 * 分页查询油库信息
	 * 
	 * @param oilDepot
	 * @param page
	 * @return
	 */
    List<OilDepot> findByPage(OilDepot oilDepot, Page page);

	/**
	 * 分页查询油库信息
	 * 
	 * @param oilDepot
	 * @param page
	 * @return
	 */
    GridPage<OilDepot> findOilDepotsForPage(OilDepot oilDepot, Page page);

	/**
	 * 查询油库是否存在
	 * 
	 * @param oilDepot
	 * @return
	 */
    Object isOilDepotExist(OilDepot oilDepot);

	/**
	 * 查询未使用的读卡器
	 * 
	 * @return
	 */
    List<InOutReader> findUnusedReaders();

	/**
	 * 批量添加油库
	 * 
	 * @param oilDepots
	 * @throws ServiceException
	 */
    void addOilDepots(List<OilDepot> oilDepots) throws ServiceException;
	
	/**
	 * 获取所有油库的ID和名称
	 */
    List<Map<String, Object>> getIdAndNameOfAllOilDepots();

	/**
	 * 获取所有油库和加油站的ID和名称
	 * 
	 * @param depotVer
	 *            油库版本
	 * @param stationVer
	 *            加油站版本
	 * @return
	 */
    Map<String, Object> getIdAndNameOfAllOilDepotsAndGasStations(Long depotVer, Long stationVer);

    /**
     * 获取油库用于转发道闸通知的读卡器个数
     * @param oilDepotId 油库ID
     * @return
     */
    Integer barrierCount(Long oilDepotId);
}
