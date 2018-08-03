package com.tipray.service.impl;

import com.tipray.bean.record.AuthorizedRecord;
import com.tipray.dao.AuthorizedRecordDao;
import com.tipray.service.AuthorizedRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * AuthorizedRecordService
 *
 * @author chenlong
 * @version 1.0 2018-07-12
 */
@Transactional
@Service("authorizedRecordService")
public class AuthorizedRecordServiceImpl implements AuthorizedRecordService {
    @Resource
    private AuthorizedRecordDao authorizedRecordDao;

    @Override
    public void addAuthorizedRecord(AuthorizedRecord authorizedRecord) {
        authorizedRecordDao.add(authorizedRecord);
    }
}
