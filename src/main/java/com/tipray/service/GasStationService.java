package com.tipray.service;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.core.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * GasStationService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface GasStationService {
	/**
	 * 新增加油站
	 * 
	 * @param gasStation
	 * @throws ServiceException
	 */
    GasStation addGasStation(GasStation gasStation) throws ServiceException;

	/**
	 * 修改加油站信息
	 * 
	 * @param gasStation
	 * @param handsetId
	 * @param cardIds
	 * @return
	 * @throws ServiceException
	 */
    GasStation updateGasStation(GasStation gasStation, Integer handsetId, String cardIds)
			throws ServiceException;

	/**
	 * 根据Id删除加油站
	 * 
	 * @param id
	 */
    void deleteGasStationById(Long id) throws ServiceException;

	/**
	 * 根据Id获取加油站信息
	 * 
	 * @param id
	 * @return
	 */
    GasStation getGasStationById(Long id);

	/**
	 * 根据名称查询加油站信息列表
	 * 
	 * @param gasStationName
	 * @return
	 */
    List<GasStation> findByName(String gasStationName);

	/**
	 * 查询所有的加油站信息列表
	 * 
	 * @return
	 */
    List<GasStation> findAllGasStations();

	/**
	 * 获取加油站数目
	 * 
	 * @param gasStation
	 * @return
	 */
    long countGasStation(GasStation gasStation);

	/**
	 * 分页查询加油站信息
	 * 
	 * @param gasStation
	 * @param page
	 * @return
	 */
    List<GasStation> findByPage(GasStation gasStation, Page page);

	/**
	 * 分页查询加油站信息
	 * 
	 * @param gasStation
	 * @param page
	 * @return
	 */
    GridPage<GasStation> findGasStationsForPage(GasStation gasStation, Page page);

	/**
	 * 查询加油站是否存在
	 * 
	 * @param gasStation
	 * @return
	 */
    GasStation isGasStationExist(GasStation gasStation);

	/**
	 * 批量添加加油站
	 * 
	 * @param gasStations
	 * @throws ServiceException
	 */
    void addGasStations(List<GasStation> gasStations) throws ServiceException;
	
	/**
	 * 获取所有加油站的ID和名称
	 */
    List<Map<String, Object>> getIdAndNameOfAllGasStations();
}
