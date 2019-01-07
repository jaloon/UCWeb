package com.tipray.service.impl;

import com.tipray.bean.ResponseMsg;
import com.tipray.bean.sqlite.BaseSqlite;
import com.tipray.bean.sqlite.InOutCard;
import com.tipray.bean.sqlite.InOutDev;
import com.tipray.bean.sqlite.ManageCard;
import com.tipray.bean.sqlite.OildepotInfo;
import com.tipray.bean.sqlite.UrgentCard;
import com.tipray.constant.CenterConst;
import com.tipray.constant.SqliteFileConst;
import com.tipray.constant.SqliteSqlConst;
import com.tipray.core.CenterVariableConfig;
import com.tipray.dao.SqliteSyncDao;
import com.tipray.dao.VehicleParamVerDao;
import com.tipray.init.impl.SqliteInit;
import com.tipray.service.SqliteSyncService;
import com.tipray.util.FtpUtil;
import com.tipray.util.JDBCUtil;
import com.tipray.util.JSONUtil;
import com.tipray.util.OkHttpUtil;
import com.tipray.util.StringUtil;
import okhttp3.FormBody;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * sqlite文件同步业务层
 *
 * @author chenlong
 * @version 1.0 2018-08-22
 */
@Service("sqliteSyncService")
public class SqliteSyncServiceImpl implements SqliteSyncService {
    private static final Logger logger = LoggerFactory.getLogger(SqliteSyncServiceImpl.class);
    private static final String EMAIL_URL = new StringBuffer()
            .append(CenterConst.PLTONE_URL).append("/basic/email.do").toString();
    @Resource
    private SqliteSyncDao sqliteSyncDao;
    @Resource
    private VehicleParamVerDao vehicleParamVerDao;

    @Transactional
    @Override
    public void syncUrgentCard() throws SQLException {
        JDBCUtil jdbcUtilUrgentCard = new JDBCUtil();
        int flag = checkVersion(SqliteFileConst.URGENT_CARD, jdbcUtilUrgentCard);
        if (flag == 0) {
            return;
        }
        List<UrgentCard> urgentCardListOfMysql = sqliteSyncDao.findUrgentCard();
        if (flag == 2) {
            List<UrgentCard> urgentCardListOfSqlite = new ArrayList<>();
            try {
                ResultSet resultSet = jdbcUtilUrgentCard.query(SqliteSqlConst.QUERY_URGENT_CARD_SQL);
                UrgentCard urgentCard;
                while (resultSet.next()) {
                    urgentCard = new UrgentCard();
                    urgentCard.setCardId(resultSet.getLong(1));
                    urgentCard.setOwnerName(resultSet.getString(2));
                    urgentCardListOfSqlite.add(urgentCard);
                }
            } catch (SQLException e) {
                logger.error("查询sqlite中应急卡数据异常", e);
            }
            flag = checkList(urgentCardListOfMysql, urgentCardListOfSqlite);
        }
        if (flag == 1) {
            try {
                jdbcUtilUrgentCard.createPrepareStatement(SqliteSqlConst.DELETE_URGENT_CARD_SQL);
                jdbcUtilUrgentCard.executeUpdate();
                jdbcUtilUrgentCard.createPrepareStatement(SqliteSqlConst.INSERT_URGENT_CARD_SQL);
                for (UrgentCard urgentCard : urgentCardListOfMysql) {
                    jdbcUtilUrgentCard.setString(1, urgentCard.getOwnerName());
                    jdbcUtilUrgentCard.setLong(2, urgentCard.getCardId());
                    jdbcUtilUrgentCard.executeUpdate();
                }
                updateParamVer(jdbcUtilUrgentCard, SqliteFileConst.URGENT_CARD);
                logger.info("车台基础配置同步：应急卡更新成功！");
            } catch (SQLException e) {
                jdbcUtilUrgentCard.rollback();
                throw new SQLException("同步应急卡异常！", e);
            } finally {
                jdbcUtilUrgentCard.close();
            }
        } else {
            jdbcUtilUrgentCard.close();
            logger.info("车台基础配置同步：应急卡无更新！");
        }
        FtpUtil.upload(SqliteFileConst.URGENT_CARD_DB_FILE);
    }

    @Transactional
    @Override
    public void syncManageCard() throws SQLException {
        JDBCUtil jdbcUtilManageCard = new JDBCUtil();
        int flag = checkVersion(SqliteFileConst.MANAGE_CARD, jdbcUtilManageCard);
        if (flag == 0) {
            return;
        }
        List<ManageCard> manageCardListOfMysql = sqliteSyncDao.findManageCard();
        if (flag == 2) {
            List<ManageCard> manageCardListOfSqlite = new ArrayList<>();
            try {
                ResultSet resultSet = jdbcUtilManageCard.query(SqliteSqlConst.QUERY_MANAGE_CARD_SQL);
                ManageCard manageCard;
                while (resultSet.next()) {
                    manageCard = new ManageCard();
                    manageCard.setCardId(resultSet.getLong(1));
                    manageCard.setOwnerName(resultSet.getString(2));
                    manageCardListOfSqlite.add(manageCard);
                }
            } catch (SQLException e) {
                logger.error("查询sqlite中管理卡数据异常", e);
            }
            flag = checkList(manageCardListOfMysql, manageCardListOfSqlite);
        }
        if (flag == 1) {
            try {
                jdbcUtilManageCard.createPrepareStatement(SqliteSqlConst.DELETE_MANAGE_CARD_SQL);
                jdbcUtilManageCard.executeUpdate();
                jdbcUtilManageCard.createPrepareStatement(SqliteSqlConst.INSERT_MANAGE_CARD_SQL);
                for (ManageCard manageCard : manageCardListOfMysql) {
                    jdbcUtilManageCard.setString(1, manageCard.getOwnerName());
                    jdbcUtilManageCard.setLong(2, manageCard.getCardId());
                    jdbcUtilManageCard.executeUpdate();
                }
                updateParamVer(jdbcUtilManageCard, SqliteFileConst.MANAGE_CARD);
                logger.info("车台基础配置同步：管理卡更新成功！");
            } catch (SQLException e) {
                jdbcUtilManageCard.rollback();
                throw new SQLException("同步管理卡异常！", e);
            } finally {
                jdbcUtilManageCard.close();
            }
        } else {
            jdbcUtilManageCard.close();
            logger.info("车台基础配置同步：管理卡无更新！");
        }
        FtpUtil.upload(SqliteFileConst.MANAGE_CARD_DB_FILE);
    }

    @Transactional
    @Override
    public void syncInOutCard() throws SQLException {
        JDBCUtil jdbcUtilInOuCard = new JDBCUtil();
        int flag = checkVersion(SqliteFileConst.IN_OUT_CARD, jdbcUtilInOuCard);
        if (flag == 0) {
            return;
        }
        List<InOutCard> inOutCardListOfMysql = sqliteSyncDao.findInOutCard();
        if (flag == 2) {
            List<InOutCard> inOutCardListOfSqlite = new ArrayList<>();
            try {
                ResultSet resultSet = jdbcUtilInOuCard.query(SqliteSqlConst.QUERY_IN_OUT_CARD_SQL);
                InOutCard inOutCard;
                while (resultSet.next()) {
                    inOutCard = new InOutCard();
                    inOutCard.setCardId(resultSet.getLong(1));
                    inOutCard.setType(resultSet.getInt(2));
                    inOutCard.setStationId(resultSet.getLong(3));
                    inOutCard.setOwnerId(resultSet.getString(4));
                    inOutCardListOfSqlite.add(inOutCard);
                }
            } catch (SQLException e) {
                logger.error("查询sqlite中出入库卡数据异常", e);
            }
            flag = checkList(inOutCardListOfMysql, inOutCardListOfSqlite);
        }
        if (flag == 1) {
            try {
                jdbcUtilInOuCard.createPrepareStatement(SqliteSqlConst.DELETE_IN_OUT_CARD_SQL);
                jdbcUtilInOuCard.executeUpdate();
                jdbcUtilInOuCard.createPrepareStatement(SqliteSqlConst.INSERT_IN_OUT_CARD_SQL);
                for (InOutCard inOutCard : inOutCardListOfMysql) {
                    jdbcUtilInOuCard.setLong(1, inOutCard.getCardId());
                    jdbcUtilInOuCard.setInt(2, inOutCard.getType());
                    jdbcUtilInOuCard.setLong(3, inOutCard.getStationId());
                    jdbcUtilInOuCard.executeUpdate();
                }
                updateParamVer(jdbcUtilInOuCard, SqliteFileConst.IN_OUT_CARD);
                logger.info("车台基础配置同步：出入库卡更新成功！");
            } catch (SQLException e) {
                jdbcUtilInOuCard.rollback();
                throw new SQLException("同步出入库卡异常！", e);
            } finally {
                jdbcUtilInOuCard.close();
            }
        } else {
            jdbcUtilInOuCard.close();
            logger.info("车台基础配置同步：出入库卡无更新！");
        }
        FtpUtil.upload(SqliteFileConst.IN_OUT_CARD_DB_FILE);
    }

    @Transactional
    @Override
    public void syncInOutDev() throws SQLException {
        JDBCUtil jdbcUtilInOutDev = new JDBCUtil();
        int flag = checkVersion(SqliteFileConst.IN_OUT_DEV, jdbcUtilInOutDev);
        if (flag == 0) {
            return;
        }
        List<InOutDev> inOutDevListOfMysql = sqliteSyncDao.findInOutDev();
        if (flag == 2) {
            List<InOutDev> inOutDevListOfSqlite = new ArrayList<>();
            try {
                ResultSet resultSet = jdbcUtilInOutDev.query(SqliteSqlConst.QUERY_IN_OUT_DEV_SQL);
                InOutDev inOutDev;
                while (resultSet.next()) {
                    inOutDev = new InOutDev();
                    inOutDev.setDevId(resultSet.getInt(1));
                    inOutDev.setType(resultSet.getInt(2));
                    inOutDev.setStationId(resultSet.getLong(3));
                    inOutDevListOfSqlite.add(inOutDev);
                }
            } catch (SQLException e) {
                logger.error("查询sqlite中出入库读卡器数据异常", e);
            }
            flag = checkList(inOutDevListOfMysql, inOutDevListOfSqlite);
        }
        if (flag == 1) {
            try {
                jdbcUtilInOutDev.createPrepareStatement(SqliteSqlConst.DELETE_IN_OUT_DEV_SQL);
                jdbcUtilInOutDev.executeUpdate();
                jdbcUtilInOutDev.createPrepareStatement(SqliteSqlConst.INSERT_IN_OUT_DEV_SQL);
                for (InOutDev inOutDev : inOutDevListOfMysql) {
                    jdbcUtilInOutDev.setInt(1, inOutDev.getDevId());
                    jdbcUtilInOutDev.setInt(2, inOutDev.getType());
                    jdbcUtilInOutDev.setLong(3, inOutDev.getStationId());
                    jdbcUtilInOutDev.executeUpdate();
                }
                updateParamVer(jdbcUtilInOutDev, SqliteFileConst.IN_OUT_DEV);
                logger.info("车台基础配置同步：出入库读卡器更新成功！");
            } catch (SQLException e) {
                jdbcUtilInOutDev.rollback();
                throw new SQLException("同步出入库读卡器异常！", e);
            } finally {
                jdbcUtilInOutDev.close();
            }
        } else {
            jdbcUtilInOutDev.close();
            logger.info("车台基础配置同步：出入库读卡器无更新！");
        }
        FtpUtil.upload(SqliteFileConst.IN_OUT_DEV_DB_FILE);
    }

    @Transactional
    @Override
    public void syncOildepot() throws SQLException {
        JDBCUtil jdbcUtilOildepot = new JDBCUtil();
        int flag = checkVersion(SqliteFileConst.OIL_DEPOT, jdbcUtilOildepot);
        if (flag == 0) {
            return;
        }
        List<OildepotInfo> oildepotInfoListOfMysql = sqliteSyncDao.findOildepot();
        if (flag == 2) {
            List<OildepotInfo> oildepotInfoListOfSqlite = new ArrayList<>();
            try {
                ResultSet resultSet = jdbcUtilOildepot.query(SqliteSqlConst.QUERY_OIL_DEPOT_SQL);
                OildepotInfo oildepotInfo;
                while (resultSet.next()) {
                    oildepotInfo = new OildepotInfo();
                    oildepotInfo.setId(resultSet.getLong(1));
                    oildepotInfo.setName(resultSet.getString(2));
                    oildepotInfo.setLongitude(resultSet.getFloat(3));
                    oildepotInfo.setLatitude(resultSet.getFloat(4));
                    oildepotInfo.setRadius(resultSet.getInt(5));
                    oildepotInfo.setCover(resultSet.getBytes(6));
                    oildepotInfoListOfSqlite.add(oildepotInfo);
                }
            } catch (SQLException e) {
                logger.error("查询sqlite中油库数据异常", e);
            }
            flag = checkList(oildepotInfoListOfMysql, oildepotInfoListOfSqlite);
        }
        if (flag == 1) {
            try {
                jdbcUtilOildepot.createPrepareStatement(SqliteSqlConst.DELETE_OIL_DEPOT_SQL);
                jdbcUtilOildepot.executeUpdate();
                jdbcUtilOildepot.createPrepareStatement(SqliteSqlConst.INSERT_OIL_DEPOT_SQL);
                for (OildepotInfo oildepotInfo : oildepotInfoListOfMysql) {
                    jdbcUtilOildepot.setLong(1, oildepotInfo.getId());
                    jdbcUtilOildepot.setString(2, oildepotInfo.getName());
                    jdbcUtilOildepot.setFloat(3, oildepotInfo.getLongitude());
                    jdbcUtilOildepot.setFloat(4, oildepotInfo.getLatitude());
                    jdbcUtilOildepot.setInt(5, oildepotInfo.getRadius());
                    jdbcUtilOildepot.setBytes(6, oildepotInfo.getCover());
                    jdbcUtilOildepot.executeUpdate();
                }
                updateParamVer(jdbcUtilOildepot, SqliteFileConst.OIL_DEPOT);
                logger.info("车台基础配置同步：油库更新成功！");
            } catch (SQLException e) {
                jdbcUtilOildepot.rollback();
                throw new SQLException("同步油库异常！", e);
            } finally {
                jdbcUtilOildepot.close();
            }
        } else {
            jdbcUtilOildepot.close();
            logger.info("车台基础配置同步：油库无更新！");
        }
        FtpUtil.upload(SqliteFileConst.OIL_DEPOT_DB_FILE);
    }

    @Override
    public void syncSqliteFile() {
        if (StringUtil.isEmpty(CenterConst.SQLITE_FILE_PATH)) {
            logger.warn("sqlite数据库文件存放路径未配置！");
            return;
        }
        boolean isSendEmail = CenterVariableConfig.isEmailSqlite();
        String[] receivers = CenterVariableConfig.getEmailSqliteReceivers();
        try {
            syncUrgentCard();
        } catch (Exception e) {
            dealException("应急卡", isSendEmail, receivers, e);
        }
        try {
            syncManageCard();
        } catch (Exception e) {
            dealException("管理卡", isSendEmail, receivers, e);
        }
        try {
            syncInOutCard();
        } catch (Exception e) {
            dealException("出入库卡", isSendEmail, receivers, e);
        }
        try {
            syncInOutDev();
        } catch (Exception e) {
            dealException("出入库读卡器", isSendEmail, receivers, e);
        }
        try {
            syncOildepot();
        } catch (Exception e) {
            dealException("油库", isSendEmail, receivers, e);
        }
    }

    /**
     * 处理同步异常
     *
     * @param description 同步文件描述
     * @param isSendEmail 是否发送邮件
     * @param receivers   邮件收件人
     * @param e           {@link Exception}
     */
    private void dealException(String description, boolean isSendEmail, String[] receivers, Exception e) {
        logger.error(description + "同步异常！", e);
        if (isSendEmail && receivers != null) {
            try {
                StringBuilder subBuilder = new StringBuilder()
                        .append("sqlite同步异常通知-")
                        .append(description)
                        .append("同步异常-用户中心[")
                        .append(CenterConst.CENTER_ID)
                        .append(']');
                StringBuilder msgBuilder = new StringBuilder()
                        .append("用户中心：[")
                        .append(CenterConst.CENTER_ID)
                        .append(']')
                        .append(CenterConst.CENTER_NAME)
                        .append('\n')
                        .append(ExceptionUtils.getStackTrace(e));
                FormBody.Builder formBuilder = new FormBody.Builder()
                        .add("centerId", CenterConst.CENTER_ID.toString())
                        .add("subject", subBuilder.toString())
                        .add("msg", msgBuilder.toString());
                for (String receiver : receivers) {
                    if (!receiver.trim().isEmpty()) {
                        formBuilder.add("receiver", receiver);
                    }
                }
                String reply =OkHttpUtil.post(EMAIL_URL, formBuilder.build());
                logger.info("邮件接口应答：{}", reply);
                ResponseMsg responseMsg = JSONUtil.parseToObject(reply, ResponseMsg.class);
                if (responseMsg.getId() > 0) {
                    logger.warn("邮件接口应答失败：{}", responseMsg.getMsg());
                }
            } catch (IOException ioe) {
                logger.error("发送sqlite同步异常邮件失败！{}", e.toString());
            }
        }
    }

    /**
     * 获取sqlite文件版本号
     *
     * @param jdbcUtil {@link JDBCUtil}
     * @return {@link Long} sqlite文件版本号（查不到为-1， 正常大于-1）
     * @throws SQLException
     */
    private long getSqliteFileVersion(JDBCUtil jdbcUtil) throws SQLException {
        ResultSet resultSet = jdbcUtil.query(SqliteSqlConst.QUERY_VERSION_SQL);
        if (resultSet.next()) {
            return resultSet.getLong(1);
        }
        return -1L;
    }

    /**
     * 创建sqlite数据库
     *
     * @param jdbcUtil     JDBCUtil
     * @param sqliteDbName sqlite数据库名称
     * @throws SQLException
     */
    private void createSqliteDb(JDBCUtil jdbcUtil, String sqliteDbName) throws SQLException {
        try {
            new SqliteInit().createSqliteDb(jdbcUtil, sqliteDbName);
            logger.info("创建sqlite数据库{}完成！", sqliteDbName);
        } catch (SQLException e) {
            jdbcUtil.close();
            logger.error("创建sqlite数据库{}异常！\n{}", sqliteDbName, e.toString());
            throw new SQLException(e);
        }
    }

    /**
     * 版本校验
     *
     * @param sqliteDbName sqlite数据库名称
     * @param jdbcUtil     JDBCUtil
     * @return 0 不更新，1 更新，2 进一步验证
     */
    private int checkVersion(String sqliteDbName, JDBCUtil jdbcUtil) throws SQLException {
        Long versionOfMysql = sqliteSyncDao.getVersion(sqliteDbName);
        if (versionOfMysql == null || versionOfMysql.equals(0L)) {
            return 0;
        }
        String pathname = new StringBuffer(CenterConst.SQLITE_FILE_PATH).append('/')
                .append(sqliteDbName).append(".db").toString();
        File file = new File(pathname);
        if (!file.exists()) {
            // sqlite数据库文件不存在，更新
            createSqliteDb(jdbcUtil, sqliteDbName);
            return 1;
        }
        try {
            jdbcUtil.createSqliteConnection(sqliteDbName);
            long versionOfSqlite = getSqliteFileVersion(jdbcUtil);
            if (!versionOfMysql.equals(versionOfSqlite)) {
                // 版本号不一致，更新
                return 1;
            }
        } catch (SQLException e) {
            jdbcUtil.close();
            throw new SQLException(e);
        }
        return 2;
    }

    /**
     * 数据校验
     *
     * @param listOfMysql MySQL数据
     * @param listOfSqlit Sqlite数据
     * @return 0 不更新，1 更新
     */
    private int checkList(List<? extends BaseSqlite> listOfMysql, List<? extends BaseSqlite> listOfSqlit) {
        int listSizeOfMysql = listOfMysql.size();
        int listSizeOfSqlite = listOfSqlit.size();
        if (listSizeOfMysql != listSizeOfSqlite) {
            return 1;
        }
        for (int i = 0; i < listSizeOfMysql; i++) {
            if (!listOfMysql.get(i).equals(listOfSqlit.get(i))) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 更新车台公共参数版本
     *
     * @param jdbcUtil JDBCUtil对象
     * @param param    车台公共参数名称
     * @throws SQLException
     */
    private void updateParamVer(JDBCUtil jdbcUtil, String param) throws SQLException {
        long ver = System.currentTimeMillis();
        jdbcUtil.createPrepareStatement(SqliteSqlConst.UPDATE_VERSION_SQL);
        jdbcUtil.setLong(1, ver);
        jdbcUtil.executeUpdate();
        vehicleParamVerDao.updateVerByParam(param, ver);
        jdbcUtil.commit();
    }

}