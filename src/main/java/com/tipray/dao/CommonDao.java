package com.tipray.dao;

import java.util.List;
import java.util.Map;

import com.tipray.core.annotation.MyBatisAnno;

/**
 * CommonDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface CommonDao {
	/**
	 * 获取数据库数据存储目录
	 * 
	 * @return
	 */
    String getDBDatadir();

	/**
	 * 统计数据库中表的数量
	 * 
	 * @return
	 */
    Integer countTables();

	/**
	 * 判断表是否存在
	 * 
	 * @return
	 */
    Integer countTableByName(String tableName);

	/**
	 * 统计表的数据量
	 * 
	 * @param tableName
	 * @return
	 */
    Integer countTableRow(String tableName);

	/**
	 * 查找数据库中所有的表名
	 * 
	 * @return
	 */
    List<String> findAllTables();

	/**
	 * 根据表名，查询右模糊匹配的表名集合
	 * 
	 * @param tableName
	 * @return
	 */
    List<String> findTables(String tableName);

	/**
	 * 删除表
	 * 
	 * @param table
	 */
    void removeTable(String table);

	/**
	 * 清除表数据
	 * 
	 * @param table
	 */
    void clearTable(String table);

	/**
	 * 执行update的SQL
	 * 
	 * @param sql
	 */
    void executeUpdate(String sql);

	/**
	 * 备份表数据
	 * 
	 * @param params
	 */
    void backupTable(Map<String, String> params);
}
