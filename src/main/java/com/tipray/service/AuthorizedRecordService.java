package com.tipray.service;

import com.tipray.bean.record.AuthorizedRecord;

/**
 * AuthorizedRecordService
 *
 * @author chenlong
 * @version 1.0 2018-07-12
 */
public interface AuthorizedRecordService {
    /**
     * 添加授权记录
     * @param authorizedRecord
     */
    void addAuthorizedRecord(AuthorizedRecord authorizedRecord);
}