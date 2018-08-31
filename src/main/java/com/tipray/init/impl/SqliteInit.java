package com.tipray.init.impl;

import com.tipray.constant.CenterConst;
import com.tipray.constant.SqliteFileConst;
import com.tipray.constant.SqliteSqlConst;
import com.tipray.init.AbstractInitialization;
import com.tipray.util.JDBCUtil;
import com.tipray.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;

/**
 * Sqlite数据库初始化
 *
 * @author chenlong
 * @version 1.0  2018-01-08
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
     * @param sqliteDbName sqlite数据库名称
     */
    private void initSqliteDb(String sqliteDbName) {
        if (StringUtil.isEmpty(CenterConst.SQLITE_FILE_PATH)) {
            logger.warn("sqlite数据库文件存放路径未配置！");
            return;
        }
        String pathname = new StringBuffer(CenterConst.SQLITE_FILE_PATH).append('/')
                .append(sqliteDbName).append(".db").toString();
        File file = new File(pathname);
        if (file.exists()) {
            logger.info("sqlite数据库{}已存在！", sqliteDbName);
            return;
        }
        createSqliteDb(sqliteDbName);
    }

    /**
     * 创建sqlite数据库
     *
     * @param sqliteDbName sqlite数据库名称
     */
    private void createSqliteDb(String sqliteDbName) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        try {
            createSqliteDb(jdbcUtil, sqliteDbName);
            logger.info("初始化sqlite数据库{}完成！", sqliteDbName);
        } catch (Exception e) {
            logger.error("初始化sqlite数据库{}异常！\n{}", sqliteDbName, e.toString());
        } finally {
            jdbcUtil.close();
        }
    }

    /**
     * 创建sqlite数据库
     *
     * @param jdbcUtil     JDBCUtil
     * @param sqliteDbName sqlite数据库名称
     * @throws SQLException
     */
    public void createSqliteDb(JDBCUtil jdbcUtil, String sqliteDbName) throws SQLException {
        switch (sqliteDbName) {
            case SqliteFileConst.URGENT_CARD:
                jdbcUtil.createSqliteConnection(sqliteDbName);
                jdbcUtil.createPrepareStatement(SqliteSqlConst.CREATE_URGENT_CARD_SQL);
                break;
            case SqliteFileConst.MANAGE_CARD:
                jdbcUtil.createSqliteConnection(sqliteDbName);
                jdbcUtil.createPrepareStatement(SqliteSqlConst.CREATE_MANAGE_CARD_SQL);
                break;
            case SqliteFileConst.IN_OUT_CARD:
                jdbcUtil.createSqliteConnection(sqliteDbName);
                jdbcUtil.createPrepareStatement(SqliteSqlConst.CREATE_IN_OUT_CARD_SQL);
                break;
            case SqliteFileConst.IN_OUT_DEV:
                jdbcUtil.createSqliteConnection(sqliteDbName);
                jdbcUtil.createPrepareStatement(SqliteSqlConst.CREATE_IN_OUT_DEV_SQL);
                break;
            case SqliteFileConst.OIL_DEPOT:
                jdbcUtil.createSqliteConnection(sqliteDbName);
                jdbcUtil.createPrepareStatement(SqliteSqlConst.CREATE_OIL_DEPOT_SQL);
                break;
            default:
                return;
        }
        jdbcUtil.executeUpdate();
        jdbcUtil.createPrepareStatement(SqliteSqlConst.CREATE_VERSION_SQL);
        jdbcUtil.executeUpdate();
        jdbcUtil.createPrepareStatement(SqliteSqlConst.INIT_VERSION_SQL);
        jdbcUtil.executeUpdate();
        jdbcUtil.commit();
    }
}
