package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.DistributionRecord;
import com.tipray.dao.DistributionRecordDao;
import com.tipray.dao.GasStationDao;
import com.tipray.dao.TrackDao;
import com.tipray.dao.VehicleDao;
import com.tipray.service.DistributionRecordService;
import com.tipray.util.BytesConverterByLittleEndian;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配送信息记录业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = "Exception")
@Service("distributionRecordService")
public class DistributionRecordServiceImpl implements DistributionRecordService {
	@Resource
	private DistributionRecordDao distributionRecordDao;
	@Resource
    private VehicleDao vehicleDao;
	@Resource
    private GasStationDao gasStationDao;
	@Resource
    private TrackDao trackDao;

	@Override
	public DistributionRecord getRecordById(Long id) {
		return id == null ? null : distributionRecordDao.getById(id);
	}

	@Override
	public List<DistributionRecord> findAllRecords() {
		return distributionRecordDao.findAll();
	}

	@Override
	public long countRecord(DistributionRecord record) {
		return distributionRecordDao.count(record);
	}

	@Override
	public List<DistributionRecord> findByPage(DistributionRecord record, Page page) {
		return distributionRecordDao.findByPage(record, page);
	}

	@Override
	public GridPage<DistributionRecord> findRecordsForPage(DistributionRecord record, Page page) {
		long records = countRecord(record);
		List<DistributionRecord> list = findByPage(record, page);
		return new GridPage<DistributionRecord>(list, records, page.getPageId(), page.getRows(), list.size(), record);
	}

	@Override
	public Integer countByInvoice(String invoice) {
		return distributionRecordDao.countByInvoice(invoice);
	}

	@Override
	public ByteBuffer addDistributionRecord(Map<String, Object> distributionMap) {
		// 车辆ID
		Long carId = (Long) distributionMap.get("carId");
		// 仓号
		Integer storeId = Integer.parseInt((String) distributionMap.get("binNum"), 10);
		distributionRecordDao.checkDistribute(carId, storeId);
		distributionRecordDao.addByDistributionMap(distributionMap);
		// 配送ID
        Long transportId = (Long) distributionMap.get("id");
        // 获取加油站相关信息
        Map<String, Object> stationMap = gasStationDao.getStationForChangeByOfficialId((String) distributionMap.get("deptId"));
        if (EmptyObjectUtil.isEmptyMap(stationMap)) {
            throw new IllegalArgumentException("加油站不存在！");
        }
        // 加油站Id
        long stationId = (long) stationMap.get("id");
        // 加油站名称（简称）
        String stationName = (String) stationMap.get("name");
        // 加油站经度
        float longitude = (float) stationMap.get("longitude");
        // 加油站纬度
        float latitude = (float) stationMap.get("latitude");
        // 加油站施解封半径
        byte radius = ((Integer) stationMap.get("radius")).byteValue();
        // 加油站占地范围
        byte[] cover = (byte[]) stationMap.get("cover");
        // 加油站手持机设备ID
        Integer handset = (Integer) stationMap.get("handset");
        // 加油站手持机版本
        Integer handver = (Integer) stationMap.get("handver");
        // 获取加油站普通卡信息
        List<Long> cardIds = gasStationDao.findOrdinaryCardById(stationId);

        byte[] tranportIdDword = BytesConverterByLittleEndian.getBytes(transportId.intValue());
        byte[] stationNameBuf = stationName.getBytes(StandardCharsets.UTF_8);
        int stationNameBufLen = stationNameBuf.length;
        byte[] userNameBuf = "物流配送接口".getBytes(StandardCharsets.UTF_8);
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
        dataBuffer.put((byte) 2);
        // 添加仓号，1个字节
        dataBuffer.put(storeId.byteValue());
        // 添加原配送ID，4个字节
        dataBuffer.put(tranportIdDword);
        // 添加新配送ID，4个字节
        dataBuffer.put(tranportIdDword);
        // 添加新加油站ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes((int) stationId));
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

	@Override
	public List<Map<String, Object>> findDistributionsByGasStationId(Long gasStationId) {
		if (gasStationId == null) {
			return null;
		}
        List<Map<String, Object>> list = distributionRecordDao.findDistributionsByGasStationId(gasStationId);
		if (EmptyObjectUtil.isEmptyList(list)) {
		    return null;
        }
		list.forEach(map -> {
		    Long carId = (Long) map.get("vehicle_id");
            Map<String, Object> track = trackDao.getLastTrackForAppByMap(map);
            if (track != null) {
                Long trackTime = (Long) track.get("timestamp"); // unix_timestamp，uint32
                track.remove("timestamp");
                Map<String, Object> status = vehicleDao.getCarStatusByCarId(carId);
                if (status != null) {
                    Long triggerTime = (Long) status.get("trigger_time"); // unix_timestamp，uint32
                    if (trackTime.compareTo(triggerTime) < 0) {
                        track.put("vehicle_status", status.get("status"));
                    }
                }
            } else {
                track = new HashMap<>();
                track.put("vehicle_id", carId);
                track.put("vehicle_number", "");
                track.put("track_id", 0);
                track.put("is_lnglat_valid", 0);
                track.put("longitude", 0);
                track.put("latitude", 0);
                track.put("vehicle_status", 0);
                track.put("vehicle_alarm_status", 0);
                track.put("angle", 0);
                track.put("speed", 0);
                track.put("speed", 0);
                track.put("lock_status_info", "");
                track.put("track_time", "");
                Map<String, Object> status = vehicleDao.getCarStatusByCarId(carId);
                if (status != null) {
                    track.put("vehicle_status", status.get("status"));
                } else {
                    track.put("vehicle_status", 0);
                }
            }
            map.put("track", track);
            map.put("is_online", vehicleDao.getOnline(carId));
        });
		return list;
	}

	@Override
	public List<Map<String, Object>> findDistributionsByVehicle(String carNumber, Integer storeId) {
		if (StringUtil.isEmpty(carNumber)) {
			return null;
		}
		return distributionRecordDao.findDistributionsByVehicle(carNumber, storeId);
	}

}
