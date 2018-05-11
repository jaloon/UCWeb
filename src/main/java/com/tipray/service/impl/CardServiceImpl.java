package com.tipray.service.impl;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Card;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.SqliteFileConst;
import com.tipray.constant.TerminalConfigBitMarkConst;
import com.tipray.constant.DatabaseOperateTypeEnum;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.VehicleParamVerDao;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.dao.CardDao;
import com.tipray.dao.GasStationDao;
import com.tipray.dao.OilDepotDao;
import com.tipray.service.CardService;
import com.tipray.util.FtpUtil;
import com.tipray.util.JDBCUtil;

/**
 * 卡管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "SQLException", "Exception" })
@Service("cardService")
public class CardServiceImpl implements CardService {
	private static Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);
	/** 操作sqlite应急卡信息 */
	private static final JDBCUtil JDBC_UTIL_URGENT = new JDBCUtil();
	/** 操作sqlite管理卡信息 */
	private static final JDBCUtil JDBC_UTIL_MANAGE = new JDBCUtil();
	/** 操作sqlite出入库卡信息 */
	private static final JDBCUtil JDBC_UTIL_IN_OUT = new JDBCUtil();
	/** 应急卡 */
	private static final Integer TYPE_URGENT = 1;
	/** 管理卡 */
	private static final Integer TYPE_MANAGE = 6;
	/** 入库卡 */
	private static final Integer TYPE_IN = 2;
	/** 出库卡 */
	private static final Integer TYPE_OUT = 3;
	/** 出入库卡 */
	private static final Integer TYPE_IN_OUT = 4;
	
	@Resource
	private NioUdpServer udpServer;
	
	@Resource
	private CardDao cardDao;
	@Resource
	private OilDepotDao oilDepotDao;
	@Resource
	private GasStationDao gasStationDao;
	@Resource
	private VehicleParamVerDao vehicleParamVerDao;

	@Override
	public Card addCard(Card card) {
		if (card != null) {
			cardDao.add(card);
			setCardInSqliteDb(card, DatabaseOperateTypeEnum.INSERT);
		}
		return card;
	}

	@Override
	public Card updateCard(Card card) {
		if (card != null) {
			Card cardInDb = new Card();
			if (card.getType() == TYPE_URGENT || card.getType() == TYPE_MANAGE) {
				cardInDb = cardDao.getById(card.getId());
			}
			cardDao.update(card);
			if (!card.getDirector().equals(cardInDb.getDirector())) {
				setCardInSqliteDb(card, DatabaseOperateTypeEnum.UPDATE);
			}
		}
		return card;
	}

	@Override
	public void deleteCardById(Long id) {
		Card card = cardDao.getById(id);
		Long cardId = card.getCardId();
		cardDao.delete(id);
		cardDao.deleteOilDepotCardByCardId(cardId);
		cardDao.deleteGasStationCardByCardId(cardId);
		setCardInSqliteDb(card, DatabaseOperateTypeEnum.DELETE);
	}

	@Override
	public Map<String, Object> getCardById(Long id) {
		if (id != null) {
			Card card = cardDao.getById(id);
			Long cardId = card.getCardId();
			List<OilDepot> oilDepots = oilDepotDao.findOilDepotsByCardId(cardId);
			List<GasStation> gasStations = gasStationDao.findGasStationsByCardId(cardId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("card", card);
			map.put("oilDepots", oilDepots);
			map.put("gasStations", gasStations);
			return map;
		}
		return null;
	}

	@Override
	public List<Card> findByOilDepotId(Long oilDepotId) {
		List<Card> cards = oilDepotId == null ? null : cardDao.findByOilDepotId(oilDepotId);
		return cards;
	}

	@Override
	public List<Card> findByGasStationId(Long gasStationId) {
		List<Card> cards = gasStationId == null ? null : cardDao.findByGasStationId(gasStationId);
		return cards;
	}

	@Override
	public List<Card> findAllCards() {
		return cardDao.findAll();
	}

	@Override
	public long countCard(Card card) {
		return cardDao.count(card);
	}

	@Override
	public List<Card> findByPage(Card card, Page page) {
		return cardDao.findByPage(card, page);
	}

	@Override
	public GridPage<Card> findCardsForPage(Card card, Page page) {
		long records = countCard(card);
		List<Card> list = findByPage(card, page);
		return new GridPage<Card>(list, records, page.getPageId(), page.getRows(), list.size(), card);
	}

	@Override
	public Card isCardExist(Long cardId) {
		return cardDao.getByCardId(cardId);
	}

	@Override
	public List<Long> findUnusedCard(Integer cardType, Long oilDepotId, Long gasStationId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cardType", cardType);
		map.put("oilDepotId", oilDepotId);
		map.put("gasStationId", gasStationId);
		return cardDao.findUnusedCards(map);
	}

	/**
	 * 向sqlite数据库添加、修改或删除卡信息
	 * 
	 * @param cardType
	 *            卡类型
	 * @param sqlType
	 *            数据库操作类型（增删改）
	 */
	private void setCardInSqliteDb(Card card, DatabaseOperateTypeEnum sqlType) {
		Integer cardType = card.getType();
		try {
			String sql = null;
			byte commonConfig = 0;
			if (cardType == TYPE_URGENT) {
				JDBC_UTIL_URGENT.createSqliteConnection(SqliteFileConst.URGENT_CARD);
				switch (sqlType) {
				case INSERT:
					sql = "INSERT INTO tbl_emergency_card(owner_name, card_id) VALUES(?, ?)";
					break;
				case UPDATE:
					sql = "UPDATE tbl_emergency_card SET owner_name = ? WHERE card_id = ?";
					break;
				case DELETE:
					sql = "DELETE FROM tbl_emergency_card WHERE card_id = ?";
					break;
				default:
					break;
				}
				statementExecute(JDBC_UTIL_URGENT, sql, card, sqlType);
				setParamVer(JDBC_UTIL_URGENT, SqliteFileConst.URGENT_CARD);
				FtpUtil.upload(SqliteFileConst.URGENT_CARD_DB_FILE);
				commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_1_URGENT_CARD;
			} else if (cardType == TYPE_MANAGE) {
				JDBC_UTIL_MANAGE.createSqliteConnection(SqliteFileConst.MANAGE_CARD);
				switch (sqlType) {
				case INSERT:
					sql = "INSERT INTO tbl_management_card(owner_name, card_id) VALUES(?, ?)";
					break;
				case UPDATE:
					sql = "UPDATE tbl_management_card SET owner_name = ? WHERE card_id = ?";
					break;
				case DELETE:
					sql = "DELETE FROM tbl_management_card WHERE card_id = ?";
					break;
				default:
					break;
				}
				statementExecute(JDBC_UTIL_MANAGE, sql, card, sqlType);
				setParamVer(JDBC_UTIL_MANAGE, SqliteFileConst.MANAGE_CARD);
				FtpUtil.upload(SqliteFileConst.MANAGE_CARD_DB_FILE);
				commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_2_MANAGE_CARD;
			} else {
				boolean belongInOut = cardType == TYPE_IN || cardType == TYPE_OUT || cardType == TYPE_IN_OUT;
				if (belongInOut) {
					JDBC_UTIL_IN_OUT.createSqliteConnection(SqliteFileConst.IN_OUT_CARD);
					if (sqlType.equals(DatabaseOperateTypeEnum.DELETE)) {
						sql = "DELETE FROM tbl_in_out_card WHERE card_id = ?";
						int i = statementExecute(JDBC_UTIL_IN_OUT, sql, card, sqlType);
						if (i > 0) {
							setParamVer(JDBC_UTIL_IN_OUT, SqliteFileConst.IN_OUT_CARD);
							FtpUtil.upload(SqliteFileConst.IN_OUT_CARD_DB_FILE);
							commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_3_IN_OUT_CARD;
						}
					}
				}
			}
			executeAsynUdp(commonConfig);
		} catch (SQLException e) {
			logger.error("sqlite更新卡相关数据库文件异常！\n{}", e.toString());
			rollback(cardType);
		} finally {
			close(cardType);
		}
	}

	/**
	 * sqlite卡信息SQL执行
	 * 
	 * @param jdbcUtil
	 *            JDBCUtil对象
	 * @param sql
	 *            SQL语句
	 * @param card
	 *            卡信息
	 * @param sqlType
	 *            数据库操作类型（增删改）
	 * @return int 增删改几条记录
	 * @throws SQLException
	 */
	private int statementExecute(JDBCUtil jdbcUtil, String sql, Card card, DatabaseOperateTypeEnum sqlType) throws SQLException {
		jdbcUtil.createPrepareStatement(sql);
		if (sqlType.equals(DatabaseOperateTypeEnum.DELETE)) {
			jdbcUtil.setLong(1, card.getCardId());
		} else {
			jdbcUtil.setString(1, card.getDirector());
			jdbcUtil.setLong(2, card.getCardId());
		}
		return jdbcUtil.executeUpdate();
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
	 * 数据回滚
	 * 
	 * @param cardType
	 *            卡类型
	 */
	private void rollback(Integer cardType) {
		if (cardType == TYPE_URGENT) {
			JDBC_UTIL_URGENT.rollback();
		} else if (cardType == TYPE_MANAGE) {
			JDBC_UTIL_MANAGE.rollback();
		} else {
			JDBC_UTIL_IN_OUT.rollback();
		}
	}

	/**
	 * 关闭资源
	 * 
	 * @param cardType
	 *            卡类型
	 */
	private void close(Integer cardType) {
		if (cardType == TYPE_URGENT) {
			JDBC_UTIL_URGENT.close();
		} else if (cardType == TYPE_MANAGE) {
			JDBC_UTIL_MANAGE.close();
		} else {
			JDBC_UTIL_IN_OUT.close();
		}
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

}
