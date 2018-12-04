package com.tipray.dao;

/**
 * 记录认证方式持久层
 *
 * @author chenlong
 * @version 1.0 2018-11-21
 */
public interface RecordAuthDao {

    String getHandsetDirectorByDeviceId(Integer deviceId);

    String getCardDirectorByCardId(Long cardId);

    Boolean isBarrier(Long remoteId);

    String getOperatorByRemoteId(Long remoteId);

    String getUserNameByUserId(Long userId);

}
