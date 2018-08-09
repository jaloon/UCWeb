package com.tipray.init;

import com.tipray.core.GridProperties;
import com.tipray.init.impl.PropertyDictInit;
import com.tipray.init.impl.SqliteInit;
import com.tipray.util.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化 - 运行器 初始化系统属性，如： 1、数据库的初始化或更新 2、权限树的初始化 2、数据字典的初始化
 * 
 * @author chends
 *
 */
public class InitializationsRunner extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private List<AbstractInitialization> inits = new ArrayList<AbstractInitialization>();

	@Override
	public void init() {
		try {
			long start = System.currentTimeMillis();
			logger.info("===>InitializationsRunner start init");
			addDefaultInitializations();
			if (GridProperties.IS_DB_RESET || !SpringBeanUtil.getCommonService().isTablesExist()) {
				execute(AbstractInitialization.MODE_CREATE);
			} else {
				execute(AbstractInitialization.MODE_UPDATE);
			}
			logger.info("===>InitializationsRunner end init,cost{}", System.currentTimeMillis() - start);
		} catch (Exception e) {
			logger.error("===>InitializationsRunner init error!", e);
		}
	}

	public void execute(String mode) {
		logger.info("==>InitializationsRunner init mode={}", mode);
		for (AbstractInitialization init : inits) {
			long startTime = System.currentTimeMillis();
			try {
				logger.info("==>InitializationsRunner execute [{}] start", init.toString());
				init.execute(mode);
				logger.info("==>InitializationsRunner execute [{}] end,cost [{}]ms", init.toString(),
						System.currentTimeMillis() - startTime);
			} catch (Exception e) {
				logger.error("==>InitializationsRunner execute [" + init.toString() + "] error!", e);
			}
		}
	}

	/**
	 * 执行的初始化操作
	 */
	private void addDefaultInitializations() {
//		addInitialization(new DatabaseSqlInit());
// 		addInitialization(new PermissionXmlInit());
		addInitialization(new PropertyDictInit());
		addInitialization(new SqliteInit());
	}

	private void addInitialization(AbstractInitialization init) {
		if (!inits.contains(init)) {
			inits.add(init);
		}
	}

}
