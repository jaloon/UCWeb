package com.tipray.service.impl;

import com.tipray.bean.baseinfo.AppDev;
import com.tipray.bean.baseinfo.AppSync;
import com.tipray.bean.baseinfo.AppVer;
import com.tipray.constant.CenterConst;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.AppdevDao;
import com.tipray.dao.AppverDao;
import com.tipray.service.AppService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service("appService")
public class AppServiceImpl implements AppService {
	@Resource
    private AppdevDao appdevDao;
	@Resource
	private AppverDao appverDao;

	@Override
	public AppDev addAppdev(AppDev appDev) throws ServiceException {
        if (appDev != null) {
            List<Long> ids = appdevDao.findIdsByUuid(appDev.getUuid());
            if (EmptyObjectUtil.isEmptyList(ids)) {
                appdevDao.add(appDev);
                return appDev;
            }
            int size = ids.size();
            if (size == 1) {
                appDev.setId(ids.get(0));
                appdevDao.update(appDev);
                return appDev;
            }
            if (size > 1) {
                appdevDao.deleteByUuid(appDev.getUuid());
                appdevDao.add(appDev);
            }
        }
		return appDev;
	}

	@Override
	public AppDev updateAppdev(AppDev appDev) throws ServiceException {
	    if (appDev != null) {
            appdevDao.update(appDev);
        }
		return appDev;
	}

	@Override
	public void updateModelAndCurrentVerByUuid(String model, String currentVer, String uuid) throws ServiceException {
        appdevDao.updateModelAndCurrentVerByUuid(model, currentVer, uuid);
	}

	@Override
	public void deleteAppdevById(Long id) throws ServiceException {
        appdevDao.delete(id);
	}

	@Override
    public boolean isAppdevExist(String uuid) {
        Integer num = appdevDao.countByUuid(uuid);
        if (num == null) {
            return false;
        }
        return num > 0;
    }

    @Override
    public AppVer addAppver(AppVer appVer) {
        if (appVer == null) {
            return null;
        }
        appverDao.add(appVer);
        return appVer;
    }

    @Override
    public AppVer updateAppver(AppVer appVer) {
        if (appVer == null) {
            return null;
        }
        appverDao.update(appVer);
        return appVer;
    }

    @Override
    public void deleteAppverById(Long id) {
        if (id == null) {
            return;
        }
        appverDao.delete(id);
    }

    @Override
    public String getMinverBySystem(String system) {
	    if (StringUtil.isEmpty(system)) {
            return null;
        }
        Long centerId = CenterConst.CENTER_ID.longValue();
        return appverDao.getMinverByCenterIdAndSystem(centerId, system);
    }

    @Override
    public void sync(AppSync appSync) {
	    List<AppVer> appVers = appSync.getAppvers();
	    List<AppDev> appDevs = appSync.getAppdevs();
	    if (!EmptyObjectUtil.isEmptyList(appVers)) {
	        List<Long> ids = appverDao.findAllIds();
	        if (EmptyObjectUtil.isEmptyList(ids)) {
	            appverDao.batchAdd(appVers);
            } else {
	            List<AppVer> adds = new ArrayList<>();
	            List<AppVer> upds = new ArrayList<>();
                for (AppVer appVer : appVers) {
                    Long id = appVer.getId();
                    if (ids.contains(id)) {
                        upds.add(appVer);
                        ids.remove(id);
                    } else {
                        adds.add(appVer);
                    }
                }
                if (adds.size()>0){
                    appverDao.batchAdd(adds);
                }
                if (upds.size()>0) {
                    appverDao.batchUpdate(upds);
                }
                if (ids.size()>0){
                    appverDao.batchDelete(ids);
                }
            }

        }
        if (!EmptyObjectUtil.isEmptyList(appDevs)) {
            List<String> dbUuids = appdevDao.findAllUuids();
            if (EmptyObjectUtil.isEmptyList(dbUuids)) {
                appdevDao.batchAdd(appDevs);
            } else {
                List<AppDev> adds = new ArrayList<>();
                List<AppDev> upds = new ArrayList<>();
                for (AppDev appDev : appDevs) {
                    String uuid = appDev.getUuid();
                    if (dbUuids.contains(uuid)) {
                        upds.add(appDev);
                        dbUuids.remove(uuid);
                    } else {
                        adds.add(appDev);
                    }
                }
                if (adds.size()>0){
                    appdevDao.batchAdd(adds);
                }
                if (upds.size()>0) {
                    appdevDao.batchUpdate(upds);
                }
                if (dbUuids.size()>0){
                    appdevDao.batchDelete(dbUuids);
                }
            }
        }
    }
}
