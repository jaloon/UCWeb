package com.tipray.init.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tipray.core.GridConfig;
import com.tipray.core.GridProperties;
import com.tipray.init.AbstractInitialization;
import com.tipray.util.FileUtil;
import com.tipray.util.SqlScriptExcuteUtil;

/**
 * 数据库文件的初始化
 * 
 * @author chends
 *
 */
public class DatabaseSqlInit extends AbstractInitialization {
	Logger logger = LoggerFactory.getLogger(DatabaseSqlInit.class);

	private static final String DEFAULT_SQL_PATH = "sqls";

	@Override
	public void init() throws Exception {
		executeSqlFile("init");
		if (GridProperties.IS_WEB_DEVELOPMENT) {
			executeSqlFile("test");
		}
	}

	@Override
	public void update() throws Exception {
		String updatePath = FileUtil.append(FileUtil.getWebClassesPath(), DEFAULT_SQL_PATH, "update");
		File file = new File(updatePath);
		if (file.exists()) {
			File[] sqlFiles = file.listFiles();
			if (sqlFiles != null && sqlFiles.length > 0) {
				List<File> fileList = Arrays.asList(sqlFiles);
				Collections.sort(fileList, new Comparator<File>() {
					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				for (File updateDir : fileList) {
					if (updateDir.getName().compareTo(GridConfig.PRODUCT_VERSION) > 0) {
						String path = FileUtil.append("update", updateDir.getName());
						executeSqlFile(path);
						deleteSqlFile(path);
					}
				}
			}
		}
	}

	private void executeSqlFile(String path) throws Exception {
		String filePath = FileUtil.append(FileUtil.getWebClassesPath(), DEFAULT_SQL_PATH, path);
		logger.info("================>executeSqlFile={}", filePath);
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}
		File[] sqlFiles = file.listFiles();
		if (sqlFiles == null || sqlFiles.length == 0) {
			return;
		}
		List<File> fileList = Arrays.asList(sqlFiles);
		Collections.sort(fileList, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (File sqlFile : fileList) {
			logger.info("executeSqlFile {}", sqlFile.getName());
			InputStream is = new FileInputStream(sqlFile);
			List<String> sqls = FileUtil.loadSqlFile(is);
			SqlScriptExcuteUtil.executeBatchSql(sqls);
		}
	}

	/**
	 * 删除Sql文件
	 * 
	 * @param path
	 */
	private void deleteSqlFile(String path) {
		String filePath = FileUtil.append(FileUtil.getWebClassesPath(), DEFAULT_SQL_PATH, path);
		File file = new File(filePath);
		if (file.isFile()) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (File f : files) {
					deleteSqlFile(FileUtil.append(path, f.getName()));
				}
			}
			file.delete();
		}
	}
}
