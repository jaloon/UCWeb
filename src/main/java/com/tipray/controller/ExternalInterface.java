package com.tipray.controller;

import com.tipray.bean.ResponseMsg;
import com.tipray.bean.baseinfo.AppDev;
import com.tipray.bean.baseinfo.AppVer;
import com.tipray.core.base.BaseAction;
import com.tipray.service.AppService;
import com.tipray.util.JSONUtil;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.StringUtil;
import com.tipray.websocket.AlarmWebSocketHandler;
import com.tipray.websocket.MonitorWebSocketHandler;
import net.sf.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 外部接口
 *
 * @author chenlong
 */
@Controller
@RequestMapping("api")
public class ExternalInterface extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ExternalInterface.class);
    @Resource
    private AppService appService;
    @Autowired
    private AlarmWebSocketHandler alarmWebSocketHandler;
    @Autowired
    private MonitorWebSocketHandler monitorWebSocketHandler;

    /**
     * 报警
     *
     * @param alarmId {@link Long} 报警ID
     * @param biz     {@link Integer} 业务类型（1 报警，2 车台消除报警）
     */
    @RequestMapping(value = "alarm", method = RequestMethod.POST)
    @ResponseBody
    public void alarm(Long alarmId, Integer biz) {
        logger.info("报警业务：alarmId = {}，biz = {}", alarmId, biz);
        boolean isParamsValid = alarmId != null && alarmId > 0 && biz != null && biz > 0;
        if (isParamsValid) {
            switch (biz) {
                case 1:
                    alarmWebSocketHandler.broadcastAlarm(alarmId);
                    return;
                case 2:
                    alarmWebSocketHandler.broadcastClearAlarm(alarmId);
                    return;
                default:
                    logger.warn("报警业务类型超出可处理范围，biz = {}", biz);
                    return;
            }
        }
        logger.error("报警业务：参数无效，alarmId = {}，biz = {}", alarmId, biz);
    }

    /**
     * 车辆监控
     *
     * @param vehicleIsOnline {@link JSON} 在线/离线 vehicleIsOnline={"vehicle_id": 1, "is_online":1}
     * @param vehicleCfg      {@link JSON} 车辆配置信息 vehicleCfg={"vehicle_id": 1}
     */
    @RequestMapping(value = "monitor", method = RequestMethod.POST)
    @ResponseBody
    public void monitor(String vehicleIsOnline, String vehicleCfg) {
        logger.info("车辆监控业务：vehicleIsOnline = {}, vehicleCfg = {}", vehicleIsOnline, vehicleCfg);
        try {
            if (StringUtil.isNotEmpty(vehicleIsOnline)) {
                Map<String, Object> onlineMap = JSONUtil.parseToMap(vehicleIsOnline);
                Integer online = (Integer) onlineMap.get("is_online");
                Integer vehicleId = (Integer) onlineMap.get("vehicle_id");
                if (online == null || vehicleId == null) {
                    logger.warn("在线信息有空值：online={}, vehicleId={}", online, vehicleId);
                    return;
                }
                if (online < 0 || online > 1) {
                    logger.warn("在线状态越界：online={}", online);
                    return;
                }
                monitorWebSocketHandler.dealOnlineUpload(vehicleId.longValue(), online);
                return;
            }
            if (StringUtil.isNotEmpty(vehicleCfg)) {
                Map<String, Object> cfgMap = JSONUtil.parseToMap(vehicleCfg);
                Integer vehicleId = (Integer) cfgMap.get("vehicle_id");
                if (vehicleId == null) {
                    return;
                }
                monitorWebSocketHandler.dealVehicleCfgUpload(vehicleId.longValue());
                return;
            }
        } catch (Exception e) {
            logger.error("处理监控信息异常！", e);
        }
    }

    /**
     * 同步新增APP设备信息
     *
     * @param appdev {@link String} APP配置信息json字符串
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "appdev/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMsg addAppdev(String appdev) {
        logger.info("同步新增APP设备信息：appdev={}", appdev);
        try {
            AppDev appDev = JSONUtil.parseToObject(appdev, AppDev.class);
            appService.addAppdev(appDev);
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            logger.error("同步新增APP设备信息异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 同步更新APP设备信息
     *
     * @param appdev {@link String} APP设备信息json字符串
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "appdev/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMsg updateAppdev(String appdev) {
        logger.info("同步更新APP设备信息：appdev={}", appdev);
        try {
            AppDev appDev = JSONUtil.parseToObject(appdev, AppDev.class);
            appService.updateAppdev(appDev);
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            logger.error("同步更新APP设备信息异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 同步删除APP设备信息
     *
     * @param id {@link Long}  APP设备信息记录ID
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "appdev/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMsg deleteAppdev(Long id) {
        logger.info("同步删除APP设备信息：id={}", id);
        try {
            appService.deleteAppdevById(id);
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            logger.error("同步删除APP设备信息异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }
    /**
     * 同步新增APP版本信息
     *
     * @param appver {@link String} APP版本信息json字符串
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "appver/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMsg addAppver(String appver) {
        logger.info("同步新增APP版本信息：appver={}", appver);
        try {
            AppVer appVer = JSONUtil.parseToObject(appver, AppVer.class);
            appService.addAppver(appVer);
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            logger.error("同步新增APP版本信息异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 同步更新APP版本信息
     *
     * @param appver {@link String} APP版本信息json字符串
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "appver/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMsg updateAppver(String appver) {
        logger.info("同步更新APP版本信息：appver={}", appver);
        try {
            AppVer appVer = JSONUtil.parseToObject(appver, AppVer.class);
            appService.updateAppver(appVer);
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            logger.error("同步更新APP版本信息异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 同步删除APP配置信息
     *
     * @param id {@link Long}  APP配置记录ID
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "appver/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMsg deleteAppver(Long id) {
        logger.info("同步删除APP版本信息：id={}", id);
        try {
            appService.deleteAppverById(id);
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            logger.error("同步删除APP版本信息异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }


}
