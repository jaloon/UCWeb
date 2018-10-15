package com.tipray.service.impl;

import com.tipray.bean.ChangeInfo;
import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.User;
import com.tipray.bean.record.ChangeRecord;
import com.tipray.dao.ChangeRecordDao;
import com.tipray.dao.DistributionRecordDao;
import com.tipray.dao.GasStationDao;
import com.tipray.service.ChangeRecordService;
import com.tipray.util.BytesConverterByLittleEndian;
import com.tipray.util.EmptyObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程换站记录业务层
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Service("changeRecordService")
public class ChangeRecordServiceImpl implements ChangeRecordService {
    @Resource
    private ChangeRecordDao changeRecordDao;
    @Resource
    private DistributionRecordDao distributionRecordDao;
    @Resource
    private GasStationDao gasStationDao;

    @Override
    public ChangeRecord getRecordById(Long id) {
        ChangeRecord record = null;
        if (id != null) {
            record = changeRecordDao.getById(id);
            setUser(record);
        }
        return record;
    }

    @Override
    public List<ChangeRecord> findAllRecords() {
        List<ChangeRecord> list = changeRecordDao.findAll();
        list.parallelStream().forEach(record -> setUser(record));
        return list;
    }

    @Override
    public long countRecord(ChangeRecord record) {
        return changeRecordDao.count(record);
    }

    @Override
    public List<ChangeRecord> findByPage(ChangeRecord record, Page page) {
        List<ChangeRecord> list = changeRecordDao.findByPage(record, page);
        list.parallelStream().forEach(changeRecord -> setUser(changeRecord));
        return list;
    }

    @Override
    public GridPage<ChangeRecord> findRecordsForPage(ChangeRecord record, Page page) {
        long records = countRecord(record);
        List<ChangeRecord> list = findByPage(record, page);
        return new GridPage<>(list, records, page, record);
    }

    private void setUser(ChangeRecord record) {
        if (record == null) {
            return;
        }
        User user = record.getUser();
        int userId = user.getId().intValue();
        switch (userId) {
            case Integer.MAX_VALUE:
                user.setName("物流平台");
                user.setAccount("-");
                break;
            default:
                break;
        }
        record.setUser(user);
    }

    @Transactional
    @Override
    public Map<String, Object> distributionChange(Map<String, Object> distributionMap) {
        // 配送单号
        String invoice = (String) distributionMap.get("distributNO");
        // // 仓号
        // byte storeId = Byte.parseByte((String) distributionMap.get("binNum"), 10);
        // 获取当前配送ID
        long transportId = distributionRecordDao.getRecentTransportIdByInvoice(invoice);
        // // 将当前配送记录的配送状态更新为换站
        // distributionRecordDao.updateStatusToChanged(transportId);
        // 根据变更的物流配送信息添加配送记录
        distributionRecordDao.addByDistributionMap(distributionMap);
        // 新配送ID
        Long changedTransportId = (Long) distributionMap.get("id");
        // 获取加油站相关信息
        Map<String, Object> stationMap = gasStationDao.getStationForChangeByOfficialId((String) distributionMap.get("deptId"));
        if (EmptyObjectUtil.isEmptyMap(stationMap)) {
            throw new IllegalArgumentException("加油站不存在！");
        }
        // 新加油站ID
        long changedGasstationId = (long) stationMap.get("id");
        distributionMap.put("transportId", transportId);
        distributionMap.put("changedTransportId", changedTransportId);
        distributionMap.put("userId", Integer.MAX_VALUE);
        // 根据变更的物流配送信息添加换站记录
        changeRecordDao.addByChangedDistribution(distributionMap);
        // 获取换站ID
        // long changeId = changeRecordDao.getChangeIdByTransportIdAndChangedTransportId(transportId, changedTransportId);
        long changeId = (Long) distributionMap.get("changeId");
        // 更新换站前配送记录的触发信息
        distributionRecordDao.updateTriggerInfo(transportId, changeId);

        String userName = "物流平台";
        ByteBuffer udpDataBuf = buildUdpDataBuf(userName, transportId, changedTransportId, changedGasstationId, stationMap);
        Map<String, Object> map = new HashMap<>();
        map.put("changeId", changeId);
        map.put("transportId", transportId);
        map.put("changedTransportId", changedTransportId);
        map.put("dataBuffer", udpDataBuf);
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> remoteChangeStation(ChangeInfo changeInfo, String userName) {
        // // 配送单号
        // String invoice = changeInfo.getInvoice();
        // // 仓号
        // byte storeId = changeInfo.getStoreId().byteValue();
        // 新加油站ID
        long changedGasstationId = changeInfo.getChangedGasstationId();
        // 获取当前配送ID
        // long transportId = distributionRecordDao.getRecentTransportIdByInvoice(invoice);
        long transportId = changeInfo.getTransportId();
        // // 将当前配送记录的配送状态更新为换站
        // distributionRecordDao.updateStatusToChanged(transportId);
        // 根据远程换站信息添加配送记录
        distributionRecordDao.addByChangeInfo(changeInfo);
        // 获取换站后的配送ID
        // long changedTransportId = distributionRecordDao.getRecentTransportIdByInvoice(invoice);
        long changedTransportId = changeInfo.getChangedTransportId();
        changeInfo.setTransportId(transportId);
        changeInfo.setChangedTransportId(changedTransportId);
        // 根据换站信息添加换站记录
        changeRecordDao.addByChangeInfo(changeInfo);
        // 获取换站ID
        // long changeId = changeRecordDao.getChangeIdByTransportIdAndChangedTransportId(transportId, changedTransportId);
        long changeId = changeInfo.getId();
        // 更新换站前配送记录的触发信息
        distributionRecordDao.updateTriggerInfo(transportId, changeId);
        // 获取新加油站相关信息
        Map<String, Object> stationMap = gasStationDao.getStationForChangeById(changedGasstationId);
        if (EmptyObjectUtil.isEmptyMap(stationMap)) {
            throw new IllegalArgumentException("新加油站不存在！");
        }
        ByteBuffer udpDataBuf = buildUdpDataBuf(userName, transportId, changedTransportId, changedGasstationId, stationMap);
        Map<String, Object> map = new HashMap<>();
        map.put("changeId", changeId);
        map.put("changedTransportId", changedTransportId);
        map.put("dataBuffer", udpDataBuf);
        return map;
    }

    @Transactional
    @Override
    public void updateChangeStatus(Long changeId, Integer changeStatus) {
        changeRecordDao.updateChangeStatus(changeId, changeStatus);
    }

    @Transactional
    @Override
    public void updateChangeAndTransportStatusForDone(Long changeId, Long transportId, Long changedTransportId) {
        // 将换站状态更新为换站完成
        changeRecordDao.updateChangeStatus(changeId, 2);
        // 将原配送记录的配送状态更新为换站
        distributionRecordDao.updateStatusToChanged(transportId);
        // 将新配送记录的配送状态更新为配送中
        distributionRecordDao.updateStatus(changedTransportId, 1);
    }

    @Transactional
    @Override
    public void updateDistStatus(Long transportId, Integer status) {
        distributionRecordDao.updateStatus(transportId, status);
    }

    /**
     * 构建换站业务UDP协议数据体
     *
     * @param userName           {@link String} 操作员姓名
     * @param transportId        {@link long} 原配送ID
     * @param changedTransportId {@link long} 新配送ID
     * @param changedStationId   {@link long} 新加油站ID
     * @return {@link ByteBuffer} UDP协议数据体
     */
    private ByteBuffer buildUdpDataBuf(String userName, long transportId, long changedTransportId,
                                       long changedStationId, Map<String, Object> stationMap) {
        // 新加油站名称（简称）
        String stationName = (String) stationMap.get("name");
        // 新加油站经度
        float longitude = (float) stationMap.get("longitude");
        // 新加油站纬度
        float latitude = (float) stationMap.get("latitude");
        // 新加油站施解封半径
        byte radius = ((Integer) stationMap.get("radius")).byteValue();
        // 新加油站占地范围
        byte[] cover = (byte[]) stationMap.get("cover");
        // 新加油站手持机设备ID
        Integer handset = (Integer) stationMap.get("handset");
        // 新加油站手持机版本
        Integer handver = (Integer) stationMap.get("handver");
        // 获取新加油站普通卡信息
        List<Long> cardIds = gasStationDao.findOrdinaryCardById(changedStationId);
        byte[] stationNameBuf = stationName.getBytes(StandardCharsets.UTF_8);
        int stationNameBufLen = stationNameBuf.length;
        byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
        int userNameBufLen = userNameBuf.length;
        int coverCoodrNum = 0, handsetNum = 0, cardNum = 0;
        // 缓冲区容量
        int capacity = 28 + stationNameBufLen + userNameBufLen;
        if (!EmptyObjectUtil.isEmptyArray(cover)) {
            coverCoodrNum = cover.length / 8;
            capacity += cover.length;
        }
        if (handset != null && handset != 0) {
            handsetNum = 1;
            capacity += 8;
        }
        if (!EmptyObjectUtil.isEmptyList(cardIds)) {
            cardNum = cardIds.size();
            capacity += cardNum * 8;
        }
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加换站类型，1个字节
        dataBuffer.put((byte) 1);
        // 添加仓号，1个字节
        dataBuffer.put((byte) 0);
        // 添加原配送ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes((int) transportId));
        // 添加新配送ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes((int) changedTransportId));
        // 添加新加油站ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes((int) changedStationId));
        // 添加新加油站经度，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(longitude));
        // 添加新加油站纬度，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(latitude));
        // 添加允许开关锁坐标半径（米），1个字节
        dataBuffer.put(radius);
        // 添加加油站占地范围坐标点数目，1个字节
        dataBuffer.put((byte) coverCoodrNum);
        if (coverCoodrNum > 0) {
            // 添加加油站占地范围
            dataBuffer.put(cover);
        }
        // 添加手持机数目，1个字节
        dataBuffer.put((byte) handsetNum);
        if (handsetNum > 0) {
            // 添加手持机设备ID，4个字节
            dataBuffer.put(BytesConverterByLittleEndian.getBytes(handset));
            // 添加手持机版本，4个字节
            dataBuffer.put(BytesConverterByLittleEndian.getBytes(handver));
        }
        // 添加普通卡数目，1个字节
        dataBuffer.put((byte) cardNum);
        if (cardNum > 0) {
            // 添加普通卡ID，每张卡8个字节
            cardIds.parallelStream().forEach(cardId -> dataBuffer.put(BytesConverterByLittleEndian.getBytes(cardId)));
        }
        // 添加加油站名称长度，1个字节
        dataBuffer.put((byte) stationNameBufLen);
        // 添加加油站名称，UTF-8编码
        dataBuffer.put(stationNameBuf);
        // 添加姓名长度，1个字节
        dataBuffer.put((byte) userNameBufLen);
        // 添加操作员姓名，UTF-8编码
        dataBuffer.put(userNameBuf);
        return dataBuffer;
    }
}