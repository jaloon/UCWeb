package com.tipray.net;

import com.tipray.bean.ResponseMsg;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.constant.RemoteControlConst;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.AlarmRecordService;
import com.tipray.service.ChangeRecordService;
import com.tipray.service.VehicleManageLogService;
import com.tipray.service.VehicleService;
import com.tipray.util.OperateLogUtil;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.SpringBeanUtil;
import com.tipray.websocket.AlarmWebSocketHandler;
import com.tipray.websocket.MonitorWebSocketHandler;
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
public class UdpReceiveResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(UdpReceiveResultHandler.class);
    private static final VehicleService VEHICLE_SERVICE = SpringBeanUtil.getBean(VehicleService.class);
    private static final ChangeRecordService CHANGE_SERVICE = SpringBeanUtil.getBean(ChangeRecordService.class);
    private static final AlarmRecordService ALARM_SERVICE = SpringBeanUtil.getBean(AlarmRecordService.class);
    private static final VehicleManageLogService LOG_SERVICE = SpringBeanUtil.getBean(VehicleManageLogService.class);
    private static final AlarmWebSocketHandler ALARM_WEB_SOCKET_HANDLER = new AlarmWebSocketHandler();
    private static final MonitorWebSocketHandler MONITOR_WEB_SOCKET_HANDLER = new MonitorWebSocketHandler();

    /**
     * 处理远程操作相关应答
     *
     * @param bizId   {@link Short} 应答业务ID
     * @param cacheId {@link Integer} 缓存ID
     * @param msg     {@link ResponseMsg}
     */
    public static void handleReplyForRemoteControl(short bizId, int cacheId, ResponseMsg msg) {
        synchronized (AsynUdpCommCache.class) {
            Map<String, Object> params = AsynUdpCommCache.getParamCache(cacheId);
            String result = buildLogResultByResponseMsg(msg);
            boolean isOk = msg.getId() == 0;
            MONITOR_WEB_SOCKET_HANDLER.dealUdpReply(bizId, cacheId, AsynUdpCommCache.getTaskCache(cacheId), isOk, result);
            try {
                switch (bizId) {
//				case UdpBizId.CAR_BIND_RESPONSE:
//					if (isOk) {
//						String carNumber = (String) params.get("carNumber");
//						Integer deviceId = (Integer) params.get("deviceId");
//						VEHICLE_SERVICE.terminalBind(carNumber, deviceId);
//					}
//					break;
                    case UdpBizId.REMOTE_CHANGE_STATION_RESPONSE:
                        Long changeId = (Long) params.get("changeId");
                        Long transportId = (Long) params.get("transportId");
                        Long changedTransportId = (Long) params.get("changedTransportId");
                        if (isOk) {
                            CHANGE_SERVICE.updateChangeAndTransportStatusForDone(changeId, transportId, changedTransportId);
                        } else {
                            CHANGE_SERVICE.updateChangeStatus(changeId, RemoteControlConst.REMOTE_PROGRESS_FAIL);
                        }
                        break;
                    case UdpBizId.REMOTE_ALARM_ELIMINATE_RESPONSE:
                        String alarmIds = (String) params.get("alarmIds");
                        Integer alarmEliminateId = (Integer) params.get("alarmEliminateId");
                        List<Long> alarmIdList = (List<Long>) params.get("alarmIdList");
                        if (isOk) {
                            ALARM_SERVICE.updateEliminateAlarm(alarmEliminateId, 2, alarmIds);
                            ALARM_WEB_SOCKET_HANDLER.broadcastClearAlarms(alarmIdList);
                        } else {
                            ALARM_SERVICE.updateEliminateAlarm(alarmEliminateId, 0, alarmIds);
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
                        Integer status = (Integer) params.get("status");
                        Long carId = (Long) params.get("carId");
                        if (isOk) {
                            VEHICLE_SERVICE.updateRemoteAlterStatusResulte(RemoteControlConst.REMOTE_PROGRESS_DONE, status,
                                    remoteAlterId, carId);
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
                logger.error("处理业务【{}】应答结果异常：{}", bizId, e.toString());
                logger.debug("处理业务应答结果异常堆栈信息：", e);
                msg = ResponseMsgUtil.excetion(e);
            } finally {
                AsynUdpCommCache.putResultCache(cacheId, msg);
                Long logId = AsynUdpCommCache.getLogCache(cacheId);
                // 移除任务日志缓存
                AsynUdpCommCache.removeLogCache(cacheId);
                if (logId == null || logId.equals(0L)) {
                    return;
                }
                VehicleManageLog vehicleManageLog = new VehicleManageLog(logId);
                vehicleManageLog.setResult(result);
                vehicleManageLog.setResponseMsg(msg);
                OperateLogUtil.updateVehicleManageLog(vehicleManageLog, LOG_SERVICE, logger);
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
                    strBuf.append("UDP请求公共错误，");
                }
                if (workError > 0) {
                    strBuf.append("UDP请求业务信息错误，");
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
    public static void handleTrack(long carId, List<String> trackList) {
        MONITOR_WEB_SOCKET_HANDLER.dealTrackUploadByUdp(carId, trackList);
    }

}