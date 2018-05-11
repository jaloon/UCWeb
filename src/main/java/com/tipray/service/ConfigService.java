package com.tipray.service;

import java.util.List;

import com.tipray.bean.Config;

/**
 * ConfigService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface ConfigService {
	/**
	 * 新增系统配置
	 * 
	 * @param bean
	 * @return
	 * @throws ServiceException
	 */
    Config addConfig(Config bean);

	/**
	 * 修改系统配置
	 * 
	 * @param bean
	 * @throws ServiceException
	 */
    Config updateConfig(Config bean);

	/**
	 * 根据Id获取系统配置
	 * 
	 * @param id
	 * @return
	 */
    Config getConfigById(Long id);

	/**
	 * 获取所有的系统配置
	 * 
	 * @param id
	 * @return
	 */
    List<Config> findAllConfigs();
}
