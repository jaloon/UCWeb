package com.tipray.service;

import java.sql.SQLException;

/**
 * SqliteSyncService
 *
 * @author chenlong
 * @version 1.0 2018-08-22
 */
public interface SqliteSyncService {
    /**
     * 同步应急卡
     */
    void syncUrgentCard() throws SQLException;

    /**
     * 同步管理卡
     */
    void syncManageCard() throws SQLException;

    /**
     * 同步出入库卡
     */
    void syncInOutCard() throws SQLException;

    /**
     * 同步出入库读卡器
     */
    void syncInOutDev() throws SQLException;

    /**
     * 同步油库
     */
    void syncOildepot() throws SQLException;

    /**
     * 同步sqlite文件
     */
    void syncSqliteFile();

}
