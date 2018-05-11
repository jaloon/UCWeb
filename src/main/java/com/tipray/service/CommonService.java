package com.tipray.service;

import java.util.List;

/**
 * CommonService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface CommonService {
	/**
	 * 获取数据库数据存储目录
	 * 
	 * @return
	 */
    String getDBDatadir();

	/**
	 * 判断数据库中是否有表存在
	 * 
	 * @return
	 */
    boolean isTablesExist();

	/**
	 * 判断指定表是否存在
	 * 
	 * @return
	 */
    boolean isTableExist(String tableName);

	/**
	 * 删除表
	 * 
	 * @param table
	 * @return
	 */
    boolean removeTable(String table);

	/**
	 * 根据表名，查询右模糊匹配的表名集合
	 * 
	 * @param tableName
	 * @return
	 */
    List<String> findTables(String tableName);

	/**
	 * 执行update sql
	 * 
	 * @param sql
	 */
    void executeUpdate(String sql);

	/**
	 * 将某张表的数据导出到指定的文件中
	 * 
	 * @param table
	 * @param outFile
	 *            //输出文件完整路径
	 * @param ids
	 */
    void backupTable(String table, String outFile, String ids);

	/**
	 * 删除表数据
	 * 
	 * @param string
	 */
    void clearTable(String... tables);
}
