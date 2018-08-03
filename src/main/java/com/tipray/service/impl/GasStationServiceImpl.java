package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.VehicleParamVer;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.Handset;
import com.tipray.constant.SqliteFileConst;
import com.tipray.dao.GasStationDao;
import com.tipray.dao.HandsetDao;
import com.tipray.dao.VehicleParamVerDao;
import com.tipray.service.GasStationService;
import com.tipray.util.CoverRegionUtil;
import com.tipray.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加油站管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("gasStationService")
public class GasStationServiceImpl implements GasStationService {
	@Resource
	private GasStationDao gasStationDao;
	@Resource
	private HandsetDao handsetDao;
	@Resource
	private VehicleParamVerDao vehicleParamVerDao;

	@Override
	public void addGasStations(List<GasStation> gasStations) {
		gasStations.parallelStream().forEach(station -> setCover(station));
		gasStationDao.addGasStations(gasStations);
        setVer(SqliteFileConst.GAS_STATION);
	}
	
	@Override
	public GasStation addGasStation(GasStation gasStation) {
		if (gasStation != null) {
			setCover(gasStation);
			gasStationDao.add(gasStation);
            setVer(SqliteFileConst.GAS_STATION);
		}
		return gasStation;
	}

	@Override
	public GasStation updateGasStation(GasStation gasStation, Integer handsetId, String cardIds) {
		if (gasStation != null) {
			setCover(gasStation);
			gasStationDao.update(gasStation);
			Long id = gasStation.getId();
			handsetDao.resetGasStationOfHandsetByGasStationId(id);
			if (handsetId != null && handsetId > 0) {
				Handset handset = handsetDao.getByDeviceId(handsetId);
				handset.setGasStation(gasStation);
				handsetDao.update(handset);
			}
			gasStationDao.deleteGasStationCardsById(id);
			if (StringUtil.isNotEmpty(cardIds)) {
				String[] cardIdStrArray = cardIds.split(",");
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (String string : cardIdStrArray) {
					Long cardId = Long.parseLong(string, 10);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", id);
					map.put("cardId", cardId);
					list.add(map);
				}
				gasStationDao.addGasStationCardsByIdAndCardIds(list);
			}
            setVer(SqliteFileConst.GAS_STATION);
		}
		return gasStation;
	}

	@Override
	public void deleteGasStationById(Long id) {
		gasStationDao.delete(id);
        setVer(SqliteFileConst.GAS_STATION);
	}

	@Override
	public GasStation getGasStationById(Long id) {
		GasStation gasStation = id == null ? null : gasStationDao.getById(id);
		setRegion(gasStation);
		return gasStation;
	}

	@Override
	public List<GasStation> findByName(String gasStationName) {
		List<GasStation> gasStations = gasStationDao.findByName(gasStationName);
		return gasStations;
	}

	@Override
	public List<GasStation> findAllGasStations() {
		List<GasStation> gasStations = gasStationDao.findAll();
		return gasStations;
	}

	@Override
	public long countGasStation(GasStation gasStation) {
		return gasStationDao.count(gasStation);
	}

	@Override
	public List<GasStation> findByPage(GasStation gasStation, Page page) {
		List<GasStation> gasStations = gasStationDao.findByPage(gasStation, page);
		return gasStations;
	}

	@Override
	public GridPage<GasStation> findGasStationsForPage(GasStation gasStation, Page page) {
		long records = countGasStation(gasStation);
		List<GasStation> list = findByPage(gasStation, page);
		return new GridPage<GasStation>(list, records, page.getPageId(), page.getRows(), list.size(), gasStation);
	}

	@Override
	public GasStation isGasStationExist(GasStation gasStation) {
		String officialId = gasStation.getOfficialId();
		String name = gasStation.getName();
		if (StringUtil.isNotEmpty(officialId)) {
			return gasStationDao.getByOfficialId(officialId);
		}
		if (StringUtil.isNotEmpty(name)) {
			return gasStationDao.getByName(name);
		}
		return null;
	}

	private void setRegion(GasStation gasStation) {
		byte[] cover = gasStation.getCover();
		String region = CoverRegionUtil.coverToRegion(cover);
		gasStation.setCoverRegion(region);
	}

	private void setCover(GasStation gasStation) {
		String region = gasStation.getCoverRegion();
		byte[] cover = CoverRegionUtil.regionToCover(region);
		gasStation.setCover(cover);
	}

	private void setVer(String param){
        VehicleParamVer vehicleParamVer = vehicleParamVerDao.getByParam(param);
        if (vehicleParamVer == null) {
            vehicleParamVer = new VehicleParamVer();
            vehicleParamVer.setParam(param);
            vehicleParamVer.setVer(System.currentTimeMillis());
            vehicleParamVerDao.add(vehicleParamVer);
        } else {
            vehicleParamVerDao.updateVerByParam(param, System.currentTimeMillis());
        }
    }

	@Override
	public List<Map<String, Object>> getIdAndNameOfAllGasStations() {
		return gasStationDao.findIdAndNameOfAllGasStations();
	}

}
