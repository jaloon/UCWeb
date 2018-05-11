package com.tipray.dao;

import java.util.List;

import com.tipray.bean.baseinfo.Handset;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * HandsetDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface HandsetDao extends BaseDao<Handset> {
	/**
	 * 根据设备ID获取手持机信息
	 * 
	 * @param deviceId
	 * @return
	 */
    Handset getByDeviceId(Integer deviceId);

	/**
	 * 根据加油站ID获取手持机信息
	 * 
	 * @param gasStationId
	 * @return
	 */
    Handset getByGasStationId(Long gasStationId);

	/**
	 * 查询在设备信息表里还未添加到手持机信息表里的手持机设备ID
	 * 
	 * @return
	 */
    List<Integer> findUnaddHandset();

	/**
	 * 查询未指定加油站的手持机设备ID
	 * 
	 * @return
	 */
    List<Integer> findUnusedHandset();

	/**
	 * 根据加油站ID重置手持机所属加油站
	 * 
	 * @param gasStationId
	 */
    void resetGasStationOfHandsetByGasStationId(Long gasStationId);
}
