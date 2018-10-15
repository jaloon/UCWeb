package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.VehicleParamVer;
import com.tipray.bean.baseinfo.Card;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.CardTypeConst;
import com.tipray.constant.DatabaseOperateTypeEnum;
import com.tipray.constant.SqliteFileConst;
import com.tipray.constant.SqliteSqlConst;
import com.tipray.constant.TerminalConfigBitMarkConst;
import com.tipray.dao.CardDao;
import com.tipray.dao.GasStationDao;
import com.tipray.dao.OilDepotDao;
import com.tipray.dao.VehicleParamVerDao;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.CardService;
import com.tipray.util.FtpUtil;
import com.tipray.util.JDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡管理业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Service("cardService")
public class CardServiceImpl implements CardService {
    private static Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);
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

    @Transactional
    @Override
    public Card addCard(Card card) throws SQLException {
        if (card != null) {
            Long cardId = card.getCardId();
            if (cardId == null) {
                throw new IllegalArgumentException("卡ID为空！");
            }
            Integer count = cardDao.countCardByCardId(cardId);
            if (count == null || count == 0) {
                cardDao.add(card);
            } else if (count == 1) {
                cardDao.updateByCardId(card);
            } else {
                cardDao.deleteByCardId(cardId);
                cardDao.add(card);
            }
            setCardInSqliteDb(card, DatabaseOperateTypeEnum.INSERT);
        }
        return card;
    }

    @Transactional
    @Override
    public Card updateCard(Card card) throws SQLException {
        if (card != null) {
            Card cardInDb = new Card();
            int cardType = card.getType();
            if (cardType == CardTypeConst.CARD_TYPE_1_URGENT || cardType == CardTypeConst.CARD_TYPE_6_MANAGE) {
                cardInDb = cardDao.getById(card.getId());
            }
            cardDao.update(card);
            if (!card.getDirector().equals(cardInDb.getDirector())) {
                setCardInSqliteDb(card, DatabaseOperateTypeEnum.UPDATE);
            }
        }
        return card;
    }

    @Transactional
    @Override
    public void deleteCardById(Long id) throws SQLException {
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
            Map<String, Object> map = new HashMap<>();
            map.put("card", card);
            map.put("oilDepots", oilDepots);
            map.put("gasStations", gasStations);
            return map;
        }
        return null;
    }

    @Override
    public List<Card> findByOilDepotId(Long oilDepotId) {
        return oilDepotId == null ? null : cardDao.findByOilDepotId(oilDepotId);
    }

    @Override
    public List<Card> findByGasStationId(Long gasStationId) {
        return gasStationId == null ? null : cardDao.findByGasStationId(gasStationId);
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
        return new GridPage<>(list, records, page, card);
    }

    @Override
    public Card isCardExist(Long cardId) {
        return cardDao.getByCardId(cardId);
    }

    @Override
    public List<Long> findUnusedCard(Integer cardType) {
        return cardDao.findUnusedCards(cardType);
    }

    /**
     * 向sqlite数据库添加、修改或删除卡信息
     *
     * @param cardType 卡类型
     * @param sqlType  数据库操作类型（增删改）
     */
    private void setCardInSqliteDb(Card card, DatabaseOperateTypeEnum sqlType) throws SQLException {
        JDBCUtil jdbcUtil = new JDBCUtil();
        Integer cardType = card.getType();
        try {
            String sql = null;
            byte commonConfig = 0;
            if (cardType == CardTypeConst.CARD_TYPE_1_URGENT) {
                jdbcUtil.createSqliteConnection(SqliteFileConst.URGENT_CARD);
                switch (sqlType) {
                    case INSERT:
                        sql = SqliteSqlConst.INSERT_URGENT_CARD_SQL;
                        break;
                    case UPDATE:
                        sql = SqliteSqlConst.UPDATE_URGENT_CARD_SQL;
                        break;
                    case DELETE:
                        sql = SqliteSqlConst.DELETE_URGENT_CARD_BY_CARD_ID_SQL;
                        break;
                    default:
                        break;
                }
                statementExecute(jdbcUtil, sql, card, sqlType);
                setParamVer(jdbcUtil, SqliteFileConst.URGENT_CARD);
                FtpUtil.upload(SqliteFileConst.URGENT_CARD_DB_FILE);
                commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_1_URGENT_CARD;
            } else if (cardType == CardTypeConst.CARD_TYPE_6_MANAGE) {
                jdbcUtil.createSqliteConnection(SqliteFileConst.MANAGE_CARD);
                switch (sqlType) {
                    case INSERT:
                        sql = SqliteSqlConst.INSERT_MANAGE_CARD_SQL;
                        break;
                    case UPDATE:
                        sql = SqliteSqlConst.UPDATE_MANAGE_CARD_SQL;
                        break;
                    case DELETE:
                        sql = SqliteSqlConst.DELETE_MANAGE_CARD_BY_CARD_ID_SQL;
                        break;
                    default:
                        break;
                }
                statementExecute(jdbcUtil, sql, card, sqlType);
                setParamVer(jdbcUtil, SqliteFileConst.MANAGE_CARD);
                FtpUtil.upload(SqliteFileConst.MANAGE_CARD_DB_FILE);
                commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_2_MANAGE_CARD;
            } else {
                boolean belongInOut = cardType == CardTypeConst.CARD_TYPE_2_IN
                        || cardType == CardTypeConst.CARD_TYPE_3_OUT
                        || cardType == CardTypeConst.CARD_TYPE_4_INOUT;
                if (belongInOut) {
                    jdbcUtil.createSqliteConnection(SqliteFileConst.IN_OUT_CARD);
                    if (sqlType.equals(DatabaseOperateTypeEnum.DELETE)) {
                        sql = SqliteSqlConst.DELETE_IN_OUT_CARD_BY_CARD_ID_SQL;
                        int i = statementExecute(jdbcUtil, sql, card, sqlType);
                        if (i > 0) {
                            setParamVer(jdbcUtil, SqliteFileConst.IN_OUT_CARD);
                            FtpUtil.upload(SqliteFileConst.IN_OUT_CARD_DB_FILE);
                            commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_3_IN_OUT_CARD;
                        }
                    }
                }
            }
            executeAsynUdp(commonConfig);
        } catch (SQLException e) {
            jdbcUtil.rollback();
            throw new SQLException("sqlite更新卡相关数据库文件异常！", e);
        } finally {
            jdbcUtil.close();
        }
    }

    /**
     * sqlite卡信息SQL执行
     *
     * @param jdbcUtil JDBCUtil对象
     * @param sql      SQL语句
     * @param card     卡信息
     * @param sqlType  数据库操作类型（增删改）
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
     * @param jdbcUtil JDBCUtil对象
     * @param param    车台公共参数名称
     * @throws SQLException
     */
    private void setParamVer(JDBCUtil jdbcUtil, String param) throws SQLException {
        long ver = System.currentTimeMillis();
        jdbcUtil.createPrepareStatement(SqliteSqlConst.UPDATE_VERSION_SQL);
        jdbcUtil.setLong(1, ver);
        jdbcUtil.executeUpdate();
        jdbcUtil.commit();
        VehicleParamVer vehicleParamVer = vehicleParamVerDao.getByParam(param);
        if (vehicleParamVer == null) {
            vehicleParamVer = new VehicleParamVer();
            vehicleParamVer.setParam(param);
            vehicleParamVer.setVer(ver);
            vehicleParamVerDao.add(vehicleParamVer);
        } else {
            vehicleParamVerDao.updateVerByParam(param, ver);
        }
    }

    /**
     * 执行UDP通信
     *
     * @param commonConfig {@link byte} 公共配置文件列表，用位标识下列更新文件：<br>
     *                     1 emergency_card_info.db<br>
     *                     2 management_card_info.db<br>
     *                     3 in_out_oildepot_card_info.db<br>
     *                     4 in_out_oildepot_dev_info.db<br>
     *                     5 oildepot_info.db
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
     * @param src     {@link ByteBuffer} 待发送数据包
     * @param cacheId {@link Integer} 缓存ID
     */
    private void addTimeoutTask(ByteBuffer src) {
        short serialNo = SerialNumberCache.getSerialNumber(UdpBizId.TERMINAL_COMMON_CONFIG_UPDATE_REQUEST);
        int cacheId = AsynUdpCommCache.buildCacheId(UdpBizId.TERMINAL_COMMON_CONFIG_UPDATE_REQUEST, serialNo);
        new TimeOutTask(src, cacheId).executeInfoIssueTask();
    }
}