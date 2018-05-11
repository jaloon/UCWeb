package com.tipray.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tipray.bean.Config;
import com.tipray.util.SpringBeanUtil;

/**
 * 系统配置属性
 * @author chends
 *
 */
public class GridConfig {
	private static Logger logger = LoggerFactory.getLogger(GridConfig.class);
	private static List<Config> properties = new ArrayList<Config>();
	
	/** 超级管理员账号*/
	public static final String SUPER_ADMIN = "admin";
	/** 部门根节点的Id*/
	public static final Long ROOT_DEPT_ID = 1L;
	/** 部门根节点的名称*/
	public static final String ROOT_DEPT_NAME = "根节点";
	/** 默认系统语言*/
	public final static String DEFAULT_LANGUAGE="zh-cn";
	/** 产品名称 */
	public static String PRODUCT_NAME;
	/** 技术支持 */
	public static String PRODUCT_SUPPORT;
	/** 服务网站 */
	public static String PRODUCT_NETWORK;
	/** 系统发布时间 */
	public static Date PRODUCT_PUBDATE;
	/** 产品LOGO */
	public static String PRODUCT_LOGO;
	/** 产品版本 */
	public static String PRODUCT_VERSION;
	
	static{
		reload();
	}
	public static void reload(){
		properties.clear();
		
		PRODUCT_NAME = getValue("");
		PRODUCT_LOGO = getValue("");
		PRODUCT_VERSION = getValue("");
	}
	
	private static List<Config> getProperties(){
		if (properties.size() == 0) {
			synchronized (properties) {
				try {
					properties.clear();
					properties.addAll(SpringBeanUtil.getConfigService().findAllConfigs());
				} catch (Exception e) {
					logger.error("查询系统配置信息出错！", e);
				}
			}
		}
		return properties;
	}
	private static String getValue(String code) {
		if(code!=null){
			for (Config config : getProperties()) {
				if(config.getCode().equals(code)){
					return config.getValue();
				}
			}
		}
		return null;
	}
}
