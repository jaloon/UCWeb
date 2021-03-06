package com.tipray.net;

import com.tipray.bean.ResponseMsg;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.constant.RemoteControlConst;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.net.constant.UdpBizId;
import com.tipray.net.constant.UdpReplyCommonErrorEnum;
import com.tipray.net.constant.UdpReplyWorkErrorEnum;
import com.tipray.service.AlarmRecordService;
import com.tipray.service.ChangeRecordService;
import com.tipray.service.VehicleManageLogService;
import com.tipray.service.VehicleService;
import com.tipray.util.JSONUtil;
import com.tipray.util.OperateLogUtil;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.SpringBeanUtil;
import com.tipray.websocket.handler.AlarmWebSocketHandler;
import com.tipray.websocket.handler.MonitorWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * UDP接收结果处理
 *
 * @author chenlong
 * @version 1.0 2018-04-10
 */
class UdpReceiveResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(UdpReceiveResultHandler.class);
    private static final VehicleService VEHICLE_SERVICE = SpringBeanUtil.getBean(VehicleService.class);
    private static final ChangeRecordService CHANGE_SERVICE = SpringBeanUtil.getBean(ChangeRecordService.class);
    private static final AlarmRecordService ALARM_SERVICE = SpringBeanUtil.getBean(AlarmRecordService.class);
    private static final VehicleManageLogService LOG_SERVICE = SpringBeanUtil.getBean(VehicleManageLogService.class);
    private static final AlarmWebSocketHandler ALARM_WEB_SOCKET_HANDLER = SpringBeanUtil.getBean(AlarmWebSocketHandler.class);
    private static final MonitorWebSocketHandler MONITOR_WEB_SOCKET_HANDLER = SpringBeanUtil.getBean(MonitorWebSocketHandler.class);

    /**
     * 处理远程操作相关应答
     *
     * @param bizId   {@link Short} 应答业务ID
     * @param cacheId {@link Integer} 缓存ID
     * @param msg     {@link ResponseMsg}
     */
    static void handleReplyForRemoteControl(short bizId, int cacheId, ResponseMsg msg) {
        synchronized (AsynUdpCommCache.class) {
            Map<String, Object> params = AsynUdpCommCache.getParamCache(cacheId);
            boolean isOk = msg.getId() == 0;
            String task = AsynUdpCommCache.getTaskCache(cacheId);
            String result = buildLogResultByResponseMsg(msg);
            if (task != null) {
                MONITOR_WEB_SOCKET_HANDLER.dealUdpReply(bizId, cacheId, task, isOk, result);
            }
            try {
                switch (bizId) {
                    // case UdpBizId.CAR_BIND_RESPONSE:
                    //     if (isOk) {
                    //         String carNumber = (String) params.get("carNumber");
                    //         Integer deviceId = (Integer) params.get("deviceId");
                    //         VEHICLE_SERVICE.terminalBind(carNumber, deviceId);
                    //     }
                    //     break;
                    case UdpBizId.REMOTE_CHANGE_STATION_RESPONSE:
                        Integer type = (Integer) params.get("type");
                        Long transportId = (Long) params.get("transportId");
                        if (type == null || type == 1) {
                            Long changeId = (Long) params.get("changeId");
                            Long changedTransportId = (Long) params.get("changedTransportId");
                            if (isOk) {
                                logger.info("换站记录[{}]，应答成功！", changeId);
                                CHANGE_SERVICE.updateChangeAndTransportStatusForDone(changeId, transportId, changedTransportId);
                            } else {
                                logger.warn("换站记录[{}]，应答失败！", changeId);
                                CHANGE_SERVICE.updateChangeStatus(changeId, RemoteControlConst.REMOTE_PROGRESS_FAIL);
                            }
                        } else if (type == 2) {
                            if (isOk) {
                                logger.info("新增配送记录[{}]，应答成功！", transportId);
                                CHANGE_SERVICE.updateDistStatus(transportId, 1);
                            } else {
                                logger.warn("新增配送记录[{}]，应答失败！", transportId);
                            }
                        }
                        break;
                    case UdpBizId.REMOTE_ALARM_ELIMINATE_RESPONSE:
                        String alarmIds = (String) params.get("alarmIds");
                        Integer alarmEliminateId = (Integer) params.get("alarmEliminateId");
                        List<Long> alarmIdList = (List<Long>) params.get("alarmIdList");
                        if (isOk) {
                            ALARM_SERVICE.updateEliminateAlarm(alarmEliminateId, 2, alarmIds, alarmIdList);
                            ALARM_WEB_SOCKET_HANDLER.broadcastClearAlarms(alarmIdList);
                        } else {
                            ALARM_SERVICE.updateEliminateAlarm(alarmEliminateId, 0, alarmIds, alarmIdList);
                        }
                        break;
                    case UdpBizId.REMOTE_CAR_IN_OUT_RESPONSE:
                        Integer remoteControlId = (Integer) params.get("remoteControlId");
                        if (isOk) {
                            VEHICLE_SERVICE.updateRemoteControlStatus(RemoteControlConst.REMOTE_PROGRESS_DONE, remoteControlId);
                        } else {
                            VEHICLE_SERVICE.updateRemoteControlStatus(RemoteControlConst.REMOTE_PROGRESS_FAIL, remoteControlId);
                        }
                        break;
                    case UdpBizId.REMOTE_CAR_STATUS_ALTER_RESPONSE:
                        Integer remoteAlterId = (Integer) params.get("remoteControlId");
                        if (isOk) {
                            VEHICLE_SERVICE.updateRemoteControlStatus(RemoteControlConst.REMOTE_PROGRESS_DONE, remoteAlterId);
                        } else {
                            VEHICLE_SERVICE.updateRemoteControlStatus(RemoteControlConst.REMOTE_PROGRESS_FAIL, remoteAlterId);
                        }
                        break;
                    case UdpBizId.LOCK_OPEN_RESET_RESPONSE:
                        String resetIds = (String) params.get("resetIds");
                        if (isOk) {
                            VEHICLE_SERVICE.batchUpdateResetRecord(resetIds, 2);
                        } else {
                            VEHICLE_SERVICE.batchUpdateResetRecord(resetIds, 0);
                        }
                        break;
                    default:
                        return;
                }
                // 移除任务参数缓存
                if (params != null) {
                    AsynUdpCommCache.removeParamCache(cacheId);
                }
            } catch (Exception e) {
                result = "处理应答结果异常！";
                logger.error(result, e);
                msg = ResponseMsgUtil.exception(e);
            } finally {
                AsynUdpCommCache.putResultCache(cacheId, msg);
                Long logId = AsynUdpCommCache.getLogCache(cacheId);
                // 移除任务日志缓存
                AsynUdpCommCache.removeLogCache(cacheId);
                if (logId != null && logId > 0) {
                    VehicleManageLog vehicleManageLog = new VehicleManageLog(logId);
                    vehicleManageLog.setResult(result);
                    vehicleManageLog.setResponseMsg(msg);
                    try {
                        vehicleManageLog.setResponseMsgJson(JSONUtil.stringify(msg));
                    } catch (Exception e) {
                        logger.error("对象转JSON异常：", e.toString());
                        vehicleManageLog.setResponseMsgJson("服务器数据处理异常！");
                    }
                    OperateLogUtil.updateVehicleManageLog(vehicleManageLog, LOG_SERVICE, logger);
                }
            }
        }
    }

    /**
     * 根据UDP应答结果构建日志显示结果
     *
     * @param msg {@link ResponseMsg} UDP应答结果
     * @return {@link String} 日志显示结果
     */
    private static String buildLogResultByResponseMsg(ResponseMsg msg) {
        byte errorTag = msg.getTag();
        switch (errorTag) {
            case ErrorTagConst.NO_ERROR_TAG:
                return "请求执行成功！";
            case ErrorTagConst.UDP_PARSE_ERROR_TAG:
                return "失败，UDP应答数据解析错误！";
            case ErrorTagConst.UDP_REPLY_ERROR_TAG:
                int errorId = msg.getCode();
                int commonError = errorId >>> 16;
                int workError = errorId & 0xFFFF;
                StringBuffer strBuf = new StringBuffer("失败，");
                if (commonError > 0) {
                    strBuf.append("公共错误：").append(UdpReplyCommonErrorEnum.codeToMsg(commonError)).append('；');
                }
                if (workError > 0) {
                    strBuf.append("业务错误：").append(UdpReplyWorkErrorEnum.codeToMsg(workError)).append('；');
                }
                strBuf.deleteCharAt(strBuf.length() - 1);
                strBuf.append('！');
                return strBuf.toString();
            case ErrorTagConst.UDP_COMMUNICATION_ERROR_TAG:
                return "失败，UDP应答超时！";
            default:
                return "失败，其它UDP通讯错误！";
        }
    }

    /**
     * 处理轨迹
     *
     * @param carId     {@link Long} 车辆ID
     * @param trackList {@link String} 车辆轨迹JSON字符串列表
     */
    static void handleTrack(long carId, List<String> trackList) {
        MONITOR_WEB_SOCKET_HANDLER.dealTrackUploadByUdp(carId, trackList);
    }

    /**
     * 检测车辆是否在线
     *
     * @param carId {@link Long} 车辆ID
     * @return {@link Boolean} 车辆是否在线
     */
    static boolean checkCarOnline(long carId) {
        return MONITOR_WEB_SOCKET_HANDLER.checkCarOnline(carId);
    }
}
