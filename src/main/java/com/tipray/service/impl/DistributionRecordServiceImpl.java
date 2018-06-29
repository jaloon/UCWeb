package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.record.DistributionRecord;
import com.tipray.dao.DistributionRecordDao;
import com.tipray.dao.TrackDao;
import com.tipray.dao.VehicleDao;
import com.tipray.service.DistributionRecordService;
import com.tipray.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
	public void addDistributionRecord(Map<String, Object> distributionMap) {
		// 车辆ID
		Long carId = (Long) distributionMap.get("carId");
		// 仓号
		Integer storeId = Integer.parseInt((String) distributionMap.get("binNum"), 10);
		distributionRecordDao.checkDistribute(carId, storeId);
		distributionRecordDao.addByDistributionMap(distributionMap);
	}

	@Override
	public List<Map<String, Object>> findDistributionsByGasStationId(Long gasStationId) {
		if (gasStationId == null) {
			return null;
		}
        List<Map<String, Object>> list = distributionRecordDao.findDistributionsByGasStationId(gasStationId);
		list.forEach(map -> {
		    map.put("is_online", vehicleDao.getOnline((Long) map.get("vehicle_id")));
		    map.put("track", trackDao.getLastTrackForAppByMap(map));
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
