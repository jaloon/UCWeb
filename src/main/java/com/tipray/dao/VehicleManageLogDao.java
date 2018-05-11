package com.tipray.dao;

import java.util.List;
import java.util.Map;

import com.tipray.bean.baseinfo.User;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * VehicleManageLogDao
 * 
 * @author chenlong
 * @version 1.0 2018-04-12
 *
 */
@MyBatisAnno
public interface VehicleManageLogDao extends BaseDao<VehicleManageLog> {
	/**
	 * 根据操作员获取车辆管理日志
	 * 
	 * @param user
	 * @return
	 */
    List<VehicleManageLog> findByUser(User user);

	/**
	 * 根据日志类型获取车辆管理日志
	 * 
	 * @param type
	 * @return
	 */
    List<VehicleManageLog> findByType(Integer type);

	/**
	 * 根据UDP业务ID和请求发起时间获取UDP应答结果
	 * 
	 * @param map
	 *            {@link Map} 参数 <br>
	 *            userId {@link Long} 用户ID <br>
	 *            isApp {@link Integer} 是否手机APP操作 <br>
	 *            udpBizId {@link Short} UDP业务ID <br>
	 *            token {@link String} UUID
	 * @return {@link Map}<br>
	 *         token {@link String} UUID<br>
	 *         description {@link String} 请求描述<br>
	 *         result {@link String} 请求结果<br>
	 *         responseMsg {@link ResponseMsg} UDP应答信息<br>
	 */
    Map<String, Object> findUdpResult(Map<String, Object> map);

}
