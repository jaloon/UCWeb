package com.tipray.controller;

import com.tipray.bean.*;
import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.baseinfo.User;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.bean.record.AlarmRecord;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.LogTypeConst;
import com.tipray.constant.RemoteControlConst;
import com.tipray.constant.TerminalConfigBitMarkConst;
import com.tipray.constant.reply.*;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.exception.UdpException;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.*;
import com.tipray.util.*;
import com.tipray.websocket.MonitorWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * 远程操作控制器
 *
 * @author chenlong
 * @version 1.0 2018-04-08
 */
@Controller
@RequestMapping("/manage/remote")
/* @Scope("prototype") */
public class VehicleManageController {
    private static final Logger logger = LoggerFactory.getLogger(VehicleManageController.class);
    @Resource
    private VehicleService vehicleService;
    @Resource
    private DeviceService deviceService;
    @Resource
    private ChangeRecordService changeRecordService;
    @Resource
    private AlarmRecordService alarmRecordService;
    @Resource
    private VehicleManageLogService vehicleManageLogService;
    @Resource
    private NioUdpServer udpServer;
    @Resource
    private MonitorWebSocketHandler monitorWebSocketHandler;
    @Resource
    private HttpSession session;

    /**
     * 更新车台轨迹配置
     *
     * @param token            {@link String} UUID令牌
     * @param scope            {@link Integer} 配置范围：0 所有车辆，1 单部车辆
     * @param carNumber        {@link String} 车牌号
     * @param scanInterval     {@link Integer} 轨迹采集间隔（秒）
     * @param uploadInterval   {@link Integer} 默认轨迹上报间隔（秒）
     * @param generateDistance {@link Integer} 轨迹生成距离间隔（米）
     * @param isApp            {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude        {@link Integer} 手机定位经度
     * @param latitude         {@link Integer} 手机定位纬度
     * @param isLocationValid  {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @PermissionAnno("paramModule")
    @RequestMapping(value = "asyn_terminal_config_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynTerminalConfigRequest(
            @RequestParam(value = "token") String token,
            @ModelAttribute VehicleTerminalConfig terminalConfig,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "更新车台配置：token={}, scope={}, carNumber={}, scanInterval={}, uploadInterval={}, generateDistance={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, terminalConfig.getScope(), terminalConfig.getCarNumber(), terminalConfig.getScanInterval(),
                terminalConfig.getUploadInterval(), terminalConfig.getGenerateDistance(), isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TERMINAL
                | LogTypeConst.TYPE_TERMINAL_TRACK_CONFIG | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("更新车台配置：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("更新")
                .append(terminalConfig.getScope() == 0 ? "所有车辆" : terminalConfig.getCarNumber()).append("的轨迹采集和上报间隔配置。")
                .toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        try {
            Integer scope = terminalConfig.getScope();
            if (scope == null
                    || (scope == 1 && StringUtil.isEmpty(terminalConfig.getCarNumber()))
                    || terminalConfig.getScanInterval() == null
                    || terminalConfig.getUploadInterval() == null
                    || terminalConfig.getGenerateDistance() == null) {
                result = "失败，配置参数为空！";
                logger.error("更新车台配置失败：{}", TerminalConfigUpdateErrorEnum.CONFIG_PARAM_NULL);
                return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.CONFIG_PARAM_NULL);
            }
            vehicleService.terminalConfig(terminalConfig);
            // 车辆状况：0 车不存在，1 正常，2 未绑定车台，3 配置范围越界
            Integer carState = terminalConfig.getCarState();
            Integer terminalId = terminalConfig.getTerminalId();
            switch (carState) {
                case 0:
                    result = "失败，车辆不存在！";
                    logger.error("更新车台配置失败：{}", TerminalConfigUpdateErrorEnum.VEHICLE_INVALID);
                    return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.VEHICLE_INVALID);
                case 1:
                    boolean isUpdate = terminalConfig.getIsUpdate() > 0;
                    if (!isUpdate) {
                        result = "失败，配置无更新！";
                        logger.error("更新车台配置失败：{}", TerminalConfigUpdateErrorEnum.CONFIG_WITHOUT_UPDATE);
                        return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.CONFIG_WITHOUT_UPDATE);
                    }

                    cacheId = addCache(UdpBizId.TERMINAL_TRACK_CONFIG_UPDATE_REQUEST, logId, description, null);

                    ByteBuffer src = SendPacketBuilder.buildProtocol0x1202(terminalId);
                    boolean isSend = udpServer.send(src);
                    if (!isSend) {
                        throw new UdpException("UDP发送数据异常！");
                    }
                    addTimeoutTask(src, cacheId);
                    vehicleManageLog.setUdpBizId(UdpBizId.TERMINAL_TRACK_CONFIG_UPDATE_REQUEST);

                    logger.info("更新车台配置指令发送成功！");
                    return ResponseMsgUtil.success();
                case 2:
                    result = "失败，车辆未绑定车台！";
                    logger.error("更新车台配置失败：{}", TerminalConfigUpdateErrorEnum.VEHICLE_UNBINDED);
                    return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.VEHICLE_UNBINDED);
                case 3:
                    result = "失败，配置范围越界！";
                    logger.error("更新车台配置失败：{}", TerminalConfigUpdateErrorEnum.BEYOND_CONFIG_SCOPE);
                    return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.BEYOND_CONFIG_SCOPE);
                default:
                    throw new IllegalArgumentException("更新车台配置参数错误！");
            }
        } catch (Exception e) {
            removeCache(cacheId, null);
            result = "失败，发送更新车台配置请求异常！";
            logger.error("更新车台配置异常：e={}", e.toString());
            logger.debug("更新车台配置异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }
    }

    /**
     * 车台功能启用
     *
     * @param token           {@link String} UUID令牌
     * @param carNumber       {@link String} 车牌号
     * @param functionEnable  {@link Integer} 车台功能启用配置参数，通过位来表示开启和关闭的设置，位序从低位开始
     *                        <ol>
     *                        <li>加油站内施解封是否启动GPS校验</li>
     *                        <li>油库出入库是否启动GPS校验</li>
     *                        <li>加油站开锁是否启动GPS校验</li>
     *                        <li>油库开锁是否启动GPS校验</li>
     *                        <li>是否检测站点信息合法</li>
     *                        <li>是否可在不同解封中多次开锁</li>
     *                        <li>是否只允许一次开锁</li>
     *                        </ol>
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     * @see TerminalConfigBitMarkConst
     */
    @PermissionAnno("paramModule")
    @RequestMapping(value = "asyn_terminal_enable_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynTerminalEnableRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "car_number", required = false) String carNumber,
            @RequestParam(value = "func_enable", required = false) Integer functionEnable,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "车台功能启用配置：token={}, carNumber={}, functionEnable={}, token={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, carNumber,
                functionEnable == null ? null : functionEnable + "[0b" + Integer.toBinaryString(functionEnable) + "]",
                isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TERMINAL
                | LogTypeConst.TYPE_TERMINAL_ENABLE_CONFIG | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("车台功能启用配置：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("配置车台功能启用。").toString();
        String result = "";
        try {
            if (StringUtil.isEmpty(carNumber)) {
                result = "失败，车牌号为空！";
                logger.error("车台功能启用配置失败：{}", TerminalConfigUpdateErrorEnum.CARNUMBER_NULL);
                return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.CARNUMBER_NULL);
            }
            if (functionEnable == null) {
                result = "失败，车台功能启用配置参数为空！";
                logger.error("车台功能启用配置失败：{}", TerminalConfigUpdateErrorEnum.CONFIG_PARAM_NULL);
                return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.CONFIG_PARAM_NULL);
            }
            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                result = "失败，车辆不存在！";
                logger.error("车台功能启用配置失败：{}", TerminalConfigUpdateErrorEnum.VEHICLE_INVALID);
                return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.VEHICLE_INVALID);
            }
            if (vehicle.getVehicleDevice() == null || vehicle.getVehicleDevice().getDeviceId() == 0) {
                result = "失败，车辆未绑定车台！";
                logger.error("车台功能启用配置失败：{}", TerminalConfigUpdateErrorEnum.VEHICLE_UNBINDED);
                return ResponseMsgUtil.error(TerminalConfigUpdateErrorEnum.VEHICLE_UNBINDED);
            }
            vehicleService.terminalEnable(vehicle.getVehicleDevice().getDeviceId(), functionEnable);
            logger.info("车台功能启用配置成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            result = "失败，车台功能启用配置异常！";
            logger.error("车台功能启用配置异常：e={}", e.toString());
            logger.debug("车台功能启用配置异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            if (result.length() > 0) {
                type++;
                monitorWebSocketHandler.broadcastLog(1, description, result);
            } else {
                result = "成功！";
                monitorWebSocketHandler.broadcastLog(0, description, result);
            }
            OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, result, token, vehicleManageLogService, logger);
        }
    }

    /**
     * 车辆绑定
     *
     * @param token           {@link String} UUID令牌
     * @param carNumber       {@link String} 车牌号
     * @param deviceId        {@link Integer} 车台设备ID
     * @param storeNum        {@link Integer} 仓数
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_vehicle_bind_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynVehicleBindRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "car_number", required = false) String carNumber,
            @RequestParam(value = "device_id", required = false) Integer deviceId,
            @RequestParam(value = "store_num", required = false) Integer storeNum,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "车载终端绑定：token={}, carNumber={}, deviceId={}, storeNum={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, carNumber, deviceId, storeNum, isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TERMINAL | LogTypeConst.TYPE_DEVICE_BIND
                | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("车载终端绑定：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("将车辆").append(carNumber).append("与车载终端")
                .append(deviceId).append("绑定。").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        Map<String, Object> params = new HashMap<>();
        boolean isOk = false;
        try {
            if (StringUtil.isEmpty(carNumber)) {
                result = "失败，车牌号为空！";
                logger.error("车载终端绑定失败：{}", DevBindErrorEnum.CARNUMBER_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.CARNUMBER_NULL);
            }
            if (deviceId == null) {
                result = "失败，设备ID为空！";
                logger.error("车载终端绑定失败：{}", DevBindErrorEnum.DEVICE_ID_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.DEVICE_ID_NULL);
            }
            if (storeNum == null) {
                result = "失败，仓数为空！";
                logger.error("车载终端绑定失败：{}", DevBindErrorEnum.STORE_NUM_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.STORE_NUM_NULL);
            }
            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                result = "失败，车辆不存在！";
                logger.error("车载终端绑定失败：{}", DevBindErrorEnum.VEHICLE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_INVALID);
            }
            Device device = deviceService.getDeviceByDeviceId(deviceId);
            if (device == null) {
                result = "失败，设备不存在！";
                logger.error("车载终端绑定失败：{}", DevBindErrorEnum.DEVICE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.DEVICE_INVALID);
            }
            if (device.getType() != 1) {
                result = "失败，设备类型不符！";
                logger.error("车载终端绑定失败：{}", DevBindErrorEnum.DEVICE_TYPE_INCONSISTENT);
                return ResponseMsgUtil.error(DevBindErrorEnum.DEVICE_TYPE_INCONSISTENT);
            }
            Vehicle vehicle1 = vehicleService.getCarByTerminalId(deviceId);
            if (vehicle1 != null) {
                String carNumber1 = vehicle1.getCarNumber();
                if (carNumber1.equals(carNumber)) {
                    result = "车载终端绑定同步成功！";
                    isOk = true;
                    logger.info("车载终端绑定同步成功！");
                    return ResponseMsgUtil.success("车载终端绑定同步成功！");
                }
                result = "失败，设备已与车辆" + vehicle1.getCarNumber() + "绑定！";
                logger.error("车载终端绑定失败：设备已与车辆{}绑定！", vehicle1.getCarNumber());
                return ResponseMsgUtil.error(ErrorTagConst.DEV_BIND_ERROR_TAG, DevBindErrorEnum.DEVICE_BINDED.code(),
                        "设备已与车辆" + carNumber1 + "绑定！");
            }

            params.put("carNumber", carNumber);
            params.put("deviceId", deviceId);
            cacheId = addCache(UdpBizId.CAR_BIND_REQUEST, logId, description, params);

            ByteBuffer src = SendPacketBuilder.buildProtocol0x1204(deviceId, carNumber, storeNum.byteValue());
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.CAR_BIND_REQUEST);

            result = "请求发起成功！";
            isOk = true;
            logger.info("车载终端绑定指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, params);
            result = "失败，发送车载终端绑定请求异常！";
            logger.error("车载终端绑定异常：e={}", e.toString());
            logger.debug("车载终端绑定异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLogResult(vehicleManageLog, isOk, type, description, result);
        }
    }

    /**
     * 监听待绑定锁列表
     *
     * @param token           {@link String} UUID令牌
     * @param carNumber       {@link String} 车牌号
     * @param listenState     {@link Integer} 监听状态<br>
     *                        1 开始监听<br>
     *                        2 结束监听
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_lock_listen_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynLockListenRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "car_number", required = false) String carNumber,
            @RequestParam(value = "listen_state", required = false) Integer listenState,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "监听待绑定锁列表：token={}, carNumber={}, listenState={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, carNumber, listenState, isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_LOCK | LogTypeConst.TYPE_LOCK_LISTEN
                | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("监听待绑定锁列表：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP")
                .append(listenState == null ? "未知监听状态" : listenState == 1 ? "开始监听" : "结束监听").append("车辆")
                .append(carNumber).append("的待绑定锁列表。").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        try {
            if (StringUtil.isEmpty(carNumber)) {
                result = "失败，车牌号为空！";
                logger.error("监听待绑定锁列表失败：{}", DevBindErrorEnum.CARNUMBER_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.CARNUMBER_NULL);
            }
            if (listenState == null) {
                result = "失败，锁绑定监听状态为空！";
                logger.error("监听待绑定锁列表失败：{}", DevBindErrorEnum.LOCK_LISTEN_STATE_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.LOCK_LISTEN_STATE_NULL);
            }
            if (listenState < 1 || listenState > 2) {
                result = "失败，锁绑定监听状态值无效！";
                logger.error("监听待绑定锁列表失败：{}", DevBindErrorEnum.LOCK_LISTEN_STATE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.LOCK_LISTEN_STATE_INVALID);
            }
            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                result = "失败，车辆不存在！";
                logger.error("监听待绑定锁列表失败：{}", DevBindErrorEnum.VEHICLE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_INVALID);
            }
            if (vehicle.getVehicleDevice() == null || vehicle.getVehicleDevice().getDeviceId() == 0) {
                result = "失败，车辆未绑定车台！";
                logger.error("监听待绑定锁列表失败：{}", DevBindErrorEnum.VEHICLE_UNBINDED);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_UNBINDED);
            }

            cacheId = addCache(UdpBizId.LOCK_LISTEN_REQUEST, logId, description, null);

            int terminalId = vehicle.getVehicleDevice().getDeviceId();
            ByteBuffer src = SendPacketBuilder.buildProtocol0x1205(terminalId, listenState.byteValue());
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.LOCK_LISTEN_REQUEST);

            logger.info("监听待绑定锁列表指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, null);
            result = "失败，发送监听待绑定锁控制请求异常！";
            logger.error("监听待绑定锁列表异常：e={}", e.toString());
            logger.debug("监听待绑定锁列表异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }
    }

    /**
     * 清除待绑定锁列表
     *
     * @param token           {@link String} UUID令牌
     * @param carNumber       {@link String} 车牌号
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_lock_clear_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynLockClearRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "car_number", required = false) String carNumber,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info("清除待绑定锁列表：token={}, carNumber={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, carNumber, isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_LOCK | LogTypeConst.TYPE_LOCK_CLEAR
                | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("清除待绑定锁列表：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("清除车辆").append(carNumber)
                .append("的待绑定锁列表。").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        try {
            if (StringUtil.isEmpty(carNumber)) {
                result = "失败，车牌号为空！";
                logger.error("清除待绑定锁列表失败：{}", DevBindErrorEnum.CARNUMBER_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.CARNUMBER_NULL);
            }
            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                result = "失败，车辆不存在！";
                logger.error("清除待绑定锁列表失败：{}", DevBindErrorEnum.VEHICLE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_INVALID);
            }
            if (vehicle.getVehicleDevice() == null || vehicle.getVehicleDevice().getDeviceId() == 0) {
                result = "失败，车辆未绑定车台！";
                logger.error("清除待绑定锁列表失败：{}", DevBindErrorEnum.VEHICLE_UNBINDED);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_UNBINDED);
            }

            cacheId = addCache(UdpBizId.LOCK_CLEAR_REQUEST, logId, description, null);

            int terminalId = vehicle.getVehicleDevice().getDeviceId();
            ByteBuffer src = SendPacketBuilder.buildProtocol0x1206(terminalId);
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.LOCK_CLEAR_REQUEST);

            logger.info("清除待绑定锁列表指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, null);
            result = "失败，发送锁监听待绑定列表清除请求异常！";
            logger.error("清除待绑定锁列表异常：e={}", e.toString());
            logger.debug("清除待绑定锁列表异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }
    }

    /**
     * 锁绑定触发开启关闭控制下发
     *
     * @param token           {@link String} UUID令牌
     * @param carNumber       {@link String} 车牌号
     * @param triggerState    {@link Integer} 触发状态<br>
     *                        1 开始进行锁绑定触发<br>
     *                        2 结束锁绑定触发
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_lock_trigger_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynLockTriggerRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "car_number", required = false) String carNumber,
            @RequestParam(value = "trigger_state", required = false) Integer triggerState,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "锁绑定触发控制：token={}, carNumber={}, triggerState={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, carNumber, triggerState, isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_LOCK | LogTypeConst.TYPE_LOCK_TRIGGER
                | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("锁绑定触发控制：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("对车辆").append(carNumber)
                .append(triggerState == null ? "未知触发状态" : triggerState == 1 ? "开始进行锁绑定触发" : "结束锁绑定触发").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        try {
            if (StringUtil.isEmpty(carNumber)) {
                result = "失败，车牌号为空！";
                logger.error("锁绑定触发控制失败：{}", DevBindErrorEnum.CARNUMBER_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.CARNUMBER_NULL);
            }
            if (triggerState == null) {
                result = "失败，锁绑定触发状态为空！";
                logger.error("锁绑定触发控制失败：{}", DevBindErrorEnum.LOCK_TRIGGER_STATE_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.LOCK_TRIGGER_STATE_NULL);
            }
            if (triggerState < 1 || triggerState > 2) {
                result = "失败，锁绑定触发状态值无效！";
                logger.error("锁绑定触发控制失败：{}", DevBindErrorEnum.LOCK_TRIGGER_STATE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.LOCK_TRIGGER_STATE_INVALID);
            }
            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                result = "失败，车辆不存在！";
                logger.error("锁绑定触发控制失败：{}", DevBindErrorEnum.VEHICLE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_INVALID);
            }
            if (vehicle.getVehicleDevice() == null || vehicle.getVehicleDevice().getDeviceId() == 0) {
                result = "失败，车辆未绑定车台！";
                logger.error("锁绑定触发控制失败：{}", DevBindErrorEnum.VEHICLE_UNBINDED);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_UNBINDED);
            }

            cacheId = addCache(UdpBizId.LOCK_BIND_TRIGGER_REQUEST, logId, description, null);

            int terminalId = vehicle.getVehicleDevice().getDeviceId();
            ByteBuffer src = SendPacketBuilder.buildProtocol0x1209(terminalId, triggerState.byteValue());
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.LOCK_BIND_TRIGGER_REQUEST);

            logger.info("锁绑定触发控制指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, null);
            result = "失败，发送锁绑定触发开启关闭控制请求异常！";
            logger.error("锁绑定触发控制异常：e={}", e.toString());
            logger.debug("锁绑定触发控制异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }
    }

    /**
     * 锁绑定
     *
     * @param token           {@link String} UUID令牌
     * @param carNumber       {@link String} 车牌号
     * @param bindType        {@link Integer} 锁绑定变更类型（1 增加；2删除；3修改）
     * @param bindingLocks    {@link String} 锁列表json，结构如下：
     *                        [{"lockId":33554438,"storeId":1,"seat":1,"seatIndex":1,"allowOpen":2}]
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_lock_bind_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynLockBindRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "car_number", required = false) String carNumber,
            @RequestParam(value = "bind_type", required = false) Integer bindType,
            @RequestParam(value = "binding_locks", required = false) String bindingLocks,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "锁绑定变更下发：token={}, carNumber={}, bindType={}, isApp={}, longitude={}, latitude={}, isLocationValid={}, \nbindingLocks={}",
                token, carNumber, bindType, isApp, longitude, latitude, isLocationValid, bindingLocks);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_LOCK | LogTypeConst.TYPE_DEVICE_BIND
                | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("锁绑定变更下发：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("对车辆").append(carNumber)
                .append(bindType == null ? "未知锁绑定变更类型" : bindType == 1 ? "增加" : bindType == 2 ? "删除" : "修改")
                .append("锁绑定。").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        boolean isOk = false;
        try {
            if (StringUtil.isEmpty(carNumber)) {
                result = "失败，车牌号为空！";
                logger.error("锁绑定变更下发失败：{}", DevBindErrorEnum.CARNUMBER_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.CARNUMBER_NULL);
            }
            if (bindType == null) {
                result = "失败，锁绑定变更类型为空！";
                logger.error("锁绑定变更下发失败：{}", DevBindErrorEnum.LOCK_BIND_TYPE_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.LOCK_BIND_TYPE_NULL);
            }
            if (bindType < 1 || bindType > 3) {
                result = "失败，锁绑定变更类型无效！";
                logger.error("锁绑定变更下发失败：{}", DevBindErrorEnum.LOCK_BIND_TYPE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.LOCK_BIND_TYPE_INVALID);
            }
            if (StringUtil.isEmpty(bindingLocks)) {
                result = "失败，待绑定锁列表为空！";
                logger.error("锁绑定失败：{}", DevBindErrorEnum.BINDING_LOCKS_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.BINDING_LOCKS_NULL);
            }
            List<Lock> locks = JSONUtil.parseToList(bindingLocks, Lock.class);
            int len = locks.size();
            if (len == 0) {
                result = "失败，待绑定锁列表为空！";
                logger.error("锁绑定失败：{}", DevBindErrorEnum.BINDING_LOCKS_NULL);
                return ResponseMsgUtil.error(DevBindErrorEnum.BINDING_LOCKS_NULL);
            }
            for (int i = 0; i < len; i++) {
                Lock lock = locks.get(i);
                boolean incomplete = lock == null       // 锁信息空
                        || lock.getLockId() == null     // 锁设备id空
                        || lock.getStoreId() == null    // 仓号空
                        || lock.getSeat() == null       // 仓位空
                        || lock.getSeatIndex() == null  // 相同仓位锁索引号空
                        || lock.getAllowOpen() == null; // 是否允许开锁空
                if (incomplete) {
                    result = "失败，待绑定锁信息不完整！";
                    logger.error("锁绑定失败：{}", DevBindErrorEnum.BINDING_LOCK_INCOMPLETE);
                    return ResponseMsgUtil.error(DevBindErrorEnum.BINDING_LOCK_INCOMPLETE);
                }
            }
            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                result = "失败，车辆不存在！";
                logger.error("锁绑定变更下发失败：{}", DevBindErrorEnum.VEHICLE_INVALID);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_INVALID);
            }
            if (vehicle.getVehicleDevice() == null || vehicle.getVehicleDevice().getDeviceId() == 0) {
                result = "失败，车辆未绑定车台！";
                logger.error("锁绑定变更下发失败：{}", DevBindErrorEnum.VEHICLE_UNBINDED);
                return ResponseMsgUtil.error(DevBindErrorEnum.VEHICLE_UNBINDED);
            }

            Long vehicleId = vehicle.getId();
            List<Lock> list = vehicleService.findVehicleIdByLocks(locks);
            StringBuffer strBuf = new StringBuffer();
            if (bindType == 2) {
                for (Lock lock : list) {
                    if (lock.getCarId() == null) {
                        strBuf.append(lock.getLockId()).append('，');
                    }
                }
            } else {
                for (Lock lock : list) {
                    if (lock.getCarId() != null && lock.getCarId().equals(vehicleId)) {
                        strBuf.append(lock.getLockId()).append('，');
                    }
                }
            }
            if (strBuf.length() > 0) {
                strBuf.insert(0, "锁：");
                strBuf.deleteCharAt(strBuf.length() - 1);
                strBuf.append("绑定变更同步成功！");
                logger.info("锁绑定变更同步成功！");
                isOk = true;
                result = "锁绑定变更同步成功！";
                return ResponseMsgUtil.success(strBuf.toString());
            }

            cacheId = addCache(UdpBizId.LOCK_BIND_MODIFY_REQUEST, logId, description, null);

            int terminalId = vehicle.getVehicleDevice().getDeviceId();
            ByteBuffer src = SendPacketBuilder.buildProtocol0x1207(terminalId, bindType.byteValue(), locks);
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.LOCK_BIND_MODIFY_REQUEST);

            isOk = true;
            result = "请求发起成功！";
            logger.info("锁绑定变更下发指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, null);
            result = "失败，发送锁绑定变更下发请求异常！";
            logger.error("锁绑定变更下发异常：e={}", e.toString());
            logger.debug("锁绑定变更下发异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLogResult(vehicleManageLog, isOk, type, description, result);
        }
    }

    /**
     * 车台软件升级
     *
     * @param token            {@link String} UUID令牌
     * @param terminalIds      {@link String} 车台设备ID，英文逗号“,”分隔
     * @param ftpPath          {@link String} ftp更新路径
     * @param upgradeFilesInfo {@link String} 升级文件信息json，结构如下：
     *                         [{"type":1,"name":"file_1","size":1024,"crc32":45}]
     * @param isApp            {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude        {@link Integer} 手机定位经度
     * @param latitude         {@link Integer} 手机定位纬度
     * @param isLocationValid  {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_terminal_upgrade_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynTerminalUpgradeRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "terminal_ids", required = false) String terminalIds,
            @RequestParam(value = "ftp_path", required = false) String ftpPath,
            @RequestParam(value = "upgrade_files", required = false) String upgradeFilesInfo,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "车台软件升级：token={}, terminalIds={}, ftpPath={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, terminalIds, ftpPath, isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TERMINAL
                | LogTypeConst.TYPE_TERMINAL_SOFTWARE_UPGRADE | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("车台软件升级：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("更新车台软件。").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        try {
            if (StringUtil.isEmpty(terminalIds)) {
                result = "失败，车牌号为空！";
                logger.error("车台软件升级失败：{}", TerminalSoftwareUpgradeErrorEnum.TERMINAL_IDS_NULL);
                return ResponseMsgUtil.error(TerminalSoftwareUpgradeErrorEnum.TERMINAL_IDS_NULL);
            }
            if (StringUtil.isEmpty(ftpPath)) {
                result = "失败，升级文件列表为空！";
                logger.error("车台软件升级失败：{}", TerminalSoftwareUpgradeErrorEnum.FTP_PATH_NULL);
                return ResponseMsgUtil.error(TerminalSoftwareUpgradeErrorEnum.FTP_PATH_NULL);
            }
            if (StringUtil.isEmpty(upgradeFilesInfo)) {
                result = "失败，升级文件列表为空！";
                logger.error("车台软件升级失败：{}", TerminalSoftwareUpgradeErrorEnum.UPLOAD_FILES_NULL);
                return ResponseMsgUtil.error(TerminalSoftwareUpgradeErrorEnum.UPLOAD_FILES_NULL);
            }
            cacheId = addCache(UdpBizId.TERMINAL_SOFTWARE_UPGRADE_REQUEST, logId, description, null);

            List<TerminalUpgradeFile> files = JSONUtil.parseToList(upgradeFilesInfo, TerminalUpgradeFile.class);
            ByteBuffer src = SendPacketBuilder.buildProtocol0x1208(terminalIds, ftpPath, files);
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.TERMINAL_SOFTWARE_UPGRADE_REQUEST);

            logger.info("车台软件升级指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, null);
            result = "失败，发送车台软件升级请求异常！";
            logger.error("车台软件升级异常：e={}", e.toString());
            logger.debug("车台软件升级异常堆栈信息", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }
    }

    /**
     * 远程换站
     *
     * @param token            {@link String} UUID令牌
     * @param transportId      {@link Long} 原配送ID
     * @param changedStationId {@link Long} 新加油站ID
     * @param isApp            {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude        {@link Integer} 手机定位经度
     * @param latitude         {@link Integer} 手机定位纬度
     * @param isLocationValid  {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_change_station_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynChangeStationRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "transport_id", required = false) Long transportId,
            @RequestParam(value = "changed_station_id", required = false) Long changedStationId,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "远程换站：token={}, transportId={}, changedStationId={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, transportId, changedStationId, isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_DISTRIBUTION
                | LogTypeConst.TYPE_CHANGE_STATION | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("远程换站：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("对配送记录").append(transportId)
                .append("远程换站到").append(changedStationId).append("号加油站").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        Map<String, Object> params = new HashMap<>();
        try {
            if (transportId == null) {
                result = "失败，原配送ID为空！";
                logger.error("远程换站失败：{}", RemoteChangeErrorEnum.TRANSPORT_ID_NULL);
                return ResponseMsgUtil.error(RemoteChangeErrorEnum.TRANSPORT_ID_NULL);
            }
            if (changedStationId == null) {
                result = "失败，新加油站ID为空！";
                logger.error("远程换站失败：{}", RemoteChangeErrorEnum.CHANGED_STATION_ID_NULL);
                return ResponseMsgUtil.error(RemoteChangeErrorEnum.CHANGED_STATION_ID_NULL);
            }
            ChangeInfo changeInfo = vehicleService.getDistributionByTransportId(transportId, changedStationId);
            if (changeInfo == null) {
                result = "失败，配送ID不存在！";
                logger.error("远程换站失败：配送ID[{}]不存在！", transportId);
                return ResponseMsgUtil.error(RemoteChangeErrorEnum.TRANSPORT_ID_INVALID);
            }
            if (changeInfo.getCarId() == null) {
                result = "失败，相关车辆不存在！";
                logger.error("远程换站失败：{}", RemoteChangeErrorEnum.VEHICLE_INVALID);
                return ResponseMsgUtil.error(RemoteChangeErrorEnum.VEHICLE_INVALID);
            }
            if (changeInfo.getChangedGasstationId() == null) {
                result = "失败，新加油站不存在！";
                logger.error("远程换站失败：{}", RemoteChangeErrorEnum.CHANGED_STATION_INVALID);
                return ResponseMsgUtil.error(RemoteChangeErrorEnum.CHANGED_STATION_INVALID);
            }
            int transportStatus = changeInfo.getTransportStatus();
            if (transportStatus != 1) {
                result = "失败，当前配送状态不可换站！";
                logger.error("远程换站失败：当前配送状态[{}]不可换站！", transportStatus);
                return ResponseMsgUtil.error(ErrorTagConst.CHANGE_STATION_ERROR_TAG,
                        RemoteChangeErrorEnum.TRANSPORT_STATUS_INCONSISTENT.code(),
                        "当前配送状态：" + transportStatus + "，不可换站！");
            }
            // 车载终端ID
            int terminalId = vehicleService.getTerminalIdById(changeInfo.getCarId());
            if (terminalId == 0) {
                result = "失败，车辆未绑定车台！";
                logger.error("远程换站失败：{}", RemoteChangeErrorEnum.VEHICLE_UNBINDED);
                return ResponseMsgUtil.error(RemoteChangeErrorEnum.VEHICLE_UNBINDED);
            }
            changeInfo.setUserId(user.getId());
            changeInfo.setChangedGasstationId(changedStationId);
            changeInfo.setIsApp(isApp);
            changeInfo.setLongitude(longitude);
            changeInfo.setLatitude(latitude);
            changeInfo.setIsLocationValid(isLocationValid);
            // 远程换站
            Map<String, Object> map = changeRecordService.remoteChangeStation(changeInfo, user.getName());
            // 换站ID
            long changeId = (long) map.get("changeId");
            // 获取换站后的配送ID
            long changedTransportId = (long) map.get("changedTransportId");
            // UDP协议数据体
            ByteBuffer dataBuffer = (ByteBuffer) map.get("dataBuffer");

            params.put("changeId", changeId);
            params.put("transportId", transportId);
            params.put("changedTransportId", changedTransportId);
            cacheId = addCache(UdpBizId.REMOTE_CHANGE_STATION_REQUEST, logId, description, params);

            ByteBuffer src = SendPacketBuilder.buildProtocol0x1401(terminalId, dataBuffer);
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.REMOTE_CHANGE_STATION_REQUEST);

            logger.info("远程换站指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, params);
            result = "失败，发送远程换站请求异常！";
            logger.error("远程换站异常：e={}", e.toString());
            logger.debug("远程换站异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }
    }

    /**
     * 远程报警消除
     *
     * @param token           {@link String} UUID令牌
     * @param vehicleId       {@link Long} 车辆ID
     * @param alarmIds        {@link String} 报警ID集合，英文逗号“,”分隔
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_alarm_eliminate_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynAlarmEliminateRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "vehicle_id", required = false) Long vehicleId,
            @RequestParam(value = "alarm_ids", required = false) String alarmIds,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info("远程报警消除：token={}, vehicleId, alarmIds={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, vehicleId, alarmIds, isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_ALARM | LogTypeConst.TYPE_ALARM_ELIMINATE
                | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("远程报警消除：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("远程消除序号为").append(alarmIds)
                .append("的报警").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        Map<String, Object> params = new HashMap<>();
        boolean isOk = false;
        try {
            if (vehicleId == null) {
                result = "失败，车辆ID为空！";
                logger.error("远程报警消除失败：{}", RemoteEliminateAlarmErrorEnum.VEHICLE_ID_NULL);
                return ResponseMsgUtil.error(RemoteEliminateAlarmErrorEnum.VEHICLE_ID_NULL);
            }
            if (StringUtil.isEmpty(alarmIds)) {
                result = "失败，报警ID为空！";
                logger.error("远程报警消除失败：{}", RemoteEliminateAlarmErrorEnum.ALARM_ID_NULL);
                return ResponseMsgUtil.error(RemoteEliminateAlarmErrorEnum.ALARM_ID_NULL);
            }
            alarmIds = StringUtil.pretreatStrWithComma(alarmIds);
            int devNum = alarmRecordService.countAlarmDeviceByIds(alarmIds);
            if (devNum > 1) {
                result = "失败，消除报警的设备不唯一！";
                logger.error("远程报警消除失败：{}", RemoteEliminateAlarmErrorEnum.ALARM_DEV_NOT_UNIQUE);
                return ResponseMsgUtil.error(ErrorTagConst.ELIMINATE_ALARM_ERROR_TAG,
                        RemoteEliminateAlarmErrorEnum.ALARM_DEV_NOT_UNIQUE.code(),
                        "每次操作只能消除一个设备的报警！");
            }
            String[] alarmIdStrs = StringUtil.splitStrWithComma(alarmIds);
            List<Long> alarmIdList = new ArrayList<>();
            int num = alarmIdStrs.length;
            ByteBuffer alarmIdBuf = ByteBuffer.allocate(num * 4);
            for (int i = 0; i < num; i++) {
                int alarmId = Integer.parseInt(alarmIdStrs[i], 10);
                alarmIdList.add((long) alarmId);
                alarmIdBuf.put(BytesConverterByLittleEndian.getBytes(alarmId));
            }
            List<AlarmRecord> alarmRecords = alarmRecordService.getAlarmRecordsByIdsAndCar(alarmIds, vehicleId);
            if (EmptyObjectUtil.isEmptyList(alarmRecords)) {
                result = "失败，报警记录不存在！";
                logger.error("远程报警消除失败：{}", RemoteEliminateAlarmErrorEnum.ALARM_NOT_EXIST);
                return ResponseMsgUtil.error(RemoteEliminateAlarmErrorEnum.ALARM_NOT_EXIST);
            }
            if (num > alarmRecords.size()) {
                for (AlarmRecord alarmRecord : alarmRecords) {
                    alarmIdList.remove(alarmRecord.getId());
                }
                StringBuffer invalidIds = new StringBuffer();
                for (Long alarmId : alarmIdList) {
                    invalidIds.append(alarmId).append(',');
                }
                invalidIds.deleteCharAt(invalidIds.length() - 1);
                result = "失败，报警ID：" + invalidIds.toString() + "无效，报警记录不存在！";
                logger.error("远程报警消除失败：报警ID【{}】无效，报警记录不存在！", invalidIds.toString());
                return ResponseMsgUtil.error(ErrorTagConst.ELIMINATE_ALARM_ERROR_TAG,
                        RemoteEliminateAlarmErrorEnum.ALARM_ID_INVALID.code(),
                        "报警ID：" + invalidIds.toString() + "无效，报警记录不存在！");
            }
            final String ALARM_STATUS_NOT_ELIMINATE = "未消除报警";
            StringBuffer eliminatedAlarm = new StringBuffer();
            byte alarmType = 0;
            for (AlarmRecord alarmRecord : alarmRecords) {
                String alarmStatus = alarmRecord.getStatus();
                if (!ALARM_STATUS_NOT_ELIMINATE.equals(alarmStatus)) {
                    eliminatedAlarm.append("报警ID：").append(alarmRecord.getId()).append('，').append(alarmStatus).append('；');
                } else {
                    alarmType |= (byte) (1 << (alarmRecord.getType() - 1));
                }
            }
            int eliminatedAlarmLen = eliminatedAlarm.length();
            if (eliminatedAlarmLen > 0) {
                eliminatedAlarm.deleteCharAt(eliminatedAlarmLen - 1);
                eliminatedAlarm.append('。');
                result = eliminatedAlarm.toString();
                logger.info(result);
                isOk = true;
                return ResponseMsgUtil.success(result);
            }
            AlarmRecord alarmRecord = alarmRecordService.getRecordById(alarmIdList.get(0));
            int terminalId = alarmRecord.getTerminalId();
            byte deviceType = alarmRecord.getDeviceType().byteValue();
            int deviceId = alarmRecord.getDeviceId();

            Map<String, Object> eAlarmMap = new HashMap<>();
            eAlarmMap.put("userId", user.getId());
            eAlarmMap.put("vehicleId", vehicleId);
            eAlarmMap.put("alarmIds", alarmIdBuf.array());
            eAlarmMap.put("app", isApp);
            eAlarmMap.put("lnt", longitude);
            eAlarmMap.put("lat", latitude);
            eAlarmMap.put("isLocationValid", isLocationValid);
            alarmRecordService.addEliminateAlarm(eAlarmMap);
            int alarmEliminateId = ((Long) eAlarmMap.get("id")).intValue();

            params.put("alarmIds", alarmIds);
            params.put("alarmEliminateId", alarmEliminateId);
            params.put("alarmIdList", alarmIdList);
            cacheId = addCache(UdpBizId.REMOTE_ALARM_ELIMINATE_REQUEST, logId, description, params);

            ByteBuffer src = SendPacketBuilder.buildProtocol0x1402(terminalId, alarmEliminateId, deviceType, deviceId,
                    alarmType, user.getName());
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.REMOTE_ALARM_ELIMINATE_REQUEST);

            result = "请求发起成功！";
            isOk = true;
            logger.info("远程报警消除指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, params);
            result = "失败，发送远程报警消除请求异常！";
            logger.error("远程报警消除异常：e={}", e.toString());
            logger.debug("远程报警消除异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLogResult(vehicleManageLog, isOk, type, description, result);
        }
    }

    /**
     * 远程控制
     *
     * @param token           {@link String} UUID令牌
     * @param controlType     {@link Integer} 远程操作类型（1：进油库 | 2：出油库 | 3：进加油站 | 4：出加油站 | 5：远程状态变更）
     * @param carNumber       {@link String} 车牌号
     * @param stationId       {@link Integer} 站点ID
     * @param status          {@link Integer} 车辆状态（0：未知 | 1：在油库 | 2：在途中 | 3：在加油站 | 4：返程中 | 5：应急 | 6: 待入库）
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_remote_control_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynRemoteControlRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "control_type", required = false) Integer controlType,
            @RequestParam(value = "car_number", required = false) String carNumber,
            @RequestParam(value = "station_id", required = false) Integer stationId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "车辆远程控制：token={}, controlType={}, carNumber={}, stationId={}, status={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, controlType, carNumber, stationId, isApp, status, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_REMOTE | LogTypeConst.TYPE_REMOTE_CONTROL
                | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("远程控制：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("远程控制车辆").append(carNumber)
                .append(getRemoteControlType(controlType))
                .append(controlType == null ? "" : controlType == 5 ? getCarStatus(status) : stationId.toString()).toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        Map<String, Object> params = new HashMap<>();
        try {
            if (controlType == null) {
                result = "失败，远程操作类型为空！";
                logger.error("车辆远程控制失败：{}", RemoteControlErrorEnum.CONTROL_TYPE_NULL);
                return ResponseMsgUtil.error(RemoteControlErrorEnum.CONTROL_TYPE_NULL);
            }
            if (controlType < 1 || controlType > 5) {
                result = "失败，远程操作类型无效！";
                logger.error("车辆远程控制失败：远程操作类型【{}】无效！", controlType);
                return ResponseMsgUtil.error(RemoteControlErrorEnum.CONTROL_TYPE_INVALID);
            }
            switch (controlType) {
                case RemoteControlConst.REMOTE_INTO_DEPOT:
                    type |= LogTypeConst.TYPE_IN_OIL_DEPOT;
                    break;
                case RemoteControlConst.REMOTE_QUIT_DEPOT:
                    type |= LogTypeConst.TYPE_OUT_OIL_DEPOT;
                    break;
                case RemoteControlConst.REMOTE_INTO_STATION:
                    type |= LogTypeConst.TYPE_IN_GAS_STATION;
                    break;
                case RemoteControlConst.REMOTE_QUIT_STATION:
                    type |= LogTypeConst.TYPE_OUT_GAS_STATION;
                    break;
                case RemoteControlConst.REMOTE_ALTER_STATUS:
                    type |= LogTypeConst.TYPE_CAR_STATUS_ALTER;
                    break;
                default:
                    break;
            }
            if (StringUtil.isEmpty(carNumber)) {
                result = "失败，车牌号为空！";
                logger.error("车辆远程控制失败：{}", RemoteControlErrorEnum.CARNUMBER_NULL);
                return ResponseMsgUtil.error(RemoteControlErrorEnum.CARNUMBER_NULL);
            }
            if (controlType < 5 && stationId == null) {
                result = "失败，站点ID为空！";
                logger.error("车辆远程控制失败：{}", RemoteControlErrorEnum.STATION_ID_NULL);
                return ResponseMsgUtil.error(RemoteControlErrorEnum.STATION_ID_NULL);
            }
            if (controlType == 5) {
                if (status == null) {
                    result = "失败，车辆状态为空！";
                    logger.error("车辆远程控制失败：{}", RemoteControlErrorEnum.CAR_STATUS_NULL);
                    return ResponseMsgUtil.error(RemoteControlErrorEnum.CAR_STATUS_NULL);
                }
                if (status < 1 || status > 6) {
                    result = "失败，车辆状态无效！";
                    logger.error("车辆远程控制失败：车辆状态【{}】无效！", status);
                    return ResponseMsgUtil.error(RemoteControlErrorEnum.CAR_STATUS_INVALID);
                }
            }

            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                result = "失败，车辆不存在！";
                logger.error("车辆远程控制失败：车辆{}不存在！", carNumber);
                return ResponseMsgUtil.error(RemoteControlErrorEnum.VEHICLE_INVALID);
            }
            if (vehicle.getVehicleDevice() == null) {
                result = "失败，车辆未绑定车台！";
                logger.error("车辆远程控制失败：车辆{}未绑定车载终端！", carNumber);
                return ResponseMsgUtil.error(RemoteControlErrorEnum.VEHICLE_UNBINDED);
            }
            int terminalId = vehicle.getVehicleDevice().getDeviceId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("userId", user.getId());
            map.put("carId", vehicle.getId());
            map.put("type", controlType);
            map.put("stationId", stationId);
            map.put("isApp", isApp);
            map.put("longitude", longitude);
            map.put("latitude", latitude);
            map.put("isLocationValid", isLocationValid);

            // 添加远程操作记录
            Integer remoteControlId = vehicleService.addRemoteControlRecord(map);
            String userName = user.getName();

            params.put("remoteControlId", remoteControlId);
            if (controlType < 5) {
                // 远程车辆进出
                cacheId = addCache(UdpBizId.REMOTE_CAR_IN_OUT_REQUEST, logId, description, params);

                ByteBuffer src = SendPacketBuilder.buildProtocol0x1403(terminalId, remoteControlId,
                        controlType.byteValue(), stationId, userName);
                boolean isSend = udpServer.send(src);
                if (!isSend) {
                    throw new UdpException("UDP发送数据异常！");
                }
                addTimeoutTask(src, cacheId);
                vehicleManageLog.setUdpBizId(UdpBizId.REMOTE_CAR_IN_OUT_REQUEST);
            } else {
                // 远程状态变更
                params.put("status", status);
                params.put("carId ", vehicle.getId());
                cacheId = addCache(UdpBizId.REMOTE_CAR_STATUS_ALTER_REQUEST, logId, description, params);

                ByteBuffer src = SendPacketBuilder.buildProtocol0x1404(terminalId, remoteControlId, status.byteValue(),
                        stationId, userName);
                boolean isSend = udpServer.send(src);
                if (!isSend) {
                    throw new UdpException("UDP发送数据异常！");
                }
                addTimeoutTask(src, cacheId);
                vehicleManageLog.setUdpBizId(UdpBizId.REMOTE_CAR_STATUS_ALTER_REQUEST);
            }
            logger.info("车辆远程控制指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, params);
            result = "失败，发送车辆远程控制请求异常！";
            logger.error("车辆远程控制异常：e={}", e.toString());
            logger.debug("车辆远程控制异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }
    }

    /**
     * 远程开锁重置
     *
     * @param token           {@link String} UUID令牌
     * @param carNumber       {@link String} 车牌号
     * @param lockIds         {@link String} 锁设备ID，英文逗号“,”分隔
     * @param isApp           {@link Integer} 是否手机操作（0 否， 1 是）
     * @param longitude       {@link Integer} 手机定位经度
     * @param latitude        {@link Integer} 手机定位纬度
     * @param isLocationValid {@link Integer} 手机定位是否有效
     * @return {@link ResponseMsg}
     */
    @RequestMapping(value = "asyn_lock_reset_request", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseMsg asynLockResetRequest(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "car_number", required = false) String carNumber,
            @RequestParam(value = "lock_ids", required = false) String lockIds,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
            @RequestParam(value = "longitude", required = false, defaultValue = "0") Float longitude,
            @RequestParam(value = "latitude", required = false, defaultValue = "0") Float latitude,
            @RequestParam(value = "is_location_valid", required = false, defaultValue = "0") Integer isLocationValid) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            return null;
        } else {
            session.setAttribute("token", token);
        }
        logger.info(
                "远程开锁重置：token={}, carNumber={}, lockIds={}, isApp={}, longitude={}, latitude={}, isLocationValid={}",
                token, carNumber, lockIds, isApp, longitude, latitude, isLocationValid);
        User user = ThreadVariable.getUser();
        VehicleManageLog vehicleManageLog = new VehicleManageLog(user, isApp);
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_LOCK | LogTypeConst.TYPE_LOCK_RESET
                | LogTypeConst.RESULT_DONE;
        String description = new StringBuffer("远程开锁重置：").append(user.getName()).append("通过")
                .append(isApp == null || isApp == 0 ? "网页" : "手机APP").append("对车辆").append(carNumber).append("的锁[")
                .append(lockIds).append("]远程开锁重置。").toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            return ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！");
        }
        String result = "";
        int cacheId = 0;
        Map<String, Object> params = new HashMap<>();
        try {
            if (StringUtil.isEmpty(carNumber)) {
                result = "失败，车牌号为空！";
                logger.error("远程开锁重置失败：{}", RemoteLockResetErrorEnum.CARNUMBER_NULL);
                return ResponseMsgUtil.error(RemoteLockResetErrorEnum.CARNUMBER_NULL);
            }
            if (StringUtil.isEmpty(lockIds)) {
                result = "失败，锁设备ID为空！";
                logger.error("远程开锁重置失败：{}", RemoteLockResetErrorEnum.LOCK_DEV_ID_NULL);
                return ResponseMsgUtil.error(RemoteLockResetErrorEnum.LOCK_DEV_ID_NULL);
            }
            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                result = "失败，车辆不存在！";
                logger.error("远程开锁重置失败：{}", RemoteLockResetErrorEnum.VEHICLE_INVALID);
                return ResponseMsgUtil.error(RemoteLockResetErrorEnum.VEHICLE_INVALID);
            }
            if (vehicle.getVehicleDevice() == null || vehicle.getVehicleDevice().getDeviceId() == 0) {
                result = "失败，车辆未绑定车台！";
                logger.error("远程开锁重置失败：{}", RemoteLockResetErrorEnum.VEHICLE_UNBINDED);
                return ResponseMsgUtil.error(RemoteLockResetErrorEnum.VEHICLE_UNBINDED);
            }
            String[] lockIdStrs = StringUtil.splitStrWithComma(lockIds);
            byte lockNum = (byte) lockIdStrs.length;
            List<Integer> list = new ArrayList<>();
            Map<String, Object> map = null;
            StringBuffer resetIds = new StringBuffer();
            for (String lockIdStr : lockIdStrs) {
                int lockId = Integer.parseInt(lockIdStr, 10);
                map = new HashMap<>();
                map.put("carId", vehicle.getId());
                map.put("lockId", lockId);
                map.put("userId", user.getId());
                map.put("isApp", isApp);
                map.put("isLocationValid", isLocationValid);
                map.put("longitude", longitude);
                map.put("latitude", latitude);
                long i = vehicleService.addLockResetRecord(map);
                if (i == 0) {
                    result = "失败，锁设备ID无效！";
                    logger.error("远程开锁重置失败：{}", RemoteLockResetErrorEnum.LOCK_DEV_ID_INVALID);
                    return ResponseMsgUtil.error(RemoteLockResetErrorEnum.LOCK_DEV_ID_INVALID);
                }
                int resetId = ((Long) map.get("id")).intValue();
                list.add(resetId);
                list.add(lockId);
                resetIds.append(resetId).append(',');
            }
            resetIds.deleteCharAt(resetIds.length() - 1);
            int terminalId = vehicle.getVehicleDevice().getDeviceId();

            params.put("resetIds", resetIds.toString());
            cacheId = addCache(UdpBizId.LOCK_OPEN_RESET_REQUEST, logId, description, params);

            ByteBuffer src = SendPacketBuilder.buildProtocol0x1405(terminalId, lockNum, list, user.getName());
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                throw new UdpException("UDP发送数据异常！");
            }
            addTimeoutTask(src, cacheId);
            vehicleManageLog.setUdpBizId(UdpBizId.LOCK_OPEN_RESET_REQUEST);

            logger.info("远程开锁重置指令发送成功！");
            return ResponseMsgUtil.success();
        } catch (Exception e) {
            removeCache(cacheId, params);
            result = "失败，发送远程开锁重置请求异常！";
            logger.error("远程开锁重置异常：e={}", e.toString());
            logger.debug("远程开锁重置异常堆栈信息：", e);
            return ResponseMsgUtil.excetion(e);
        } finally {
            broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }
    }

    /**
     * 获取用户车辆管理指令回复
     *
     * @param udpBizId    {@link Short} UDP请求业务ID
     * @param requestTime {@link String} UDP请求发起时间
     * @param isApp       {@link Integer} 是否手机操作（0 否， 1 是）
     * @return {@link Map}
     */
    @RequestMapping(value = "asyn_vehicle_manage_response", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> asynVehicleManageResponse(
            @RequestParam(value = "udp_id", required = false) Short udpBizId,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp) {
        logger.info("获取用户车辆管理指令回复：udpBizId={}, token={}, isApp={}", udpBizId, token, isApp);
        if (udpBizId == null) {
            logger.warn("UDP业务ID为空！");
            return null;
        }
        if (Arrays.binarySearch(UdpBizId.REQUEST_BIZ_IDS, udpBizId) < 0) {
            logger.warn("UDP业务ID【{}】越界！", udpBizId);
            return null;
        }
        if (!UUIDUtil.isUUID(token)) {
            logger.warn("token={}，不是UUID", token);
            return null;
        }
        User user = ThreadVariable.getUser();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("isApp", isApp);
        map.put("udpBizId", udpBizId);
        map.put("token", token);
        return vehicleManageLogService.findUdpResult(map);
    }

    /**
     * 广播操作和更新操作日志
     *
     * @param log         {@link VehicleManageLog}
     * @param type        {@link Integer} 日志类型
     * @param description {@link String} 操作描述
     * @param result      {@link String} 操作结果
     */
    private void broadcastAndUpdateLog(VehicleManageLog log,
                                       Integer type,
                                       String description,
                                       String result) {
        if (result.length() > 0) {
            type++;
            log.setType(type);
            log.setResult(result);
            monitorWebSocketHandler.broadcastLog(1, description, result);
        } else {
            monitorWebSocketHandler.broadcastLog(0, description, "请求发起成功！");
        }
        OperateLogUtil.updateVehicleManageLog(log, vehicleManageLogService, logger);
    }

    /**
     * 广播操作和更新日志操作结果
     *
     * @param log         {@link VehicleManageLog}
     * @param isOk        {@link Boolean} 操作是否成功
     * @param type        {@link Integer} 日志类型
     * @param description {@link String} 操作描述
     * @param result      {@link String} 操作结果
     */
    private void broadcastAndUpdateLogResult(VehicleManageLog log,
                                             Boolean isOk,
                                             Integer type,
                                             String description,
                                             String result) {
        if (!isOk) {
            type++;
            log.setType(type);
            monitorWebSocketHandler.broadcastLog(1, description, result);
        } else {
            monitorWebSocketHandler.broadcastLog(0, description, result);
        }
        if (!"请求发起成功！".equals(result)) {
            log.setResult(result);
        }
        OperateLogUtil.updateVehicleManageLog(log, vehicleManageLogService, logger);
    }

    /**
     * 添加缓存
     *
     * @param bizId       {@link Short} 业务ID
     * @param logId       {@link Long} 日志记录ID
     * @param description {@link String} 任务描述
     * @param params      {@link Map} UDP通讯后待处理的参数
     * @return cacheId {@link Integer} 缓存ID
     */
    private int addCache(short bizId,
                         long logId,
                         String description,
                         Map<String, Object> params) {
        short serialNo = (short) (SerialNumberCache.getSerialNumber(bizId) + 1);
        int cacheId = AsynUdpCommCache.buildCacheId(bizId, serialNo);
        AsynUdpCommCache.putLogCache(cacheId, logId);
        AsynUdpCommCache.putTaskCache(cacheId, description);
        if (params != null) {
            AsynUdpCommCache.putParamCache(cacheId, params);
        }
        return cacheId;
    }

    /**
     * 移除缓存
     *
     * @param cacheId {@link Integer} 缓存ID
     * @param params  {@link Map} UDP通讯后待处理的参数
     */
    private void removeCache(int cacheId, Map<String, Object> params) {
        if (cacheId == 0) {
            return;
        }
        AsynUdpCommCache.removeLogCache(cacheId);
        AsynUdpCommCache.removeTaskCache(cacheId);
        if (params != null) {
            AsynUdpCommCache.removeParamCache(cacheId);
        }
        cacheId = 0;
    }

    /**
     * 添加超时任务
     *
     * @param src     {@link ByteBuffer} 待发送数据包
     * @param cacheId {@link Integer} 缓存ID
     */
    private void addTimeoutTask(ByteBuffer src, int cacheId) {
        new TimeOutTask(src, cacheId).executeRemoteControlTask();
    }

    /**
     * 获取远程操作类型
     *
     * @param controlType {@link Integer} 远程操作类型值
     * @return {@link String} 远程操作类型名称
     */
    private String getRemoteControlType(Integer controlType) {
        if (controlType == null) {
            return "未知远程操作类型";
        }
        switch (controlType) {
            case 1:
                return "进油库";
            case 2:
                return "出油库";
            case 3:
                return "进加油站";
            case 4:
                return "出加油站";
            case 5:
                return "变更车辆状态为";
            default:
                return "远程操作类型越界";
        }
    }

    /**
     * 获取车辆状态
     *
     * @param carStatus {@link Integer} 车辆状态值（0：未知 | 1：在油库 | 2：在途中 | 3：在加油站 | 4：返程中 |
     *                  5：应急）
     * @return {@link String} 车辆状态名称
     */
    private String getCarStatus(Integer carStatus) {
        switch (carStatus) {
            case 1:
                return "在油库";
            case 2:
                return "在途中";
            case 3:
                return "在加油站";
            case 4:
                return "返程中";
            case 5:
                return "应急";
            default:
                return "未知";
        }
    }
}
