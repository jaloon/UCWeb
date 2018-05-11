package com.tipray.service;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.core.exception.ServiceException;

import java.util.List;

/**
 * InfoManageLogService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface InfoManageLogService {
	/**
	 * 新增操作日志
	 * 
	 * @param infoManageLog
	 * @throws ServiceException
	 */
	InfoManageLog addInfoManageLog(InfoManageLog infoManageLog) throws ServiceException;

	/**
	 * 根据Id获取操作日志信息
	 * 
	 * @param id
	 * @return
	 */
	InfoManageLog getInfoManageLogById(Long id);

	/**
	 * 查询所有的操作日志信息列表
	 * 
	 * @param
	 * @return
	 */
    List<InfoManageLog> findAllInfoManageLogs();

	/**
	 * 获取操作日志数目
	 * 
	 * @return
	 */
    long countInfoManageLog(InfoManageLog infoManageLog);

	/**
	 * 分页查询操作日志信息
	 * 
	 * @param OperateLog
	 * @param page
	 * @return
	 */
    List<InfoManageLog> findByPage(InfoManageLog infoManageLog, Page page);

	/**
	 * 分页查询操作日志信息
	 * 
	 * @param OperateLog
	 * @param page
	 * @return
	 */
    GridPage<InfoManageLog> findInfoManageLogsForPage(InfoManageLog infoManageLog, Page page);
}
