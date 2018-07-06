package com.tipray.dao;

import com.tipray.bean.VehicleParamVer;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Map;

/**
 * VehicleParamVerDao
 * 
 * @author chenlong
 * @version 1.0 2018-01-15
 *
 */
@MyBatisAnno
public interface VehicleParamVerDao extends BaseDao<VehicleParamVer> {
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
    void deleteByParam(String param);

	/**
	 * 跟据参数名称更新参数版本信息
	 * 
	 * @param param
	 * @param ver
	 */
    void updateVerByParam(@Param("param") String param, @Param("ver") Long ver);

	/**
	 * 获取加油站和油库的版本号
	 * 
	 * @return
	 */
    Map<String, BigInteger> getVersionsOfOilDepotAndGasStation();
}
