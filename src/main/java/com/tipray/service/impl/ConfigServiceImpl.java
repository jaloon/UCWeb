package com.tipray.service.impl;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.Config;
import com.tipray.service.ConfigService;

/**
 * 配置管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("configService")
public class ConfigServiceImpl implements ConfigService {
	@Resource
	private com.tipray.dao.ConfigDao configDao;

	@Override
	public Config addConfig(Config bean) {
		if (bean != null) {
			configDao.add(bean);
		}
		return bean;
	}

	@Override
	public Config updateConfig(Config bean) {
		if (bean != null) {
			configDao.update(bean);
		}
		return bean;
	}

	@Override
	public Config getConfigById(Long id) {
		if (id == null) {
			return null;
		}
		return configDao.getById(id);
	}

	@Override
	public List<Config> findAllConfigs() {
		return configDao.findAll();
	}
}
