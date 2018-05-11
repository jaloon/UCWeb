package com.tipray.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * sql脚本执行工具类
 * @author chends
 *
 */
public class SqlScriptExcuteUtil {
	protected final static Logger logger = LoggerFactory.getLogger(SqlScriptExcuteUtil.class);

	public static List<String> loadSqlFile(String fileName) throws Exception {
		FileReader sqlFileIn = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(sqlFileIn);
		StringBuffer sqlSb = new StringBuffer();
		String str = null;
		while ((str = bufferedReader.readLine()) != null) {
			if (!str.startsWith("/*!") && !str.startsWith("--")
					&& str.indexOf("/*") <= 0 && str.trim().length() > 0) {
				sqlSb.append("\n" + str);
			}
		}
		bufferedReader.close();
		sqlFileIn.close();
		String[] sqls = sqlSb.toString().split(";");
		List<String> sqlList = new ArrayList<String>();
		for (int i = 0; i < sqls.length; i++) {
			sqlList.add(new String(sqls[i].getBytes(), StandardCharsets.UTF_8));
		}
		return sqlList;
	}
	
	public static void executeBatchSql(String sql)
			throws Exception {
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		executeBatchSql(sqls);
	}

	public static void executeBatchSql(List<String> sqls)
			throws Exception {
		if(sqls==null || sqls.size()==0){
			return;
		}
		Connection con = SpringBeanUtil.getSqlSessionFactory().openSession().getConnection();
		con.setAutoCommit(false);
		Statement statement = con.createStatement();
		try {
			executeBatchSql(sqls, statement);
			con.commit();
		} catch (Exception e) {
			logger.error("异常信息：\n{}", e.toString());
			con.rollback();
		} finally {
			statement.close();
			con.close();
		}
	}

	private static void executeBatchSql(List<String> sqls, Statement statement)
			throws Exception {
		for (int i = 0; i < sqls.size();) {
			String sql = sqls.get(i);
			i++;
			if (sql != null && !sql.trim().isEmpty()) {
				statement.addBatch(sql);
			}
			if (i % 50 == 0 || i == sqls.size()) {
				try {
					statement.executeBatch();
				} catch (Exception e) {
					logger.info("错误SQL：{}", sql);
					if (sql.toLowerCase().trim().indexOf("drop") == -1) {
						throw new Exception(sql, e);
					}
				}
			}
		}
	}
}
