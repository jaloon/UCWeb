package com.tipray.dao;

import com.tipray.bean.record.AuthorizedRecord;
import com.tipray.core.annotation.MyBatisAnno;

/**
 * AuthorizedRecordDao
 *
 * @author chenlong
 * @version 1.0 2018-07-12
 */
@MyBatisAnno
public interface AuthorizedRecordDao {
    /**
     * 添加授权记录
     * @param authorizedRecord
     */
    void add(AuthorizedRecord authorizedRecord);

}
