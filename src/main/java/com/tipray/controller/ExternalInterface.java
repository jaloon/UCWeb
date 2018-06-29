package com.tipray.controller;

import com.tipray.core.base.BaseAction;
import com.tipray.util.JSONUtil;
import com.tipray.util.NetUtil;
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

import javax.servlet.http.HttpServletRequest;
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
        logger.debug("报警业务：alarmId = {}，biz = {}", alarmId, biz);
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
        logger.debug("车辆监控业务：vehicleIsOnline = {}, vehicleCfg = {}", vehicleIsOnline, vehicleCfg);
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
            logger.error("处理监控信息异常：\n{}", e.toString());
            logger.debug("处理监控信息异常堆栈信息：", e);
        }
    }

    @RequestMapping(value = "mac")
    @ResponseBody
    public String mac(HttpServletRequest request) {
        String ip = NetUtil.getRequestIpAddr(request);
        System.out.println(ip);
        String mac = NetUtil.getMACAddress(ip);
        System.out.println(mac);
        return mac;
    }
}
