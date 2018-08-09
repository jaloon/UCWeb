package com.tipray.cache;

import com.tipray.util.JDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 车辆与所属运输公司对应关系缓存
 * 
 * @author chenlong
 * @version 1.0 2018-01-23
 *
 */
public class VehicleCompanyRelationCache {
	private static final Logger logger = LoggerFactory.getLogger(VehicleCompanyRelationCache.class);
	private static final Map<Long, String> CAR_COMPANY_RELATION_MAP = new HashMap<Long, String>();
	private static final JDBCUtil JDBC_UTIL = new JDBCUtil();
	static {
		try {
			JDBC_UTIL.dbConfig("jdbc.properties");
			JDBC_UTIL.createConnection();
			String sql = "SELECT c.id car, tc.name com FROM tbl_vehicle c, tbl_trans_company tc WHERE c.trans_company_id = tc.id";
			JDBC_UTIL.createPrepareStatement(sql);
			JDBC_UTIL.setResultSet(JDBC_UTIL.getPreparedStatement().executeQuery());
			while (JDBC_UTIL.getResultSet().next()) {
				Long carId = JDBC_UTIL.getResultSet().getLong("car");
				String comName = JDBC_UTIL.getResultSet().getString("com");
				CAR_COMPANY_RELATION_MAP.put(carId, comName);
			}
		} catch (SQLException e) {
			logger.error("初始化车辆与所属运输公司对应关系缓存异常！", e);
		} finally {
			JDBC_UTIL.close();
		}
	}

	/**
	 * 根据车辆ID获取车辆所属公司名称
	 * 
	 * @param carId
	 *            车辆ID
	 * @return 车辆所属公司名称
	 */
	public synchronized static String getComOfCar(Long carId) {
		if (CAR_COMPANY_RELATION_MAP.containsKey(carId)) {
			return CAR_COMPANY_RELATION_MAP.get(carId);
		}
		try {
			JDBC_UTIL.dbConfig("jdbc.properties");
			JDBC_UTIL.createConnection();
			String sql = "SELECT tc.name com FROM tbl_vehicle c, tbl_trans_company tc WHERE c.trans_company_id = tc.id and c.id = ?";
			JDBC_UTIL.createPrepareStatement(sql);
			JDBC_UTIL.setLong(1, carId);
			JDBC_UTIL.setResultSet(JDBC_UTIL.getPreparedStatement().executeQuery());
			while (JDBC_UTIL.getResultSet().next()) {
				String comName = JDBC_UTIL.getResultSet().getString("com");
				CAR_COMPANY_RELATION_MAP.put(carId, comName);
				return comName;
			}
		} catch (SQLException e) {
			logger.error("根据车辆ID获取车辆所属公司名称异常！", e);
		} finally {
			JDBC_UTIL.close();
		}
		return "";
	}

	private VehicleCompanyRelationCache() {
	}
}
