package com.tipray.service;

import org.apache.ibatis.annotations.Param;

import com.tipray.bean.VehicleParamVer;
import com.tipray.core.exception.ServiceException;

/**
 * CarParamVerService
 * 
 * @author chenlong
 * @version 1.0 2018-01-15
 *
 */
public interface VehicleParamVerService {
	/**
	 * 新增车辆参数版本
	 * 
	 * @param carParamVer
	 * @throws ServiceException
	 */
    VehicleParamVer addCarParamVer(VehicleParamVer carParamVer) throws ServiceException;

	/**
	 * 修改车辆参数版本
	 * 
	 * @param carParamVer
	 * @throws ServiceException
	 */
    VehicleParamVer updateCarParamVer(VehicleParamVer carParamVer) throws ServiceException;

	/**
	 * 根据Id删除车辆参数版本
	 * 
	 * @param id
	 * @throws ServiceException
	 */
    void deleteCarParamVersById(Long id) throws ServiceException;

	/**
	 * 根据Id获取车辆参数版本
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
    VehicleParamVer getCarParamVerById(Long id);

	/**
	 * 根据参数名称获取参数版本信息
	 * 
	 * @param param
	 * @return
	 */
    VehicleParamVer getByParam(String param);

	/**
	 * 跟据参数名称删除参数版本信息
	 * 
	 * @param param
	 */
    void deleteByParam(String param) throws ServiceException;

	/**
	 * 跟据参数名称更新参数版本信息
	 * 
	 * @param param
	 * @param ver
	 */
    void updateVerByParam(@Param("param") String param, @Param("ver") Long ver);
}
