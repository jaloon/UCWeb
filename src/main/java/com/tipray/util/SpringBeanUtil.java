package com.tipray.util;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.tipray.service.CommonService;
import com.tipray.service.ConfigService;
import com.tipray.service.PermissionService;
import com.tipray.service.RoleService;
import com.tipray.service.SessionService;
import com.tipray.service.UserService;

/**
 * SpringBeanUtil
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class SpringBeanUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringBeanUtil.applicationContext = applicationContext;
	}

	/**
	 * 通过名称在spring容器中获取对象
	 * 
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		if (applicationContext == null) {
			return null;
		}
		return applicationContext.getBean(beanName);
	}

	public static <T> T getBean(Class<T> clazz) {
		if (applicationContext == null) {
			return null;
		}
		return applicationContext.getBean(clazz);
	}

	public static CommonService getCommonService() {
		return (CommonService) getBean("commonService");
	}

	public static PermissionService getPermissionService() {
		return (PermissionService) getBean("permissionService");
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return (SqlSessionFactory) getBean("sqlSessionFactory");
	}

	public static ConfigService getConfigService() {
		return (ConfigService) getBean("configService");
	}

	public static RoleService getRoleService() {
		return (RoleService) getBean("roleService");
	}

	public static SessionService getSessionService() {
		return (SessionService) getBean("sessionService");
	}

	public static UserService getUserService() {
		return (UserService) getBean("userService");
	}

}
