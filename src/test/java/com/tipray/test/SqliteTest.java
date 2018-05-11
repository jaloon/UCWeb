package com.tipray.test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.tipray.constant.SqliteFileConst;
import com.tipray.init.impl.SqliteInit;
import com.tipray.util.JDBCUtil;

/**
 * sqlite数据库测试
 * 
 * @author chenlong
 * @version 1.0 2018-01-08
 *
 */
public class SqliteTest {
	@Test
	public void test() {
		try {
			JDBCUtil jdbcUtil =new JDBCUtil("com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/db_user_center", "root", "root");
			jdbcUtil.createConnection();
			byte b = -127;
			byte[] bs = {-128};
			String sql = "insert into ver(ver,verb) values(?,hex(?))";
			jdbcUtil.createPrepareStatement(sql);
			jdbcUtil.setByte(1, b);
			jdbcUtil.setString(2, "-127");
			int i = jdbcUtil.executeUpdate();
			jdbcUtil.commit();
			System.out.println(i);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void strTest1() {
		System.out.println((byte)128);
		String str = "track={\"carNumber\":\"桂A12345\",\"biz\":\"track\",\"carStatus\":\"在途中\",\"lockStatusInfo\":\"AAAAAAAAAAA=\",\"latitude\":24.743252,\"alarm\":false,\"angle\":0,\"id\":1,\"velocity\":76,\"longitude\":118.31967}";
		int i = str.indexOf('=');
		String str1 = str.substring(0, i);
		String str2 = str.substring(i + 1, str.length());
		System.out.println("str : " + str);
		System.out.println("i   : " + i);
		System.out.println("str1: " + str1);
		System.out.println("str2: " + str2);
	}

	@Test
	public void strTest2() {
		String str = "newwindow=1&safe=strict&biw=1920&bih=949&ei=iAlXWvOaF8Gz0gStrZaYCQ&q=java";
		String[] objs = str.split("&");
		for (String string : objs) {
			String[] obj = string.split("=");
			System.out.println(obj[0] + ": " + obj[1]);
		}
	}

	@Test
	public void mapTest() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", 1);
		map.put("name", "aaa");
		int age = (int) map.get("age");
		System.out.println(age);
	}

	@Test
	public void sqliteTest() {
		JDBCUtil jdbcUtil = new JDBCUtil();
		try {
			jdbcUtil.createSqliteConnection("parm_version_info");
			// String sql = "DROP TABLE IF EXISTS \"tbl_version\";";
			// jdbcUtil.createPrepareStatement(sql);
			// jdbcUtil.getPreparedStatement().executeUpdate();
			String sql1 = "CREATE TABLE \"tbl_version\" (" + "\"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "\"name\" text(20) NOT NULL," + "\"version\" integer NOT NULL DEFAULT 0" + ")";
			jdbcUtil.createPrepareStatement(sql1);
			jdbcUtil.getPreparedStatement().executeUpdate();

			jdbcUtil.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				jdbcUtil.getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			jdbcUtil.close();
		}
	}

	@Test
	public void sqliteTest1() {
		JDBCUtil jdbcUtil = new JDBCUtil();
		try {
			jdbcUtil.createSqliteConnection("emergency_card_info");
			String sql1 = "CREATE TABLE \"tbl_emergency_card\" ("
					+ "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT," + "  \"card_id\" integer NOT NULL,"
					+ "  \"holder\" text(20) NOT NULL" + ");";
			jdbcUtil.createPrepareStatement(sql1);
			jdbcUtil.getPreparedStatement().executeUpdate();

			jdbcUtil.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				jdbcUtil.getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			jdbcUtil.close();
		}
	}

	@Test
	public void sqliteTest2() {
		JDBCUtil jdbcUtil = new JDBCUtil();
		try {
			jdbcUtil.createSqliteConnection("management_card_info");
			String sql1 = "CREATE TABLE \"tbl_management_card\" ("
					+ "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT," + "  \"card_id\" integer NOT NULL,"
					+ "  \"holder\" text(20) NOT NULL" + ");";
			jdbcUtil.createPrepareStatement(sql1);
			jdbcUtil.getPreparedStatement().executeUpdate();

			jdbcUtil.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				jdbcUtil.getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			jdbcUtil.close();
		}
	}

	@Test
	public void sqliteTest3() {
		JDBCUtil jdbcUtil = new JDBCUtil();
		try {
			jdbcUtil.createSqliteConnection("in_out_oildepot_card_info");
			String sql1 = "CREATE TABLE \"tbl_in_out_card\" (" + "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "  \"card_id\" integer NOT NULL," + "  \"oildepot_name\" text NOT NULL" + ");";
			jdbcUtil.createPrepareStatement(sql1);
			jdbcUtil.getPreparedStatement().executeUpdate();

			jdbcUtil.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				jdbcUtil.getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			jdbcUtil.close();
		}
	}

	@Test
	public void sqliteTest4() {
		JDBCUtil jdbcUtil = new JDBCUtil();
		try {
			jdbcUtil.createSqliteConnection("oildepot_info");
			String sql1 = "CREATE TABLE \"tbl_oildepot\" (" + "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "  \"official_id\" integer NOT NULL," + "  \"name\" text(20) NOT NULL,"
					+ "  \"longitude\" real NOT NULL," + "  \"latitude\" real NOT NULL," + "  \"cover\" blob,"
					+ "  \"in_reader_id\" integer NOT NULL," + "  \"out_reader_id\" integer NOT NULL" + ");";
			jdbcUtil.createPrepareStatement(sql1);
			jdbcUtil.getPreparedStatement().executeUpdate();

			jdbcUtil.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				jdbcUtil.getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			jdbcUtil.close();
		}
	}

	@Test
	public void sqliteTest5() {
		JDBCUtil jdbcUtil = new JDBCUtil();
		try {
			jdbcUtil.createSqliteConnection("gasstation_info");
			String sql1 = "CREATE TABLE \"tbl_gasstation\" (" + "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "  \"official_id\" integer NOT NULL," + "  \"name\" text(20) NOT NULL,"
					+ "  \"longitude\" real NOT NULL," + "  \"latitude\" real NOT NULL," + "  \"cover\" blob" + ");";
			jdbcUtil.createPrepareStatement(sql1);
			jdbcUtil.getPreparedStatement().executeUpdate();

			jdbcUtil.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				jdbcUtil.getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			jdbcUtil.close();
		}
	}

	@Test
	public void init() {
		try {
			new SqliteInit().init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void findOil() {
		JDBCUtil jdbcUtil = new JDBCUtil();
		try {
			jdbcUtil.createSqliteConnection(SqliteFileConst.OIL_DEPOT);
			String sql = "select * from tbl_oildepot";
			jdbcUtil.createPrepareStatement(sql);
			ResultSet resultSet = jdbcUtil.getPreparedStatement().executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();// 结果集的元信息（查询结果的字段名字，别名，字段类型）
			int count = rsmd.getColumnCount();// 获取结果集中的列数
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();// 一堆的对象
			while (resultSet.next()) {// 遍历行，一个对象
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= count; i++) {
					// 遍历每一列，一个属性
					String key = rsmd.getColumnLabel(i);// password,id,name 字段名字
					Object value = resultSet.getObject(i);
					map.put(key, value);
				}

				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			jdbcUtil.close();
		}
	}

}
