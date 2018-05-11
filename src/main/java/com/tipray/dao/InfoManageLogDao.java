package com.tipray.dao;

import com.tipray.bean.baseinfo.User;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

import java.util.List;

/**
 * InfoManageLogDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface InfoManageLogDao extends BaseDao<InfoManageLog> {
	/**
	 * 根据操作员获取操作日志
	 * 
	 * @param user
	 * @return
	 */
    List<InfoManageLog> findByUser(User user);

	/**
	 * 根据日志类型获取操作日志
	 * 
	 * @param type
	 * @return
	 */
    List<InfoManageLog> findByType(Integer type);

}
