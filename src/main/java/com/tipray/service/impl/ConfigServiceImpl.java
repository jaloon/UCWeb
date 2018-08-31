package com.tipray.service.impl;

import com.tipray.bean.Config;
import com.tipray.service.ConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 配置管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Service("configService")
public class ConfigServiceImpl implements ConfigService {
	@Resource
	private com.tipray.dao.ConfigDao configDao;

	@Transactional
	@Override
	public Config addConfig(Config bean) {
		if (bean != null) {
			configDao.add(bean);
		}
		return bean;
	}

	@Transactional
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
