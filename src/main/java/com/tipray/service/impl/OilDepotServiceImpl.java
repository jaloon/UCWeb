package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.ResponseMsg;
import com.tipray.bean.Station;
import com.tipray.bean.VehicleParamVer;
import com.tipray.bean.baseinfo.Card;
import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.CardTypeConst;
import com.tipray.constant.PageActionMode;
import com.tipray.constant.SqliteFileConst;
import com.tipray.constant.SqliteSqlConst;
import com.tipray.constant.TerminalConfigBitMarkConst;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.CardDao;
import com.tipray.dao.DeviceDao;
import com.tipray.dao.GasStationDao;
import com.tipray.dao.InOutReaderDao;
import com.tipray.dao.OilDepotDao;
import com.tipray.dao.VehicleParamVerDao;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.OilDepotService;
import com.tipray.util.CoverRegionUtil;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.FtpUtil;
import com.tipray.util.JDBCUtil;
import com.tipray.util.JSONUtil;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.StringUtil;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 油库管理业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Service("oilDepotService")
public class OilDepotServiceImpl implements OilDepotService {
    private static final Logger logger = LoggerFactory.getLogger(OilDepotServiceImpl.class);
    @Resource
    private NioUdpServer udpServer;
    @Resource
    private OilDepotDao oilDepotDao;
    @Resource
    private InOutReaderDao inOutReaderDao;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private CardDao cardDao;
    @Resource
    private VehicleParamVerDao vehicleParamVerDao;
    @Resource
    private GasStationDao gasStationDao;

    @Transactional
    @Override
    public void addOilDepots(List<OilDepot> oilDepots) throws ServiceException {
        oilDepots.parallelStream().forEach(depot -> setCover(depot));
        oilDepotDao.addOilDepots(oilDepots);
        List<OilDepot> list = oilDepotDao.findRecentOilDepotsBySize(oilDepots.size());
        JDBCUtil jdbcUtil = new JDBCUtil();
        try {
            jdbcUtil.createSqliteConnection(SqliteFileConst.OIL_DEPOT);
            jdbcUtil.createPrepareStatement(SqliteSqlConst.INSERT_OIL_DEPOT_SQL);
            for (OilDepot oilDepot : list) {
                setSqliteOilDepotInsertParams(jdbcUtil, oilDepot);
            }
            setParamVer(jdbcUtil, SqliteFileConst.OIL_DEPOT);
            jdbcUtil.commit();
        } catch (SQLException e) {
            jdbcUtil.rollback();
            throw new ServiceException("批量导入：sqlite批量添加油库异常！", e);
        } finally {
            jdbcUtil.close();
        }
        FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
        executeAsynUdp(TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_5_OIL_DEPOT);
    }

    @Transactional
    @Override
    public OilDepot addOilDepot(OilDepot oilDepot) throws ServiceException {
        if (oilDepot != null) {
            String officialId = oilDepot.getOfficialId();
            if (StringUtil.isEmpty(officialId)) {
                throw new IllegalArgumentException("油库编号为空！");
            }
            String name = oilDepot.getName();
            if (StringUtil.isEmpty(name)) {
                throw new IllegalArgumentException("油库名称为空！");
            }
            String abbr = oilDepot.getAbbr();
            if (StringUtil.isEmpty(abbr)) {
                throw new IllegalArgumentException("油库简称为空！");
            }
            if (isOilDepotExist(oilDepot)) {
                throw new IllegalArgumentException("油库已存在！");
            }
            setCover(oilDepot);
            List<Long> invalidIds = oilDepotDao.findInvalidOilDepot(oilDepot);
            int size = invalidIds.size();
            if (size == 0) {
                oilDepotDao.add(oilDepot);
            } else if (size == 1) {
                oilDepot.setId(invalidIds.get(0));
                oilDepotDao.update(oilDepot);
            } else {
                oilDepotDao.deleteByIds(invalidIds);
                oilDepotDao.add(oilDepot);
            }
            JDBCUtil jdbcUtil = new JDBCUtil();
            try {
                jdbcUtil.createSqliteConnection(SqliteFileConst.OIL_DEPOT);
                jdbcUtil.createPrepareStatement(SqliteSqlConst.INSERT_OIL_DEPOT_SQL);
                setSqliteOilDepotInsertParams(jdbcUtil, oilDepot);
                setParamVer(jdbcUtil, SqliteFileConst.OIL_DEPOT);
                jdbcUtil.commit();
                FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
                executeAsynUdp(TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_5_OIL_DEPOT);
            } catch (SQLException e) {
                jdbcUtil.rollback();
                throw new ServiceException("sqlite添加油库信息异常！", e);
            } finally {
                jdbcUtil.close();
            }
        }
        return oilDepot;
    }

    /**
     * 设置sqlite中新增油库的参数
     *
     * @param jdbcUtil JDBCUtil
     * @param oilDepot 油库信息
     * @throws SQLException
     */
    private void setSqliteOilDepotInsertParams(JDBCUtil jdbcUtil, OilDepot oilDepot) throws SQLException {
        jdbcUtil.setLong(1, oilDepot.getId());
        jdbcUtil.setString(2, oilDepot.getAbbr());
        jdbcUtil.setFloat(3, oilDepot.getLongitude());
        jdbcUtil.setFloat(4, oilDepot.getLatitude());
        jdbcUtil.setInt(5, oilDepot.getRadius());
        jdbcUtil.setBytes(6, oilDepot.getCover());
        jdbcUtil.executeUpdate();
    }

    @Transactional
    @Override
    public OilDepot updateOilDepot(OilDepot oilDepot, String readersJson, String cardIds) throws Exception {
        if (oilDepot != null) {
            setCover(oilDepot);
            Long id = oilDepot.getId();
            OilDepot oilDepotInDb = oilDepotDao.getById(id);
            setRegion(oilDepotInDb);
            // sqlite油库信息是否需要更新
            boolean isUpdateOfSqliteOilDepot = !oilDepot.getAbbr().equals(oilDepotInDb.getAbbr())
                    || !oilDepot.getLongitude().equals(oilDepotInDb.getLongitude())
                    || !oilDepot.getLatitude().equals(oilDepotInDb.getLatitude())
                    || !oilDepot.getRadius().equals(oilDepotInDb.getRadius())
                    || !oilDepot.getCoverRegion().equals(oilDepotInDb.getCoverRegion());
            oilDepotDao.update(oilDepot);

            List<InOutReader> inOutReadersInDb = inOutReaderDao.findByOilDepotId(id);
            int dbReaderNum = inOutReadersInDb.size();
            List<InOutReader> inOutReadersOfWeb = new ArrayList<>();
            int webReaderNum = 0;
            if (StringUtil.isNotEmpty(readersJson)) {
                inOutReadersOfWeb = JSONUtil.parseToList(readersJson, InOutReader.class);
                webReaderNum = inOutReadersOfWeb.size();
            }

            // 出入库读卡器是否需要更新
            boolean isUpdateOfInOutDev = dbReaderNum != webReaderNum;
            if (!isUpdateOfInOutDev && webReaderNum > 0) {
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
            List<Long> inOutCardIdsInDb = cardDao.findInOutCardIdsByOilDepotId(id);
            int dbInOutCardNum = inOutCardIdsInDb.size();
            List<Card> inOutCardsOfWeb = new ArrayList<>();
            int webInOutCardNum = 0;
            boolean isUpdateOfInOutCard = dbInOutCardNum != webInOutCardNum;
            oilDepotDao.deleteOilDepotCardsById(id);
            if (StringUtil.isNotEmpty(cardIds)) {
                String[] cardIdStrArray = cardIds.split(",");
                List<Map<String, Object>> list = new ArrayList<>();
                for (String string : cardIdStrArray) {
                    Long cardId = Long.parseLong(string, 10);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("cardId", cardId);
                    list.add(map);
                    Card card = cardDao.getByCardId(cardId);
                    int cardType = card.getType();
                    if (cardType == CardTypeConst.CARD_TYPE_2_IN
                            || cardType == CardTypeConst.CARD_TYPE_3_OUT
                            || cardType == CardTypeConst.CARD_TYPE_4_INOUT) {
                        inOutCardsOfWeb.add(card);
                        if (!isUpdateOfInOutCard && !inOutCardIdsInDb.contains(cardId)) {
                            isUpdateOfInOutCard = true;
                        }
                    }
                }
                oilDepotDao.addOilDepotCardsByIdAndCardIds(list);
                webInOutCardNum = inOutCardsOfWeb.size();
                if (!isUpdateOfInOutCard) {
                    isUpdateOfInOutCard = dbInOutCardNum != webInOutCardNum;
                }

            }

            byte commonConfig = 0;
            JDBCUtil jdbcUtilOil = null;
            JDBCUtil jdbcUtilCard = null;
            JDBCUtil jdbcUtilDev = null;
            try {
                if (isUpdateOfSqliteOilDepot) {
                    jdbcUtilOil = new JDBCUtil();
                    jdbcUtilOil.createSqliteConnection(SqliteFileConst.OIL_DEPOT);
                    jdbcUtilOil.createPrepareStatement(SqliteSqlConst.UPDATE_OIL_DEPOT_SQL);
                    jdbcUtilOil.setString(1, oilDepot.getAbbr());
                    jdbcUtilOil.setFloat(2, oilDepot.getLongitude());
                    jdbcUtilOil.setFloat(3, oilDepot.getLatitude());
                    jdbcUtilOil.setInt(4, oilDepot.getRadius());
                    jdbcUtilOil.setBytes(5, oilDepot.getCover());
                    jdbcUtilOil.setLong(6, oilDepot.getId());
                    jdbcUtilOil.executeUpdate();
                    setParamVer(jdbcUtilOil, SqliteFileConst.OIL_DEPOT);
                }

                if (isUpdateOfInOutDev) {
                    if (dbReaderNum > 0) {
                        StringBuffer dbReaderIds = new StringBuffer();
                        for (InOutReader reader : inOutReadersInDb) {
                            dbReaderIds.append(',').append(reader.getDevId());
                        }
                        dbReaderIds.deleteCharAt(0);
                        inOutReaderDao.deleteByOilDepotId(id);
                        deviceDao.updateDevicesUse(dbReaderIds.toString(), 0);
                    }
                    if (webReaderNum > 0) {
                        StringBuffer webReaderIds = new StringBuffer();
                        for (InOutReader reader : inOutReadersOfWeb) {
                            webReaderIds.append(',').append(reader.getDevId());
                        }
                        webReaderIds.deleteCharAt(0);
                        inOutReaderDao.addReaderList(inOutReadersOfWeb);
                        deviceDao.updateDevicesUse(webReaderIds.toString(), 1);
                    }
                    jdbcUtilDev = setInOutDev(jdbcUtilDev, oilDepot, inOutReadersOfWeb);
                }

                if (isUpdateOfInOutCard) {
                    jdbcUtilCard = setInOutCard(jdbcUtilCard, oilDepot, inOutCardsOfWeb);
                }

                // 提交
                if (isUpdateOfSqliteOilDepot) {
                    jdbcUtilOil.commit();
                    FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
                    commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_5_OIL_DEPOT;
                }
                if (isUpdateOfInOutDev) {
                    jdbcUtilDev.commit();
                    FtpUtil.upload(SqliteFileConst.IN_OUT_DEV_DB_FILE);
                    commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_4_IN_OUT_DEV;
                }
                if (isUpdateOfInOutCard) {
                    jdbcUtilCard.commit();
                    FtpUtil.upload(SqliteFileConst.IN_OUT_CARD_DB_FILE);
                    commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_3_IN_OUT_CARD;
                }

            } catch (Exception e) {
                if (jdbcUtilOil != null) {
                    jdbcUtilOil.rollback();
                }
                if (jdbcUtilCard != null) {
                    jdbcUtilCard.rollback();
                }
                if (jdbcUtilDev != null) {
                    jdbcUtilDev.rollback();
                }
                throw new ServiceException(e);
            } finally {
                if (jdbcUtilOil != null) {
                    jdbcUtilOil.close();
                }
                if (jdbcUtilCard != null) {
                    jdbcUtilCard.close();
                }
                if (jdbcUtilDev != null) {
                    jdbcUtilDev.close();
                }
            }
            executeAsynUdp(commonConfig);
        }
        return oilDepot;
    }

    @Transactional
    @Override
    public void deleteOilDepotById(Long id) throws ServiceException {
        OilDepot oilDepot = new OilDepot();
        oilDepot.setId(id);
        oilDepotDao.delete(id);
        Integer cardNum = cardDao.countInOutCardsByOilDepotId(id);
        if (cardNum != null && cardNum > 0) {
            oilDepotDao.deleteOilDepotCardsById(id);
        }
        List<Integer> readerIdList = inOutReaderDao.findReaderIdsByOilDepotId(id);
        if (!EmptyObjectUtil.isEmptyList(readerIdList)) {
            StringBuffer readerIds = new StringBuffer();
            for (Integer readerId : readerIdList) {
                readerIds.append(',').append(readerId);
            }
            inOutReaderDao.deleteByOilDepotId(id);
            deviceDao.updateDevicesUse(readerIds.deleteCharAt(0).toString(), 0);
        }
        JDBCUtil jdbcUtilOil = null;
        JDBCUtil jdbcUtilCard = null;
        JDBCUtil jdbcUtilDev = null;
        try {
            jdbcUtilOil = new JDBCUtil();
            jdbcUtilOil.createSqliteConnection(SqliteFileConst.OIL_DEPOT);
            jdbcUtilOil.createPrepareStatement(SqliteSqlConst.DELETE_OIL_DEPOT_BY_MYSQL_ID_SQL);
            jdbcUtilOil.setLong(1, oilDepot.getId());
            jdbcUtilOil.executeUpdate();
            setParamVer(jdbcUtilOil, SqliteFileConst.OIL_DEPOT);

            byte commonConfig = TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_5_OIL_DEPOT;

            if (cardNum != null && cardNum > 0) {
                jdbcUtilCard = setInOutCard(jdbcUtilCard, oilDepot, null);
            }
            if (!EmptyObjectUtil.isEmptyList(readerIdList)) {
                jdbcUtilDev = setInOutDev(jdbcUtilDev, oilDepot, null);
            }

            // 提交
            if (jdbcUtilDev != null) {
                jdbcUtilDev.commit();
                FtpUtil.upload(SqliteFileConst.IN_OUT_DEV_DB_FILE);
                commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_4_IN_OUT_DEV;
            }
            if (jdbcUtilCard != null) {
                jdbcUtilCard.commit();
                FtpUtil.upload(SqliteFileConst.IN_OUT_CARD_DB_FILE);
                commonConfig |= TerminalConfigBitMarkConst.COMMON_CONFIG_BIT_3_IN_OUT_CARD;
            }
            jdbcUtilOil.commit();
            FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
            executeAsynUdp(commonConfig);
        } catch (Exception e) {
            if (jdbcUtilOil != null) {
                jdbcUtilOil.rollback();
            }
            if (jdbcUtilCard != null) {
                jdbcUtilCard.rollback();
            }
            if (jdbcUtilDev != null) {
                jdbcUtilDev.rollback();
            }
            throw new ServiceException(e);
        } finally {
            if (jdbcUtilOil != null) {
                jdbcUtilOil.close();
            }
            if (jdbcUtilCard != null) {
                jdbcUtilCard.close();
            }
            if (jdbcUtilDev != null) {
                jdbcUtilDev.close();
            }
        }
    }

    @Override
    public OilDepot getOilDepotById(Long id) {
        OilDepot oilDepot = id == null ? null : oilDepotDao.getById(id);
        if (oilDepot != null) {
            setRegion(oilDepot);
        }
        return oilDepot;
    }

    @Override
    public List<OilDepot> findByName(String oildepotName) {
        return StringUtil.isEmpty(oildepotName) ? null : oilDepotDao.findByName(oildepotName);
    }

    @Override
    public Long getIdByOfficialId(String officialId) {
        return StringUtil.isEmpty(officialId) ? null : oilDepotDao.getIdByOfficialId(officialId);
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
        return new GridPage<>(list, records, page, oilDepot);
    }

    @Override
    public boolean isOilDepotExist(OilDepot oilDepot) {
        if (oilDepot == null) {
            return false;
        }
        Integer count = oilDepotDao.countValidOilDepot(oilDepot);
        return count != null && count > 0;
    }

    @Override
    public ResponseMsg getExistInfo(String officialId, String name, String abbr, String mode) {
        if (StringUtil.isEmpty(mode)) {
            throw new IllegalArgumentException("参数不完整！");
        }
        if (StringUtil.isEmpty(officialId)) {
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 11, "油库编号为空！");
        }
        if (StringUtil.isEmpty(name)) {
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 21, "油库名称为空！");
        }
        if (StringUtil.isEmpty(abbr)) {
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 31, "油库简称为空！");
        }
        if (PageActionMode.EDIT.equalsIgnoreCase(mode)) {
            OilDepot oilDepot = oilDepotDao.getByOfficialId(officialId);
            if (!oilDepot.getName().equals(name)) {
                Integer nameCount = oilDepotDao.countValidOilDepotByName(name);
                if (nameCount != null && nameCount > 0) {
                    return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 22, "油库名称已存在！");
                }
            }
            if (!oilDepot.getAbbr().equals(abbr)) {
                Integer abbrCount = oilDepotDao.countValidOilDepotByAbbr(abbr);
                if (abbrCount != null && abbrCount > 0) {
                    return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 32, "油库简称已存在！");
                }
            }
            return ResponseMsgUtil.success();
        }
        if (PageActionMode.ADD.equalsIgnoreCase(mode)) {
            Integer idCount = oilDepotDao.countValidOilDepotByOfficialId(officialId);
            if (idCount != null && idCount > 0) {
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 12, "油库编号已存在！");
            }
        }
        if (StringUtil.isNotEmpty(officialId)) {
            Integer idCount = oilDepotDao.countValidOilDepotByOfficialId(officialId);
            if (idCount != null && idCount > 0) {
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 12, "油库编号已存在！");
            }
            Integer nameCount = oilDepotDao.countValidOilDepotByName(name);
            if (nameCount != null && nameCount > 0) {
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 22, "油库名称已存在！");
            }
            Integer abbrCount = oilDepotDao.countValidOilDepotByAbbr(abbr);
            if (abbrCount != null && abbrCount > 0) {
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 32, "油库简称已存在！");
            }
            return ResponseMsgUtil.success();
        }
        throw new IllegalArgumentException("参数无效！");
    }

    @Override
    public List<InOutReader> findUnusedReaders() {
        return inOutReaderDao.findUnusedReader();
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
     * 设置sqlite出入库读卡器信息
     *
     * @param jdbcUtil          JDBCUtil
     * @param oilDepot          油库信息
     * @param inOutReadersOfWeb Web页面传递的出入库读卡器列表
     * @return {@link JDBCUtil}
     * @throws SQLException
     */
    private JDBCUtil setInOutDev(JDBCUtil jdbcUtil, OilDepot oilDepot, List<InOutReader> inOutReadersOfWeb) throws SQLException {
        if (jdbcUtil == null) {
            jdbcUtil = new JDBCUtil();
        }
        jdbcUtil.createSqliteConnection(SqliteFileConst.IN_OUT_DEV);
        jdbcUtil.createPrepareStatement(SqliteSqlConst.DELETE_IN_OUT_DEV_BY_STATION_ID_SQL);
        jdbcUtil.setLong(1, oilDepot.getId());
        jdbcUtil.executeUpdate();
        if (!EmptyObjectUtil.isEmptyList(inOutReadersOfWeb)) {
            jdbcUtil.createPrepareStatement(SqliteSqlConst.INSERT_IN_OUT_DEV_SQL);
            for (InOutReader inOutReader : inOutReadersOfWeb) {
                jdbcUtil.setInt(1, inOutReader.getDevId());
                jdbcUtil.setInt(2, inOutReader.getType());
                jdbcUtil.setLong(3, oilDepot.getId());
                jdbcUtil.executeUpdate();
            }
        }
        setParamVer(jdbcUtil, SqliteFileConst.IN_OUT_DEV);
        return jdbcUtil;
    }

    /**
     * 设置sqlite出入库卡信息
     *
     * @param jdbcUtil       JDBCUtil
     * @param oilDepot       油库信息
     * @param inOutCardOfWeb Web页面传递的出入库卡列表
     * @return {@link JDBCUtil}
     * @throws SQLException
     */
    private JDBCUtil setInOutCard(JDBCUtil jdbcUtil, OilDepot oilDepot, List<Card> inOutCardOfWeb) throws SQLException {
        if (jdbcUtil == null) {
            jdbcUtil = new JDBCUtil();
        }
        jdbcUtil.createSqliteConnection(SqliteFileConst.IN_OUT_CARD);
        jdbcUtil.createPrepareStatement(SqliteSqlConst.DELETE_IN_OUT_CARD_BY_STATION_ID_SQL);
        jdbcUtil.setLong(1, oilDepot.getId());
        jdbcUtil.executeUpdate();
        if (!EmptyObjectUtil.isEmptyList(inOutCardOfWeb)) {
            jdbcUtil.createPrepareStatement(SqliteSqlConst.INSERT_IN_OUT_CARD_SQL);
            for (Card card : inOutCardOfWeb) {
                jdbcUtil.setLong(1, card.getCardId());
                jdbcUtil.setInt(2, card.getType());
                jdbcUtil.setLong(3, oilDepot.getId());
                jdbcUtil.executeUpdate();
            }
        }
        setParamVer(jdbcUtil, SqliteFileConst.IN_OUT_CARD);
        return jdbcUtil;
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
        if (commonConfig == 0) {
            return;
        }
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

    @Override
    public List<Map<String, Object>> getIdAndNameOfAllOilDepots() {
        return oilDepotDao.findIdAndNameOfAllOilDepots();
    }

    @Override
    public Map<String, Object> getIdAndNameOfAllOilDepotsAndGasStations() {
        Map<String, Object> map = new HashMap<>();
        map.put("depots", oilDepotDao.findIdAndNameOfAllOilDepots());
        map.put("stations", gasStationDao.findIdAndNameOfAllGasStations());
        return map;
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
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("depotVer", dbDepotVer);
        map.put("stationVer", dbStationVer);
        if (!dbDepotVer.equals(depotVer)) {
            List<Station> depots = oilDepotDao.findStationsForApp();
            depots.forEach(depot -> {
                if (depot.getCover() != null) {
                    depot.setCover_lonlatlist_region(CoverRegionUtil.coverToPoints(depot.getCover()));
                }
            });
            map.put("depots", depots);
        }
        if (!dbStationVer.equals(stationVer)) {
            List<Station> stations = gasStationDao.findStationsForApp();
            stations.forEach(station -> {
                if (station.getCover() != null) {
                    station.setCover_lonlatlist_region(CoverRegionUtil.coverToPoints(station.getCover()));
                }
            });
            map.put("stations", stations);
        }
        return map;
    }

    @Override
    public Integer barrierCount(Long oilDepotId) {
        if (oilDepotId == null) {
            return 0;
        }
        Integer barrierCount = oilDepotDao.barrierCount(oilDepotId);
        return barrierCount == null ? 0 : barrierCount;
    }

}