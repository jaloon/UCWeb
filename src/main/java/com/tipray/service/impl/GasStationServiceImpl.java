package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.ResponseMsg;
import com.tipray.bean.VehicleParamVer;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.Handset;
import com.tipray.constant.PageActionMode;
import com.tipray.constant.SqliteFileConst;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.dao.GasStationDao;
import com.tipray.dao.HandsetDao;
import com.tipray.dao.VehicleParamVerDao;
import com.tipray.service.GasStationService;
import com.tipray.util.CoverRegionUtil;
import com.tipray.util.ResponseMsgUtil;
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
@Service("gasStationService")
public class GasStationServiceImpl implements GasStationService {
	@Resource
	private GasStationDao gasStationDao;
	@Resource
	private HandsetDao handsetDao;
	@Resource
	private VehicleParamVerDao vehicleParamVerDao;

	@Transactional
	@Override
	public void addGasStations(List<GasStation> gasStations) {
		gasStations.parallelStream().forEach(station -> setCover(station));
		gasStationDao.addGasStations(gasStations);
        setVer();
	}

    @Transactional
	@Override
	public GasStation addGasStation(GasStation gasStation) {
		if (gasStation != null) {
			String officialId = gasStation.getOfficialId();
			if (StringUtil.isEmpty(officialId)) {
				throw new IllegalArgumentException("加油站编号为空！");
			}
			String name = gasStation.getName();
			if (StringUtil.isEmpty(name)) {
				throw new IllegalArgumentException("加油站名称为空！");
			}
			String abbr = gasStation.getAbbr();
			if (StringUtil.isEmpty(abbr)) {
				throw new IllegalArgumentException("加油站简称为空！");
			}
			if (isGasStationExist(gasStation)) {
				throw new IllegalArgumentException("加油站已存在！");
			}
            setCover(gasStation);
			List<Long> invalidIds = gasStationDao.findInvalidGasStation(gasStation);
			int size = invalidIds.size();
			if (size == 0) {
                gasStationDao.add(gasStation);
			} else if (size == 1) {
                gasStation.setId(invalidIds.get(0));
                gasStationDao.update(gasStation);
			} else {
                gasStationDao.deleteByIds(invalidIds);
                gasStationDao.add(gasStation);
			}

            setVer();
		}
		return gasStation;
	}

    @Transactional
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
				List<Map<String, Object>> list = new ArrayList<>();
				for (String string : cardIdStrArray) {
					Long cardId = Long.parseLong(string, 10);
					Map<String, Object> map = new HashMap<>();
					map.put("id", id);
					map.put("cardId", cardId);
					list.add(map);
				}
				gasStationDao.addGasStationCardsByIdAndCardIds(list);
			}
            setVer();
		}
		return gasStation;
	}

    @Transactional
	@Override
	public void deleteGasStationById(Long id) {
		gasStationDao.delete(id);
        setVer();
	}

	@Override
	public GasStation getGasStationById(Long id) {
		GasStation gasStation = id == null ? null : gasStationDao.getById(id);
		setRegion(gasStation);
		return gasStation;
	}

	@Override
	public List<GasStation> findByName(String gasStationName) {
		return gasStationDao.findByName(gasStationName);
	}

	@Override
	public List<GasStation> findAllGasStations() {
        return gasStationDao.findAll();
	}

	@Override
	public long countGasStation(GasStation gasStation) {
		return gasStationDao.count(gasStation);
	}

	@Override
	public List<GasStation> findByPage(GasStation gasStation, Page page) {
        return gasStationDao.findByPage(gasStation, page);
	}

	@Override
	public GridPage<GasStation> findGasStationsForPage(GasStation gasStation, Page page) {
		long records = countGasStation(gasStation);
		List<GasStation> list = findByPage(gasStation, page);
		return new GridPage<>(list, records, page, gasStation);
	}

	@Override
	public boolean isGasStationExist(GasStation gasStation) {
        if (gasStation == null) {
            return false;
        }
        Integer count = gasStationDao.countValidGasStation(gasStation);
		return count != null && count > 0;
	}

    @Override
    public ResponseMsg getExistInfo(String officialId, String name, String abbr, String mode) {
        if (StringUtil.isEmpty(mode)) {
            throw new IllegalArgumentException("参数不完整！");
        }
        if (StringUtil.isEmpty(officialId)) {
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 11, "加油站编号为空！");
        }
        if (StringUtil.isEmpty(name)) {
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 21, "加油站名称为空！");
        }
        if (StringUtil.isEmpty(abbr)) {
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 31, "加油站简称为空！");
        }
        if (PageActionMode.EDIT.equalsIgnoreCase(mode)) {
            GasStation gasStation = gasStationDao.getByOfficialId(officialId);
            if (!gasStation.getName().equals(name)) {
                Integer nameCount = gasStationDao.countValidGasStationByName(name);
                if (nameCount != null && nameCount > 0) {
                    return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 22, "加油站名称已存在！");
                }
            }
            if (!gasStation.getAbbr().equals(abbr)) {
                Integer abbrCount = gasStationDao.countValidGasStationByAbbr(abbr);
                if (abbrCount != null && abbrCount > 0) {
                    return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 32, "加油站简称已存在！");
                }
            }
            return ResponseMsgUtil.success();
        }
        if (PageActionMode.ADD.equalsIgnoreCase(mode)) {
            Integer idCount = gasStationDao.countValidGasStationByOfficialId(officialId);
            if (idCount != null && idCount > 0) {
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 12, "加油站编号已存在！");
            }
            Integer nameCount = gasStationDao.countValidGasStationByName(name);
            if (nameCount != null && nameCount > 0) {
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 22, "加油站名称已存在！");
            }
            Integer abbrCount = gasStationDao.countValidGasStationByAbbr(abbr);
            if (abbrCount != null && abbrCount > 0) {
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 32, "加油站简称已存在！");
            }
            return ResponseMsgUtil.success();
        }
        throw new IllegalArgumentException("参数无效！");
    }

    private void setRegion(GasStation gasStation) {
		if (gasStation == null) {
			return;
		}
		byte[] cover = gasStation.getCover();
		String region = CoverRegionUtil.coverToRegion(cover);
		gasStation.setCoverRegion(region);
	}

	private void setCover(GasStation gasStation) {
        if (gasStation == null) {
            return;
        }
		String region = gasStation.getCoverRegion();
		byte[] cover = CoverRegionUtil.regionToCover(region);
		gasStation.setCover(cover);
	}

	private void setVer(){
        VehicleParamVer vehicleParamVer = vehicleParamVerDao.getByParam(SqliteFileConst.GAS_STATION);
        if (vehicleParamVer == null) {
            vehicleParamVer = new VehicleParamVer();
            vehicleParamVer.setParam(SqliteFileConst.GAS_STATION);
            vehicleParamVer.setVer(System.currentTimeMillis());
            vehicleParamVerDao.add(vehicleParamVer);
        } else {
            vehicleParamVerDao.updateVerByParam(SqliteFileConst.GAS_STATION, System.currentTimeMillis());
        }
    }

	@Override
	public List<Map<String, Object>> getIdAndNameOfAllGasStations() {
		return gasStationDao.findIdAndNameOfAllGasStations();
	}
}