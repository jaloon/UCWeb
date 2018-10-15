package com.tipray.service;

import com.tipray.bean.ChangeInfo;
import com.tipray.bean.record.ChangeRecord;
import com.tipray.core.exception.ServiceException;

import java.util.Map;

/**
 * ChangeRecordService
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
public interface ChangeRecordService extends RecordService<ChangeRecord> {

    /**
     * 物流配送接口更改配送信息换站
     *
     * @param distributionMap 物流配送接口信息
     * @return 换站ID，UDP协议数据体
     * @throws ServiceException
     */
    Map<String, Object> distributionChange(Map<String, Object> distributionMap) throws ServiceException;

    /**
     * 远程换站
     *
     * @param changeInfo 远程换站信息
     * @param userName   操作员姓名
     * @return 换站ID，UDP协议数据体
     * @throws ServiceException
     */
    Map<String, Object> remoteChangeStation(ChangeInfo changeInfo, String userName) throws ServiceException;

    /**
     * 根据换站ID更新换站状态
     *
     * @param changeId     换站ID
     * @param changeStatus 换站状态（0 ：未完成 | 1：远程换站请求中 | 2：远程换站完成）
     * @throws ServiceException
     */
    void updateChangeStatus(Long changeId, Integer changeStatus) throws ServiceException;

    /**
     * 换站完成更新相关状态
     *
     * @param changeId           换站ID
     * @param transportId        原配送ID
     * @param changedTransportId 新配送ID
     * @throws ServiceException
     */
    void updateChangeAndTransportStatusForDone(Long changeId, Long transportId, Long changedTransportId)
            throws ServiceException;

    /**
     * 更新配送状态
     *
     * @param transportId 配送ID
     * @param status      配送状态
     */
    void updateDistStatus(Long transportId, Integer status);
}
