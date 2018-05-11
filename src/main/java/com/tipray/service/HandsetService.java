package com.tipray.service;

import java.util.List;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.Handset;
import com.tipray.core.exception.ServiceException;

/**
 * HandsetService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface HandsetService {
	/**
	 * 新增手持机
	 * 
	 * @param handset
	 * @throws ServiceException
	 */
    Handset addHandset(Handset handset) throws ServiceException;

	/**
	 * 修改手持机信息
	 * 
	 * @param handset
	 * @throws ServiceException
	 */
    Handset updateHandset(Handset handset) throws ServiceException;

	/**
	 * 根据Id删除手持机
	 * 
	 * @param id
	 * @throws ServiceException
	 */
    void deleteHandsetById(Long id) throws ServiceException;

	/**
	 * 根据Id获取手持机信息
	 * 
	 * @param id
	 * @return
	 */
    Handset getHandsetById(Long id);

	/**
	 * 根据加油站ID查询手持机信息
	 * 
	 * @param gasStationId
	 * @return
	 */
    Handset getByGasStationId(Long gasStationId);

	/**
	 * 查询所有的手持机信息列表
	 * 
	 * @param
	 * @return
	 */
    List<Handset> findAllHandsets();

	/**
	 * 获取手持机数目
	 * 
	 * @return
	 */
    long countHandset(Handset handset);

	/**
	 * 分页查询手持机信息
	 * 
	 * @param handset
	 * @param page
	 * @return
	 */
    List<Handset> findByPage(Handset handset, Page page);

	/**
	 * 分页查询手持机信息
	 * 
	 * @param handset
	 * @param page
	 * @return
	 */
    GridPage<Handset> findHandsetsForPage(Handset handset, Page page);

	/**
	 * 获取加油站列表
	 * 
	 * @return
	 */
    List<GasStation> getGasStationList();

	/**
	 * 查询在设备信息表里还未添加到手持机信息表里的手持机设备ID
	 * 
	 * @return
	 */
    List<Integer> findUnaddHandset();

	/**
	 * 查询未配置手持机的加油站
	 * 
	 * @return
	 */
    List<GasStation> findUnconfigGasStation();

	/**
	 * 查询未指定加油站的手持机设备ID
	 * 
	 * @return
	 */
    List<Integer> findUnusedHandset();
}
