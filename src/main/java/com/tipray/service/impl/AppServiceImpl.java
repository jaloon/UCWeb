package com.tipray.service.impl;

import com.tipray.bean.baseinfo.AppDev;
import com.tipray.bean.baseinfo.AppSync;
import com.tipray.bean.baseinfo.AppVer;
import com.tipray.bean.baseinfo.CenterDev;
import com.tipray.constant.CenterConst;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.AppdevDao;
import com.tipray.dao.AppverDao;
import com.tipray.dao.CenterDevDao;
import com.tipray.service.AppService;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.StringUtil;
import com.tipray.util.VersionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * APP相关业务层
 *
 * @author chenlong
 * @version 1.0 2018-07-22
 *
 */
@Service("appService")
public class AppServiceImpl implements AppService {
	@Resource
    private AppdevDao appdevDao;
	@Resource
	private AppverDao appverDao;
	@Resource
    private CenterDevDao centerDevDao;

    @Transactional
    @Override
    public AppDev addOrUpdateAppdev(String uuid, String appid, String system, String ver) {
        if (StringUtil.isEmpty(uuid)) {
            throw new IllegalArgumentException("uuid为空！");
        }
        if (StringUtil.isEmpty(appid)) {
            throw new IllegalArgumentException("appid为空！");
        }
        if (StringUtil.isEmpty(system)) {
            throw new IllegalArgumentException("system为空！");
        }
        if (StringUtil.isEmpty(ver)) {
            throw new IllegalArgumentException("当前版本号为空！");
        }
        if (!VersionUtil.isVerSion(ver)) {
            throw new IllegalArgumentException("版本号格式不符！");
        }
        AppDev appDev = new AppDev();
        appDev.setUuid(uuid);
        appDev.setAppid(appid);
        appDev.setSystem(system);
        appDev.setCurrentVer(ver);
        List<Long> ids = appdevDao.findIdsByUuidAndAppid(uuid, appid);
        if (EmptyObjectUtil.isEmptyList(ids)) {
            appdevDao.add(appDev);
        } else {
            int size = ids.size();
            if (size == 1) {
                appDev.setId(ids.get(0));
                appdevDao.update(appDev);
                return appDev;
            }
            if (size > 1) {
                appdevDao.deleteByUuidAndAppid(uuid, appid);
                appdevDao.add(appDev);
            }
        }
        return appDev;
    }

    @Transactional
	@Override
	public AppDev addAppdev(AppDev appDev) throws ServiceException {
        if (appDev == null) {
            return null;
        }
        String uuid = appDev.getUuid();
        if (StringUtil.isEmpty(uuid)) {
            throw new IllegalArgumentException("uuid为空！");
        }
        String appid = appDev.getAppid();
        if (StringUtil.isEmpty(appid)) {
            throw new IllegalArgumentException("appid为空！");
        }
        List<Long> ids = appdevDao.findIdsByUuidAndAppid(uuid, appid);
        if (EmptyObjectUtil.isEmptyList(ids)) {
            appdevDao.add(appDev);
        } else {
            int size = ids.size();
            if (size == 1) {
                appDev.setId(ids.get(0));
                appdevDao.update(appDev);
                return appDev;
            }
            if (size > 1) {
                appdevDao.deleteByUuidAndAppid(uuid, appid);
                appdevDao.add(appDev);
            }
        }
		return appDev;
	}

    @Transactional
	@Override
	public AppDev updateAppdev(AppDev appDev) throws ServiceException {
	    if (appDev != null) {
            appdevDao.update(appDev);
        }
		return appDev;
	}

    @Transactional
    @Override
    public void updateModelAndCurrentVerByUuidAndAppid(String model, String currentVer, String uuid, String appid) throws ServiceException {
        appdevDao.updateModelAndCurrentVerByUuidAndAppid(model, currentVer, uuid, appid);
    }

    @Transactional
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
    public boolean isAppdevExist(String uuid, String appid) {
        Integer num = appdevDao.countByUuidAndAppid(uuid, appid);
        if (num == null) {
            return false;
        }
        return num > 0;
    }

    @Transactional
    @Override
    public AppVer addAppver(AppVer appVer) {
        if (appVer == null) {
            return null;
        }
        appverDao.add(appVer);
        return appVer;
    }

    @Transactional
    @Override
    public AppVer updateAppver(AppVer appVer) {
        if (appVer == null) {
            return null;
        }
        appverDao.update(appVer);
        return appVer;
    }

    @Transactional
    @Override
    public void deleteAppverById(Long id) {
        if (id == null) {
            return;
        }
        appverDao.delete(id);
    }

    @Override
    public String getMinverByAppidAndSystem(String appid, String system) {
        if (StringUtil.isEmpty(appid)) {
            throw new IllegalArgumentException("appid为空！");
        }
	    if (StringUtil.isEmpty(system)) {
            throw new IllegalArgumentException("system为空！");
        }
        AppVer appVer = new AppVer();
	    appVer.setCenterId(CenterConst.CENTER_ID.longValue());
	    appVer.setAppid(appid);
	    appVer.setSystem(system);
        return appverDao.getMinverByAppver(appVer);
    }

    @Override
    public CenterDev addCenterdev(CenterDev centerDev) {
        if (centerDev == null) {
            return null;
        }
        centerDevDao.add(centerDev);
        return centerDev;
    }

    @Override
    public CenterDev updateCenterdev(CenterDev centerDev) {
        if (centerDev == null) {
            return null;
        }
        centerDevDao.update(centerDev);
        return centerDev;
    }

    @Override
    public void deleteCenterdevById(Long id) {
        if (id == null) {
            return;
        }
        centerDevDao.delete(id);
    }

    @Override
    public boolean isCenterdevExist(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            throw new IllegalArgumentException("uuid为空！");
        }
        Integer num = centerDevDao.countByUuidAndCenterId(uuid, CenterConst.CENTER_ID.longValue());
        if (num == null) {
            return false;
        }
        return num > 0;
    }

    @Transactional
    @Override
    public void sync(AppSync appSync) {
	    List<AppVer> appVers = appSync.getAppvers();
	    List<CenterDev> centerDevs = appSync.getCenterDevs();
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
                if (ids.size()>0){
                    appverDao.batchDelete(ids);
                }
                if (adds.size()>0){
                    appverDao.batchAdd(adds);
                }
                if (upds.size()>0) {
                    appverDao.batchUpdate(upds);
                }
            }
        }
        if (!EmptyObjectUtil.isEmptyList(centerDevs)) {
            List<Long> ids = centerDevDao.findAllIds();
            if (EmptyObjectUtil.isEmptyList(ids)) {
                centerDevDao.batchAdd(centerDevs);
            } else {
                List<CenterDev> adds = new ArrayList<>();
                List<CenterDev> upds = new ArrayList<>();
                for (CenterDev centerDev : centerDevs) {
                    Long id = centerDev.getId();
                    if (ids.contains(id)) {
                        upds.add(centerDev);
                        ids.remove(id);
                    } else {
                        adds.add(centerDev);
                    }
                }
                if (ids.size()>0){
                    centerDevDao.batchDelete(ids);
                }
                if (adds.size()>0){
                    centerDevDao.batchAdd(adds);
                }
                if (upds.size()>0) {
                    centerDevDao.batchUpdate(upds);
                }
            }
        }
    }
}