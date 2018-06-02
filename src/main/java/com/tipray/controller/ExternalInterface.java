package com.tipray.controller;

import com.tipray.bean.ResponseMsg;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.LogTypeConst;
import com.tipray.constant.reply.BarrierErrorEnum;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.PermissionException;
import com.tipray.net.NioUdpServer;
import com.tipray.service.OilDepotService;
import com.tipray.service.TransportCardService;
import com.tipray.service.VehicleManageLogService;
import com.tipray.util.*;
import com.tipray.websocket.AlarmWebSocketHandler;
import com.tipray.websocket.MonitorWebSocketHandler;
import net.sf.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.nio.ByteBuffer;
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
    @Resource
    private OilDepotService oilDepotService;
    @Resource
    private TransportCardService transportCardService;
    @Resource
    private NioUdpServer udpServer;
    @Resource
    private VehicleManageLogService vehicleManageLogService;

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

    // @RequestMapping(value = "barrier", method = {RequestMethod.POST, RequestMethod.GET})
    // @ResponseBody
    // public void barrier(HttpServletRequest request){
    //     short serial = SerialNumberCache.getNextSerialNumber((short)1501);
    //     System.out.println("send: " + serial);
    //     ByteBuffer buffer = ByteBuffer.allocate(2);
    //     buffer.putShort(serial);
    //     final AsyncContext asyncContext = request.startAsync();
    //     AsynUdpCommCache.putAsyncContextCache((int)serial,asyncContext);
    //     udpServer.send(buffer);
    // }

    /**
     * 远程进出道闸
     * @param token   {@link String} UUID令牌
     * @param depotId {@link String} 油库编号
     * @param cardId  {@link Long} 配送卡ID
     * @param sign    {@link Integer} 进出闸标志（1 进闸，2  出闸）
     * @return {@link ResponseMsg}
     */
    @PostMapping("barrier")
    @ResponseBody
    public DeferredResult<ResponseMsg> barrier(@RequestParam("token") String token,
                                               @RequestParam("depot_id") String depotId,
                                               @RequestParam("card_id") Long cardId,
                                               @RequestParam("sign") Integer sign,
                                               HttpSession session) {
        if (!UUIDUtil.verifyUUIDToken(token, session)) {
            logger.error("令牌无效！token={}", token);
            throw new PermissionException();
        } else {
            session.setAttribute("token", token);
        }
        logger.info("远程进出道闸：token={}, depot_id={}, card_id={}, sign={}", token, depotId, cardId, sign);
        DeferredResult<ResponseMsg> deferredResult = new DeferredResult<>();
        VehicleManageLog vehicleManageLog = new VehicleManageLog();
        Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TERMINAL | LogTypeConst.TYPE_BARRIER_OPEN
                | LogTypeConst.RESULT_DONE;
        if (sign == 1) {
            type |= LogTypeConst.TYPE_BARRIER_IN;
        } else if (sign == 2) {
            type |= LogTypeConst.TYPE_BARRIER_OUT;
        }
        String description = new StringBuffer("远程进出道闸：").append("道闸")
                .toString();
        Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
        if (logId == null || logId == 0L) {
            deferredResult.setResult(ResponseMsgUtil.error(ErrorTagConst.DB_INSERT_ERROR_TAG, 1, "数据库操作异常！"));
            return deferredResult;
        }
        String result = "";
        int cacheId = 0;
        try {
            if (depotId.isEmpty()) {
                result = "失败，油库编号无效！";
                logger.error("远程进出道闸失败：{}", BarrierErrorEnum.OILDEPOT_INVALID);
                deferredResult.setResult(ResponseMsgUtil.error(BarrierErrorEnum.OILDEPOT_INVALID));
                return deferredResult;
            }
            if (sign < 1 || sign > 2){
                result = "失败，进出闸标志无效！";
                logger.error("远程进出道闸失败：进出闸标志[{}]无效！", sign);
                deferredResult.setResult(ResponseMsgUtil.error(BarrierErrorEnum.SIGN_INVALID));
                return deferredResult;
            }

            Long oilId = oilDepotService.getIdByOfficialId(depotId);
            if (oilId == null) {
                result = "失败，油库编号无效！";
                logger.error("远程进出道闸失败：{}", BarrierErrorEnum.OILDEPOT_INVALID);
                deferredResult.setResult(ResponseMsgUtil.error(BarrierErrorEnum.OILDEPOT_INVALID));
                return deferredResult;
            }

            Map<String, Object> transportCard = transportCardService.getByTransportCardId(cardId);
            if (transportCard == null) {
                result = "失败，配送卡ID无效！";
                logger.error("远程进出道闸失败：配送卡ID[{}]无效！", cardId);
                deferredResult.setResult(ResponseMsgUtil.error(BarrierErrorEnum.CARD_INVALID));
                return deferredResult;
            }

            Integer terminalId = (Integer) transportCard.get("terminalId");
            if (terminalId == null) {
                result = "失败，配送卡未与车辆绑定！";
                logger.error("远程进出道闸失败：{}", BarrierErrorEnum.CARD_UNBIND);
                deferredResult.setResult(ResponseMsgUtil.error(BarrierErrorEnum.CARD_UNBIND));
                return deferredResult;
            }

            if (terminalId == 0) {
                result = "失败，与配送卡绑定的车辆未绑定车载终端！";
                logger.error("远程进出道闸失败：{}", BarrierErrorEnum.TERMINAL_UNBIND);
                deferredResult.setResult(ResponseMsgUtil.error(BarrierErrorEnum.TERMINAL_UNBIND));
                return deferredResult;
            }

            int remoteId = 0;

        } catch (Exception e) {
            // removeCache(cacheId, null);
            result = "失败，发送更新车台配置请求异常！";
            logger.error("更新车台配置异常：e={}", e.toString());
            logger.debug("更新车台配置异常堆栈信息");

            ResponseMsgUtil.excetion(ErrorTagConst.BARRIER_ERROR_TAG, BarrierErrorEnum.EXCEPTON.code(), e.getMessage());
        } finally {
            // broadcastAndUpdateLog(vehicleManageLog, type, description, result);
        }

        short serial = SerialNumberCache.getNextSerialNumber((short) 1501);
        System.out.println("send: " + serial);
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(serial);
        AsynUdpCommCache.putDeferredResultCache((int) serial, deferredResult);
        udpServer.send(buffer);
        return deferredResult;
    }
}
