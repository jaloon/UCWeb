package com.tipray.init.impl;

import com.tipray.constant.CenterConfigConst;
import com.tipray.constant.SqliteFileConst;
import com.tipray.init.AbstractInitialization;
import com.tipray.util.JDBCUtil;
import com.tipray.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Sqlite数据库初始化
 * 
 * @author         chenlong
 * @version        1.0  2018-01-08
 *
 */
public class SqliteInit extends AbstractInitialization {
	private static final Logger logger = LoggerFactory.getLogger(SqliteInit.class);

	@Override
	public void init() {
		initSqliteDb(SqliteFileConst.URGENT_CARD);
		initSqliteDb(SqliteFileConst.MANAGE_CARD);
		initSqliteDb(SqliteFileConst.IN_OUT_CARD);
		initSqliteDb(SqliteFileConst.IN_OUT_DEV);
		initSqliteDb(SqliteFileConst.OIL_DEPOT);
	}

	@Override
	public void update() {
		
	}
	
	/**
	 * 初始化sqlite数据库
	 * 
	 * @param sqliteDbName
	 *            sqlite数据库名称
	 */
	private void initSqliteDb(String sqliteDbName) {
		if (StringUtil.isNotEmpty(CenterConfigConst.SQLITE_FILE_PATH)) {
			String pathname = new StringBuffer(CenterConfigConst.SQLITE_FILE_PATH).append('/')
					.append(sqliteDbName).append(".db").toString();
			File file = new File(pathname);
			if (file.exists()) {
			    logger.info("sqlite数据库{}已存在！", sqliteDbName);
			    return;
            }
		}
		JDBCUtil jdbcUtil = new JDBCUtil();
		try {
			jdbcUtil.createSqliteConnection(sqliteDbName);
			String sql = null;
			switch (sqliteDbName) {
			case SqliteFileConst.URGENT_CARD:
				sql = "CREATE TABLE \"tbl_emergency_card\" (" + 
						"  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT," + 
						"  \"card_id\" integer NOT NULL," + 
						"  \"owner_name\" text(20) NOT NULL" + 
						");";		  
				break;
			case SqliteFileConst.MANAGE_CARD:
				sql = "CREATE TABLE \"tbl_management_card\" (" + 
						"  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT," + 
						"  \"card_id\" integer NOT NULL," + 
						"  \"owner_name\" text(20) NOT NULL" + 
						");";		  
				break;
			case SqliteFileConst.IN_OUT_CARD:
				sql = "CREATE TABLE \"tbl_in_out_card\" (" + 
						"  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT," + 
						"  \"card_id\" integer NOT NULL," + 
						"  \"type\" integer NOT NULL," +
						"  \"station_id\" integer NOT NULL," + 
						"  \"owner_id\" text(50)" + 
						");";		  
				break;
			case SqliteFileConst.IN_OUT_DEV:
				sql = "CREATE TABLE \"tbl_in_out_dev\" (" + 
						"  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT," + 
						"  \"dev_id\" integer NOT NULL," + 
						"  \"type\" integer NOT NULL," +
						"  \"station_id\" integer NOT NULL" + 
						");";		  
				break;
			case SqliteFileConst.OIL_DEPOT:
				sql = "CREATE TABLE \"tbl_oildepot\" (" + 
						"  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT," + 
						"  \"official_id\" integer NOT NULL," + 
						"  \"name\" text(20) NOT NULL," + 
						"  \"longitude\" real(4) NOT NULL," + 
						"  \"latitude\" real(4) NOT NULL," + 
						"  \"radius\" integer NOT NULL DEFAULT 0," + 
						"  \"cover\" blob" + 
						");";		  
				break;
			default:
				break;
			}
			jdbcUtil.createPrepareStatement(sql);
			jdbcUtil.executeUpdate();
			sql = "CREATE TABLE \"tbl_version\" (" + 
					"  \"version\" integer NOT NULL DEFAULT 0" +
					");";
			jdbcUtil.createPrepareStatement(sql);
			jdbcUtil.executeUpdate();
			sql = "INSERT INTO tbl_version ( version ) VALUES ( 0 );";
			jdbcUtil.createPrepareStatement(sql);
			jdbcUtil.executeUpdate();
			jdbcUtil.commit();
			logger.info("初始化sqlite数据库{}完成！", sqliteDbName);
		} catch (Exception e) {
			logger.error("初始化sqlite数据库{}异常！\n{}", sqliteDbName, e.toString());
		} finally {
			jdbcUtil.close();
		}
	}
}
