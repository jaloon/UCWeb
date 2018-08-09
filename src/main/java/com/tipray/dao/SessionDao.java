package com.tipray.dao;

import com.tipray.bean.Session;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.MapKey;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SessionDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface SessionDao extends BaseDao<Session> {
	/**
	 * 更新操作时间
	 * 
	 * @param session
	 */
    void updateOperateDate(Session session);

	/**
	 * 删除session
	 * 
	 * @param uuid
	 */
    void deleteByUUID(String uuid);

	/**
	 * 删除用户的所有session
	 * 
	 * @param userId
	 */
    void deleteByUser(Long userId);

	/**
	 * 删除过期session
	 * 
	 * @param timeoutDate
	 *            过期临界时间
	 */
    void deleteTimeOutSession(Date timeoutDate);

	/**
	 * 根据uuid获取session
	 * 
	 * @param uuid
	 * @return
	 */
    Session getByUUID(String uuid);

	/**
	 * 根据oldUuid获取session
	 * 
	 * @param oldUuid
	 * @return
	 */
    Session getByOldUUID(String oldUuid);

	/**
	 * 根据操作员ID获取session
	 * 
	 * @param userId
	 * @return
	 */
    List<Session> getByUser(Long userId);

	/**
	 * 获取session列表
	 * @return
	 */
	@MapKey("uuid")
	Map<String, Session> findSessions();
}
