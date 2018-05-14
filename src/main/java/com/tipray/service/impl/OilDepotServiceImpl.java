package com.tipray.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Card;
import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.DatabaseOperateTypeEnum;
import com.tipray.constant.SqliteFileConst;
import com.tipray.constant.TerminalConfigBitMarkConst;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.*;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.OilDepotService;
import com.tipray.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 油库管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "SQLException", "Exception" })
@Service("oilDepotService")
public class OilDepotServiceImpl implements OilDepotService {
	private static final Logger logger = LoggerFactory.getLogger(OilDepotServiceImpl.class);
	/** 操作sqlite油库信息 */
	private static final JDBCUtil JDBC_UTIL_OIL = new JDBCUtil();
	/** 操作sqlite出入库卡信息 */
	private static final JDBCUtil JDBC_UTIL_CARD = new JDBCUtil();
	/** 操作sqlite出入库读卡器信息 */
	private static final JDBCUtil JDBC_UTIL_DEV = new JDBCUtil();
	/** 批量添加结果 */
	private boolean batchAdd = true;
	/** 批量导入异常 */
	private SQLException sqlException;

	@Resource
	private NioUdpServer udpServer;

	@Resource
	private OilDepotDao oilDepotDao;
	@Resource
	private InOutReaderDao inOutReaderDao;
	@Resource
	private CardDao cardDao;
	@Resource
	private VehicleParamVerDao vehicleParamVerDao;
	@Resource
	private GasStationDao gasStationDao;

	@Override
	public void addOilDepots(List<OilDepot> oilDepots) throws ServiceException {
		oilDepots.parallelStream().forEach(depot -> setCover(depot));
		oilDepotDao.addOilDepots(oilDepots);
		List<OilDepot> list = oilDepotDao.findRecentOilDepotsBySize(oilDepots.size());
		JDBC_UTIL_OIL.createSqliteConnection(SqliteFileConst.OIL_DEPOT);
		String sql = "INSERT INTO tbl_oildepot(official_id,name,longitude,latitude,radius,cover) VALUES(?,?,?,?,?,?)";
		JDBC_UTIL_OIL.createPrepareStatement(sql);
		list.parallelStream().forEach(oilDepot -> {
			try {
				JDBC_UTIL_OIL.setLong(1, oilDepot.getId());
				JDBC_UTIL_OIL.setString(2, oilDepot.getAbbr());
				JDBC_UTIL_OIL.setFloat(3, oilDepot.getLongitude());
				JDBC_UTIL_OIL.setFloat(4, oilDepot.getLatitude());
				JDBC_UTIL_OIL.setInt(5, oilDepot.getRadius());
				JDBC_UTIL_OIL.setBytes(6, oilDepot.getCover());
				JDBC_UTIL_OIL.executeUpdate();
			} catch (SQLException e) {
				batchAdd = false;
				sqlException = e;
				logger.error("批量导入：sqlite添加油库{}异常！\n{}", oilDepot.getName(), e.toString());
				JDBC_UTIL_OIL.rollback();
			}
		});
		if (!batchAdd) {
			throw new ServiceException("批量导入：sqlite添加油库信息异常！", sqlException);
		}
		try {
			setParamVer(JDBC_UTIL_OIL, SqliteFileConst.OIL_DEPOT);
		} catch (SQLException e) {
			logger.error("批量导入：更新油库版本异常！\n{}", e.toString());
			JDBC_UTIL_OIL.rollback();
			throw new ServiceException("批量导入：更新油库版本异常！", e);
		} finally {
			JDBC_UTIL_OIL.close();
		}
		FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
		executeAsynUdp(TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_5_OIL_DEPOT);
	}

	@Override
	public OilDepot addOilDepot(OilDepot oilDepot) throws ServiceException {
		if (oilDepot != null) {
			try {
				setCover(oilDepot);
				oilDepotDao.add(oilDepot);
				oilDepot.setId(oilDepotDao.getIdByOfficialId(oilDepot.getOfficialId()));
				String sql = "INSERT INTO tbl_oildepot(official_id,name,longitude,latitude,radius,cover) VALUES(?,?,?,?,?,?)";
				setOilDepot(sql, oilDepot, DatabaseOperateTypeEnum.INSERT);
				FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
				executeAsynUdp(TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_5_OIL_DEPOT);
			} catch (SQLException e) {
				JDBC_UTIL_OIL.rollback();
				throw new ServiceException("sqlite添加油库信息异常！", e);
			} finally {
				JDBC_UTIL_OIL.close();
			}
		}
		return oilDepot;
	}

	@Override
	public OilDepot updateOilDepot(OilDepot oilDepot, String readersJson, String cardIds) throws Exception {
		if (oilDepot != null) {
			setCover(oilDepot);
			Long id = oilDepot.getId();
			OilDepot oilDepotInDb = oilDepotDao.getById(id);
			setRegion(oilDepotInDb);
			// sqlite油库信息是否需要更新
			boolean isUpdateOfSqliteOilDepot = oilDepot.getLongitude() != oilDepotInDb.getLongitude()
					|| oilDepot.getLatitude() != oilDepotInDb.getLatitude()
					|| !oilDepot.getCoverRegion().equals(oilDepotInDb.getCoverRegion());
			oilDepotDao.update(oilDepot);

			List<InOutReader> inOutReadersInDb = inOutReaderDao.findByOilDepotId(id);
			List<InOutReader> inOutReadersOfWeb = JSONUtil.parse(readersJson,
					new TypeReference<List<InOutReader>>() {});
			// 出入库读卡器是否需要更新
			boolean isUpdateOfInOutDev = inOutReadersInDb.size() != inOutReadersOfWeb.size();
			if (!isUpdateOfInOutDev) {
				Map<Integer, Integer> mapInDb = new HashMap<>();
				Map<Integer, Integer> mapOfWeb = new HashMap<>();
				List<Integer> devIdsInDb = new ArrayList<>();
				for (InOutReader readerInDb : inOutReadersInDb) {
					Integer devId = readerInDb.getDevId();
					mapInDb.put(devId, readerInDb.getType());
					devIdsInDb.add(devId);
				}
				for (InOutReader readerOfWeb : inOutReadersOfWeb) {
					Integer readerId = readerOfWeb.getDevId();
					mapOfWeb.put(readerId, readerOfWeb.getType());
					if (!devIdsInDb.contains(readerId)) {
						isUpdateOfInOutDev = true;
						break;
					}
				}
				if (!isUpdateOfInOutDev) {
					for (Integer devId : devIdsInDb) {
						if (!mapInDb.get(devId).equals(mapOfWeb.get(devId))) {
							isUpdateOfInOutDev = true;
							break;
						}
					}
				}
			}

			// 出入库卡信息是否需要更新
			boolean isUpdateOfInOutCard = false;
			List<Card> inOutCardsInDb = cardDao.findInOutCardsByOilDepotId(id);
			List<Long> inOutCardIdsInDb = new ArrayList<>();
			for (Card card : inOutCardsInDb) {
				inOutCardIdsInDb.add(card.getCardId());
			}
			List<Card> inOutCardOfWeb = new ArrayList<Card>();
			String typesOfInOutCard = "2,3,4";// 出入库卡类型
			if (StringUtil.isNotEmpty(cardIds)) {
				String[] cardIdStrArray = cardIds.split(",");
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (String string : cardIdStrArray) {
					Long cardId = Long.parseLong(string, 10);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", id);
					map.put("cardId", cardId);
					list.add(map);
					Card card = cardDao.getByCardId(cardId);
					if (typesOfInOutCard.indexOf(Integer.toString(card.getType())) >= 0) {
						inOutCardOfWeb.add(card);
						if (!inOutCardsInDb.contains(card)) {
							isUpdateOfInOutCard = true;
						}
					}
				}
				oilDepotDao.deleteOilDepotCardsById(id);
				oilDepotDao.addOilDepotCardsByIdAndCardIds(list);
				if (!isUpdateOfInOutCard) {
					isUpdateOfInOutCard = inOutCardsInDb.size() != inOutCardOfWeb.size();
				}

			}

			byte commonConfig = 0;
			if (isUpdateOfSqliteOilDepot) {
				try {
					String sql = "UPDATE tbl_oildepot SET longitude = ?,latitude = ?,radius = ?,cover = ? WHERE official_id = ?";
					setOilDepot(sql, oilDepot, DatabaseOperateTypeEnum.UPDATE);
					FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
					commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_5_OIL_DEPOT;
				} catch (SQLException e) {
					JDBC_UTIL_OIL.rollback();
					throw new ServiceException("sqlite更新油库信息异常！", e);
				} finally {
					JDBC_UTIL_OIL.close();
				}
			}

			if (isUpdateOfInOutDev) {
				try {
					inOutReaderDao.deleteByOilDepotId(id);
					inOutReaderDao.addReaderList(inOutReadersOfWeb);
					setInOutDev(oilDepot, inOutReadersOfWeb);
					FtpUtil.upload(SqliteFileConst.IN_OUT_DEV_DB_FILE);
					commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_4_IN_OUT_DEV;
				} catch (SQLException e) {
					JDBC_UTIL_DEV.rollback();
					throw new ServiceException("sqlite更新油库设备异常！", e);
				} finally {
					JDBC_UTIL_DEV.close();
				}
			}

			if (isUpdateOfInOutCard) {
				try {
					setInOutCard(oilDepot, inOutCardOfWeb);
					FtpUtil.upload(SqliteFileConst.IN_OUT_CARD_DB_FILE);
					commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_3_IN_OUT_CARD;
				} catch (SQLException e) {
					JDBC_UTIL_CARD.rollback();
					throw new ServiceException("sqlite更新出入库卡信息异常！", e);
				} finally {
					JDBC_UTIL_CARD.close();
				}
			}

			executeAsynUdp(commonConfig);
		}
		return oilDepot;
	}

	@Override
	public void deleteOilDepotById(Long id) throws ServiceException {
		// OilDepot oilDepot = oilDepotDao.getById(id);
		OilDepot oilDepot = new OilDepot();
		oilDepot.setId(id);
		oilDepotDao.delete(id);
		oilDepotDao.deleteOilDepotCardsById(id);
		try {
			String sql = "DELETE FROM tbl_oildepot WHERE official_id = ?";
			setOilDepot(sql, oilDepot, DatabaseOperateTypeEnum.DELETE);
			byte commonConfig = 0;
			if (!EmptyObjectUtil.isEmptyList(cardDao.findInOutCardsByOilDepotId(id))) {
				try {
					setInOutDev(oilDepot, null);
					FtpUtil.upload(SqliteFileConst.IN_OUT_DEV_DB_FILE);
					commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_4_IN_OUT_DEV;
				} catch (SQLException e) {
					JDBC_UTIL_DEV.rollback();
					throw new ServiceException("sqlite删除油库设备异常！", e);
				} finally {
					JDBC_UTIL_DEV.close();
				}
			}
			if (!EmptyObjectUtil.isEmptyList(inOutReaderDao.findByOilDepotId(id))) {
				try {
					setInOutCard(oilDepot, null);
					FtpUtil.upload(SqliteFileConst.IN_OUT_CARD_DB_FILE);
					commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_3_IN_OUT_CARD;
				} catch (SQLException e) {
					JDBC_UTIL_CARD.rollback();
					throw new ServiceException("sqlite删除出入库卡异常！", e);
				} finally {
					JDBC_UTIL_CARD.close();
				}
			}
			FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
			commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_5_OIL_DEPOT;
			executeAsynUdp(commonConfig);
		} catch (SQLException e) {
			JDBC_UTIL_OIL.rollback();
			throw new ServiceException("sqlite删除油库信息异常！", e);
		} finally {
			JDBC_UTIL_OIL.close();
		}
	}

	@Override
	public OilDepot getOilDepotById(Long id) {
		OilDepot oilDepot = id == null ? null : oilDepotDao.getById(id);
		setRegion(oilDepot);
		return oilDepot;
	}

	@Override
	public List<OilDepot> findByName(String oildepotName) {
		return oilDepotDao.findByName(oildepotName);
	}

	@Override
	public List<OilDepot> findAllOilDepots() {
		return oilDepotDao.findAll();
	}

	@Override
	public long countOilDepot(OilDepot oilDepot) {
		return oilDepotDao.count(oilDepot);
	}

	@Override
	public List<OilDepot> findByPage(OilDepot oilDepot, Page page) {
		return oilDepotDao.findByPage(oilDepot, page);
	}

	@Override
	public GridPage<OilDepot> findOilDepotsForPage(OilDepot oilDepot, Page page) {
		long records = countOilDepot(oilDepot);
		List<OilDepot> list = findByPage(oilDepot, page);
		return new GridPage<OilDepot>(list, records, page.getPageId(), page.getRows(), list.size(), oilDepot);
	}

	@Override
	public OilDepot isOilDepotExist(OilDepot oilDepot) {
		String officialId = oilDepot.getOfficialId();
		String name = oilDepot.getName();
		if (StringUtil.isNotEmpty(officialId)) {
			return oilDepotDao.getByOfficialId(officialId);
		}
		if (StringUtil.isNotEmpty(name)) {
			return oilDepotDao.getByName(name);
		}
		return null;
	}

	@Override
	public List<InOutReader> findUnusedReaders() {
		List<InOutReader> list = inOutReaderDao.findUnusedReader();
		return list;
	}

	/**
	 * 设置占地范围 （经纬度集合字符串，用逗号,相隔 ）
	 * 
	 * @param oilDepot
	 */
	private void setRegion(OilDepot oilDepot) {
		byte[] cover = oilDepot.getCover();
		String region = CoverRegionUtil.coverToRegion(cover);
		oilDepot.setCoverRegion(region);
	}

	/**
	 * 设置占地范围 （经纬度浮点值字节数组）
	 * 
	 * @param oilDepot
	 */
	private void setCover(OilDepot oilDepot) {
		String region = oilDepot.getCoverRegion();
		byte[] cover = CoverRegionUtil.regionToCover(region);
		oilDepot.setCover(cover);
	}

	/**
	 * 设置sqlite油库信息
	 * 
	 * @param sql
	 *            增删sqlite油库信息SQL语句
	 * @param oilDepot
	 *            油库信息
	 * @param sqlType
	 *            数据库操作类型（增删改）
	 * @throws SQLException
	 */
	private void setOilDepot(String sql, OilDepot oilDepot, DatabaseOperateTypeEnum sqlType) throws SQLException {
		JDBC_UTIL_OIL.createSqliteConnection(SqliteFileConst.OIL_DEPOT);
		JDBC_UTIL_OIL.createPrepareStatement(sql);
		switch (sqlType) {
		case INSERT:
			JDBC_UTIL_OIL.setLong(1, oilDepot.getId());
			JDBC_UTIL_OIL.setString(2, oilDepot.getAbbr());
			JDBC_UTIL_OIL.setFloat(3, oilDepot.getLongitude());
			JDBC_UTIL_OIL.setFloat(4, oilDepot.getLatitude());
			JDBC_UTIL_OIL.setInt(5, oilDepot.getRadius());
			JDBC_UTIL_OIL.setBytes(6, oilDepot.getCover());
			break;
		case UPDATE:
			JDBC_UTIL_OIL.setFloat(1, oilDepot.getLongitude());
			JDBC_UTIL_OIL.setFloat(2, oilDepot.getLatitude());
			JDBC_UTIL_OIL.setInt(3, oilDepot.getRadius());
			JDBC_UTIL_OIL.setBytes(4, oilDepot.getCover());
			JDBC_UTIL_OIL.setLong(5, oilDepot.getId());
			break;
		case DELETE:
			JDBC_UTIL_OIL.setLong(1, oilDepot.getId());
			break;
		default:
			break;
		}
		JDBC_UTIL_OIL.executeUpdate();
		setParamVer(JDBC_UTIL_OIL, SqliteFileConst.OIL_DEPOT);
	}

	/**
	 * 设置sqlite出入库读卡器信息
	 * 
	 * @param oilDepot
	 *            油库信息
	 * @param inOutReadersOfWeb
	 *            Web页面传递的出入库读卡器列表
	 * @throws SQLException
	 */
	private void setInOutDev(OilDepot oilDepot, List<InOutReader> inOutReadersOfWeb) throws SQLException {
		JDBC_UTIL_DEV.createSqliteConnection(SqliteFileConst.IN_OUT_DEV);
		String sql1 = "DELETE FROM tbl_in_out_dev WHERE oildepot_official_id = ?";
		JDBC_UTIL_DEV.createPrepareStatement(sql1);
		JDBC_UTIL_DEV.setLong(1, oilDepot.getId());
		JDBC_UTIL_DEV.executeUpdate();
		if (!EmptyObjectUtil.isEmptyList(inOutReadersOfWeb)) {
			String sql2 = "INSERT INTO tbl_in_out_dev(dev_id,type,oildepot_official_id) VALUES(?,?,?)";
			for (InOutReader inOutReader : inOutReadersOfWeb) {
				JDBC_UTIL_DEV.createPrepareStatement(sql2);
				JDBC_UTIL_DEV.setInt(1, inOutReader.getDevId());
				JDBC_UTIL_DEV.setInt(2, inOutReader.getType());
				JDBC_UTIL_DEV.setLong(3, oilDepot.getId());
				JDBC_UTIL_DEV.executeUpdate();
			}
		}
		setParamVer(JDBC_UTIL_DEV, SqliteFileConst.IN_OUT_DEV);
	}

	/**
	 * 设置sqlite出入库卡信息
	 * 
	 * @param oilDepot
	 *            油库信息
	 * @param inOutCardOfWeb
	 *            Web页面传递的出入库卡列表
	 * @throws SQLException
	 */
	private void setInOutCard(OilDepot oilDepot, List<Card> inOutCardOfWeb) throws SQLException {
		JDBC_UTIL_CARD.createSqliteConnection(SqliteFileConst.IN_OUT_CARD);
		String sql1 = "DELETE FROM tbl_in_out_card WHERE oildepot_official_id = ?";
		JDBC_UTIL_CARD.createPrepareStatement(sql1);
		JDBC_UTIL_CARD.setLong(1, oilDepot.getId());
		JDBC_UTIL_CARD.executeUpdate();
		if (!EmptyObjectUtil.isEmptyList(inOutCardOfWeb)) {
			String sql2 = "INSERT INTO tbl_in_out_card(card_id,type,oildepot_official_id) VALUES(?,?,?)";
			for (Card card : inOutCardOfWeb) {
				JDBC_UTIL_CARD.createPrepareStatement(sql2);
				JDBC_UTIL_CARD.setLong(1, card.getCardId());
				JDBC_UTIL_CARD.setInt(2, card.getType());
				JDBC_UTIL_CARD.setLong(3, oilDepot.getId());
				JDBC_UTIL_CARD.executeUpdate();
			}
		}
		setParamVer(JDBC_UTIL_CARD, SqliteFileConst.IN_OUT_CARD);
	}

	/**
	 * 设置车台公共参数版本
	 * 
	 * @param jdbcUtil
	 *            JDBCUtil对象
	 * @param param
	 *            车台公共参数名称
	 * @throws SQLException
	 */
	private void setParamVer(JDBCUtil jdbcUtil, String param) throws SQLException {
		long ver = System.currentTimeMillis();
		String sql = "UPDATE tbl_version SET version = ?";
		jdbcUtil.createPrepareStatement(sql);
		jdbcUtil.setLong(1, ver);
		jdbcUtil.executeUpdate();
		jdbcUtil.commit();
		vehicleParamVerDao.updateVerByParam(param, ver);
	}

	/**
	 * 执行UDP通信
	 * 
	 * @param commonConfig
	 *            {@link byte} 公共配置文件列表，用位标识下列更新文件：<br>
	 *            1 emergency_card_info.db<br>
	 *            2 management_card_info.db<br>
	 *            3 in_out_oildepot_card_info.db<br>
	 *            4 in_out_oildepot_dev_info.db<br>
	 *            5 oildepot_info.db
	 */
	private void executeAsynUdp(byte commonConfig) {
		ByteBuffer src = SendPacketBuilder.buildProtocol0x1201(commonConfig);
		boolean isSend = udpServer.send(src);
		if (!isSend) {
			logger.error("UDP发送数据异常！");
		}
		addTimeoutTask(src);
	}

	/**
	 * 添加超时任务
	 * 
	 * @param src
	 *            {@link ByteBuffer} 待发送数据包
	 * @param cacheId
	 *            {@link Integer} 缓存ID
	 */
	private void addTimeoutTask(ByteBuffer src) {
		short serialNo = SerialNumberCache.getSerialNumber(UdpBizId.TERMINAL_COMMON_CONFIG_UPDATE_REQUEST);
		int cacheId = AsynUdpCommCache.buildCacheId(UdpBizId.TERMINAL_COMMON_CONFIG_UPDATE_REQUEST, serialNo);
		new TimeOutTask(src, cacheId).executeInfoIssueTask();
	}

	@Override
	public List<Map<String, Object>> getIdAndNameOfAllOilDepots() {
		return oilDepotDao.findIdAndNameOfAllOilDepots();
	}

	@Override
	public Map<String, Object> getIdAndNameOfAllOilDepotsAndGasStations(Long depotVer, Long stationVer) {
		if (depotVer == null || stationVer == null) {
		    return null;
        }
		Map<String, BigInteger> verMap = vehicleParamVerDao.getVersionsOfOilDepotAndGasStation();
        if (verMap == null) {
            throw new IllegalArgumentException("数据库数据异常！");
        }
		Long dbDepotVer = verMap.get("depot_ver").longValue();
		Long dbStationVer = verMap.get("station_ver").longValue();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("depotVer", dbDepotVer);
		map.put("stationVer", dbStationVer);
		if (!dbDepotVer.equals(depotVer)) {
			List<Map<String, Object>> depots = oilDepotDao.findIdAndNameOfAllOilDepots();
			map.put("depots", depots);
		}
		if (!dbStationVer.equals(stationVer)) {
			List<Map<String, Object>> stations = gasStationDao.findIdAndNameOfAllGasStations();
			map.put("stations", stations);
		}
		return map;
	}

}