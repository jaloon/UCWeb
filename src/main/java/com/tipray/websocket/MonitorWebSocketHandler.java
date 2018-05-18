package com.tipray.websocket;

import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.baseinfo.User;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.cache.VehicleCompanyRelationCache;
import com.tipray.constant.AlarmBitMarkConst;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.VehicleManageLogService;
import com.tipray.service.VehicleService;
import com.tipray.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SpringWebSocket实时监控业务处理
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
public class MonitorWebSocketHandler implements WebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(MonitorWebSocketHandler.class);
    /**
     * 所有WebSocket客户端缓存Map&lt;sessionId, WebSocketSession&gt;
     */
    private static final Map<Long, WebSocketSession> WEB_SOCKET_CLIENTS = new ConcurrentHashMap<Long, WebSocketSession>();
    /**
     * 普通监控客户端缓存Map&lt;WebSocketSession-Id, User-Account&gt;
     */
    private static final Map<Long, String> GENERAL_CLIENTS = new ConcurrentHashMap<Long, String>();
    /**
     * 重点监控客户端缓存Map&lt;WebSocketSession-Id, 重点监控发起时间&gt;
     */
    private static final Map<Long, Long> FOCUS_CLIENTS = new ConcurrentHashMap<Long, Long>();
    /**
     * 重点监控车辆缓存Map&lt;WebSocketSession-Id, 车辆ID&gt;
     */
    private static final Map<Long, Long> FOCUS_CARS = new ConcurrentHashMap<Long, Long>();
    /**
     * 实时监控客户端缓存Map&lt;WebSocketSession-Id, 实时监控发起时间和监控时长&gt;
     */
    private static final Map<Long, Map<String, Long>> REALTIME_CLIENTS = new ConcurrentHashMap<Long, Map<String, Long>>();
    /**
     * 实时监控车辆缓存Map&lt;WebSocketSession-Id, 车辆ID列表&gt;
     */
    private static final Map<Long, List<Long>> REALTIME_CARS = new ConcurrentHashMap<Long, List<Long>>();
    /**
     * 车辆轨迹缓存Map&lt;车辆ID, 最后轨迹&gt;
     */
    private static final Map<Long, String> CAR_TRACKS = new ConcurrentHashMap<Long, String>();
    /**
     * UDP缓存Map&lt;UDP缓存ID, WebSocketSession-Id&gt;
     */
    private static final Map<Integer, Long> UDP_CACHE = new ConcurrentHashMap<Integer, Long>();
    /**
     * 实时监控车辆缓存Map&lt;UDP缓存ID, 车辆Id&gt;
     */
    private static final Map<Integer, Long> REALTIME_CARS_CACHE = new ConcurrentHashMap<Integer, Long>();
    /**
     * 重点监控重发缓存Set&lt;UDP缓存ID&gt;
     */
    private static final Set<Integer> REPEAT_CACHE = new HashSet<Integer>();
    /**
     * 普通监控
     */
    private static final String GENERAL_MONITOR = "general";
    /**
     * 重点监控
     */
    private static final String FOCUS_MONITOR = "focus";
    /**
     * 实时监控
     */
    private static final String REALTIME_MONITOR = "realtime";
    /**
     * 车辆轨迹上报
     */
    private static final String CAR_TRACK_REPORT = "track";
    /**
     * 车辆轨迹列表上报
     */
    private static final String CAR_TRACK_LIST = "tracklist";
    /**
     * 车辆离线
     */
    private static final String CAR_OFF_LINE = "offline";
    /**
     * 业务请求
     */
    private static final String BIZ_REQUEST = "request";
    /**
     * 业务重发
     */
    private static final String BIZ_REPEAT = "repeat";
    /**
     * 业务取消
     */
    private static final String BIZ_CANCEL = "cancel";
    /**
     * 实时监控请求指令执行结果：至少一辆车执行成功
     */
    private boolean succuessAtLeastOne = false;
    @Resource
    private VehicleService vehicleService;
    @Resource
    private VehicleManageLogService vehicleManageLogService;
    @Resource
    private NioUdpServer udpServer;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long sessionId = Long.parseLong(session.getId(), 16);
        WEB_SOCKET_CLIENTS.put(sessionId, session);
        logger.debug("monitor connection established：", sessionId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (message.getPayload() instanceof ByteBuffer) {
            // IE浏览器不间断发送0字节的PongMessage以维持WebSocket连接，服务端忽略此消息
            return;
        }
        logger.debug("handleMessage：\n" + message.getPayload());
        String msgText = null;
        try {
            msgText = URLDecoder.decode((String) message.getPayload(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("接收WebSocket信息解码异常！\n{}", e.getMessage());
            return;
        }
        Map<String, Object> msgMap;
        try {
            msgMap = JSONUtil.parseToMap(msgText);
        } catch (Exception e) {
            logger.error("JSON解析异常！\n{}", e.getMessage());
            return;
        }
        String biz = (String) msgMap.get("biz");
        switch (biz) {
            case GENERAL_MONITOR:
                dealGeneralMonitor(session);
                break;
            case FOCUS_MONITOR:
                dealFocusMonitor(session, msgMap);
                break;
            case REALTIME_MONITOR:
                dealRealtimeMonitor(session, msgMap);
                break;
            case CAR_TRACK_REPORT:
                dealCarTrackReport(msgMap);
                break;
            case CAR_TRACK_LIST:
                dealCarTrackList(msgMap);
                break;
            case CAR_OFF_LINE:
                dealCarOffLine(msgMap);
                break;
            default:
                break;
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        Long sessionId = Long.parseLong(session.getId(), 16);
        removeMonitorCache(sessionId);
        logger.error("monitor connection {} transport error: {}", sessionId, exception.toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        Long sessionId = Long.parseLong(session.getId(), 16);
        removeMonitorCache(sessionId);
        logger.info("monitor connection {} closed by {}", sessionId,
                WebSocketCloseStatusEnum.getByCode(closeStatus.getCode()));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 发送消息到客户端
     *
     * @param session {@link WebSocketSession} 客户端
     * @param msg     {@link String} 消息文本
     */
    private void sendMsg(WebSocketSession session, String msg) {
        if (StringUtil.isEmpty(msg)) {
            return;
        }
        if (!session.isOpen()) {
            logger.warn("monitor connection {} is not still open", Long.parseLong(session.getId(), 16));
            return;
        }
        try {
            session.sendMessage(new TextMessage(msg));
        } catch (IOException e) {
            logger.error("发送消息{}异常！\n{}", msg, e.getMessage());
        }
    }

    /**
     * 移除monitor缓存
     *
     * @param sessionId {@link WebSocketSession#getId()}
     */
    private void removeMonitorCache(Long sessionId) {
        WEB_SOCKET_CLIENTS.remove(sessionId);
        GENERAL_CLIENTS.remove(sessionId);
        FOCUS_CLIENTS.remove(sessionId);
        FOCUS_CARS.remove(sessionId);
        REALTIME_CLIENTS.remove(sessionId);
        REALTIME_CARS.remove(sessionId);
        UDP_CACHE.keySet().parallelStream().forEach(cacheId -> {
            if (UDP_CACHE.get(cacheId).equals(sessionId)) {
                UDP_CACHE.remove(cacheId);
                REALTIME_CARS_CACHE.remove(cacheId);
            }
        });
    }

    /**
     * 处理普通监控
     *
     * @param session {@link WebSocketSession}
     */
    private void dealGeneralMonitor(WebSocketSession session) {
        User user = ThreadVariable.getUser();
        if (user == null) {
            WebSocketUtil.sendTextMsg(session, BIZ_REPEAT);
        } else {
            String account = user.getAccount();
            Long sessionId = Long.parseLong(session.getId(), 16);
            GENERAL_CLIENTS.put(sessionId, account);
            Map<String, Object> sessionMap = new HashMap<String, Object>();
            sessionMap.put("biz", "session");
            sessionMap.put("sessionId", sessionId);
            sessionMap.put("userId", user.getId());
            sessionMap.put("userAccount", user.getAccount());
            sessionMap.put("userName", user.getName());
            try {
                session.sendMessage(new TextMessage(JSONUtil.stringify(sessionMap)));
                CAR_TRACKS.values().parallelStream().forEach(carTrack -> WebSocketUtil.sendTextMsg(session, carTrack));
            } catch (Exception e) {
                logger.error("普通监控：WebSocket通信异常！\n{}", e.getMessage());
            }
        }
    }

    /**
     * 处理重点监控指令
     *
     * @param session {@link WebSocketSession}
     * @param msgMap  {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealFocusMonitor(WebSocketSession session, Map<String, Object> msgMap) {
        // WebSocketSession ID
        Long sessionId = Long.parseLong(session.getId(), 16);
        String bizType = (String) msgMap.get("bizType");
        switch (bizType) {
            case BIZ_REQUEST:
                sendChildSessionToParent(msgMap, sessionId, FOCUS_MONITOR);
                // 操作员
                User user;
                try {
                    user = JSONUtil.parseToObject((String) msgMap.get("user"), User.class);
                } catch (Exception e) {
                    logger.error("JSON解析User对象异常：{}", e.toString());
                    WebSocketUtil.sendTextMsg(session, "parse_error");
                    return;
                }
                // 车辆ID
                Long carId = ((Integer) msgMap.get("carcarId")).longValue();
                // 车牌号
                String carNumber = (String) msgMap.get("carNumber");
                // token
                String token = (String) msgMap.get("token");
                VehicleManageLog vehicleManageLog = new VehicleManageLog(user, 0);
                Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TRACK
                        | LogTypeConst.TYPE_FOCUS_MONITOR | LogTypeConst.RESULT_DONE;
                String description = new StringBuffer("车辆轨迹查看：").append(user.getName()).append("通过网页查看车辆").append(carNumber)
                        .append("的轨迹。").toString();
                Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
                if (logId == null || logId == 0L) {
                    WebSocketUtil.sendTextMsg(session, "db_error");
                    return;
                }
                String result = "";
                int cacheId = 0;
                try {
                    cacheId = addUdpCache(UdpBizId.FOCUS_MONITOR_REQUEST, logId, description);
                    // 车载终端设备ID
                    Integer terminalId = vehicleService.getTerminalIdById(carId);
                    ByteBuffer src = SendPacketBuilder.buildProtocol0x1303(terminalId, sessionId);
                    boolean isSend = udpServer.send(src);
                    if (!isSend) {
                        removeUdpCache(cacheId, description);
                        result = "失败，指令发送异常！";
                        logger.error("UDP发送数据异常！");
                        WebSocketUtil.sendTextMsg(session, "conn_error");
                        return;
                    }
                    addUdpTimeoutTask(src, cacheId, false);
                    UDP_CACHE.put(cacheId, sessionId);
                    WebSocketUtil.sendTextMsg(session, CAR_TRACKS.get(carId));
                    // 当前时间戳（重点监控发起时间）
                    Long timestamp = System.currentTimeMillis();
                    FOCUS_CLIENTS.put(sessionId, timestamp);
                    FOCUS_CARS.put(sessionId, carId);
                } catch (Exception e) {
                    removeUdpCache(cacheId, description);
                    result = "失败，重点监控异常！";
                    logger.error("重点监控异常：e={}", e.toString());
                    logger.debug("重点监控异常堆栈信息：", e);
                    WebSocketUtil.sendTextMsg(session, "conn_error");
                } finally {
                    updateLog(vehicleManageLog, type, result);
                }
                break;
            case BIZ_REPEAT:
                int repeatCacheId = addRepeatCache();
                Integer terminalId = vehicleService.getTerminalIdById(FOCUS_CARS.get(sessionId));
                ByteBuffer src = SendPacketBuilder.buildProtocol0x1303(terminalId, sessionId);
                boolean isSend = udpServer.send(src);
                if (!isSend) {
                    removeUdpCache(repeatCacheId, null);
                    logger.error("UDP发送数据异常！");
                    FOCUS_CLIENTS.remove(sessionId);
                    FOCUS_CARS.remove(sessionId);
                    WebSocketUtil.sendTextMsg(session, "conn_broken");
                }
                addUdpTimeoutTask(src, repeatCacheId, true);
                break;
            case BIZ_CANCEL:
                dealFocusCancel(msgMap);
                break;
            default:
                break;
        }
    }

    /**
     * 处理重点监控取消
     *
     * @param msgMap {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealFocusCancel(Map<String, Object> msgMap) {
        Long sessionId = ((Integer) msgMap.get("session")).longValue();
        // 操作员
        User user;
        try {
            user = JSONUtil.parseToObject((String) msgMap.get("user"), User.class);
        } catch (Exception e) {
            logger.error("JSON解析User对象异常：{}", e.toString());
            user = new User();
            user.setId(Long.MAX_VALUE);
            user.setName("未知用户");
        }
        // token
        String token = (String) msgMap.get("token");
        Long carId = FOCUS_CARS.get(sessionId);
        if (carId != null && carId != 0) {
            // 车载终端设备ID
            Integer terminalId = vehicleService.getTerminalIdById(carId);
            if (terminalId != null && terminalId != 0) {
                VehicleManageLog vehicleManageLog = new VehicleManageLog(user, 0);
                Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TRACK
                        | LogTypeConst.TYPE_FOCUS_CANCEL | LogTypeConst.RESULT_DONE;
                String description = new StringBuffer("车辆轨迹查看：").append(user.getName()).append("通过网页取消查看车辆")
                        .append(vehicleService.getCarNumberById(carId)).append("的轨迹").toString();
                Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
                if (logId == null || logId == 0L) {
                    return;
                }
                String result = "";
                int cacheId = 0;
                try {
                    cacheId = addUdpCache(UdpBizId.FOCUS_MONITOR_CANCEL_REQUEST, logId, description);
                    ByteBuffer src = SendPacketBuilder.buildProtocol0x1304(terminalId, sessionId);
                    boolean isSend = udpServer.send(src);
                    if (!isSend) {
                        removeUdpCache(cacheId, description);
                        result = "失败，指令发送异常！";
                        logger.error("UDP发送数据异常！");
                        return;
                    }
                    addUdpTimeoutTask(src, cacheId, false);
                } catch (Exception e) {
                    removeUdpCache(cacheId, description);
                    result = "失败，重点监控取消异常！";
                    logger.error("重点监控取消异常：e={}", e.toString());
                    logger.debug("重点监控取消异常堆栈信息：", e);
                } finally {
                    updateLog(vehicleManageLog, type, result);
                }
            }
        }
    }

    /**
     * 处理实时监控指令
     *
     * @param session {@link WebSocketSession#getId()}
     * @param msgMap  {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealRealtimeMonitor(WebSocketSession session, Map<String, Object> msgMap) {
        String bizType = (String) msgMap.get("bizType");
        // 操作员
        User user;
        try {
            user = JSONUtil.parseToObject((String) msgMap.get("user"), User.class);
        } catch (Exception e) {
            logger.error("JSON解析User对象异常：{}", e.toString());
            WebSocketUtil.sendTextMsg(session, "parse_error");
            return;
        }
        // token
        String token = (String) msgMap.get("token");
        switch (bizType) {
            case BIZ_REQUEST:
                // WebSocketSession ID
                final Long sessionId = Long.parseLong(session.getId(), 16);
                sendChildSessionToParent(msgMap, sessionId, REALTIME_MONITOR);
                // 车牌号
                String carNumber = (String) msgMap.get("car");
                List<Vehicle> cars = new ArrayList<>();
                boolean isCom = StringUtil.isEmpty(carNumber);
                if (isCom) {
                    // 运输公司ID
                    Long comId = ((Integer) msgMap.get("comId")).longValue();
                    TransCompany company = new TransCompany();
                    company.setId(comId);
                    cars = vehicleService.findByTransCompany(company);
                } else {
                    Vehicle car = vehicleService.getByCarNo(carNumber);
                    cars.add(car);
                }
                // 监控间隔（秒）
                Short interval = ((Integer) msgMap.get("interval")).shortValue();
                // 监控时长（分）
                Short duration = ((Integer) msgMap.get("duration")).shortValue();

                // 指令执行成功的车辆ID信息列表
                List<Long> carIds = new ArrayList<>();
                // 指令执行失败的车辆车牌号码字符串拼接处理
                StringBuffer carNoBuf = new StringBuffer();
                cars.parallelStream().forEach(car -> {
                    VehicleManageLog vehicleManageLog = new VehicleManageLog(user, 0);
                    Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TRACK
                            | LogTypeConst.TYPE_REALTIME_MONITOR | LogTypeConst.RESULT_DONE;
                    String description = new StringBuffer("车辆轨迹实时监控：").append(user.getName()).append("通过网页实时监控")
                            .append("车辆" + car.getCarNumber()).append("的轨迹。").toString();
                    Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
                    if (logId == null || logId == 0L) {
                        WebSocketUtil.sendTextMsg(session, "db_error");
                        return;
                    }
                    String result = "";
                    int cacheId = 0;
                    try {
                        cacheId = addUdpCache(UdpBizId.REALTIME_MONITOR_REQUEST, logId, description);
                        // 车载终端设备ID
                        Integer terminalId = car.getVehicleDevice().getDeviceId();
                        ByteBuffer src = SendPacketBuilder.buildProtocol0x1301(terminalId, sessionId, interval, duration);
                        udpServer.send(src);
                        boolean isSend = udpServer.send(src);
                        if (!isSend) {
                            removeUdpCache(cacheId, description);
                            result = "失败，指令发送异常！";
                            carNoBuf.append(car.getCarNumber()).append('、');
                            logger.error("UDP发送数据异常！");
                            return;
                        }
                        addUdpTimeoutTask(src, cacheId, false);
                        succuessAtLeastOne = true;
                        carIds.add(car.getId());
                        REALTIME_CARS_CACHE.put(cacheId, car.getId());
                        WebSocketUtil.sendTextMsg(session, CAR_TRACKS.get(car.getId()));
                    } catch (Exception e) {
                        removeUdpCache(cacheId, description);
                        result = "失败，实时监控异常！";
                        carNoBuf.append(car.getCarNumber()).append('、');
                        logger.error("实时监控异常：e={}", e.toString());
                        logger.debug("实时监控异常堆栈信息：", e);
                    } finally {
                        updateLog(vehicleManageLog, type, result);
                    }

                });
                if (!succuessAtLeastOne) {
                    WebSocketUtil.sendTextMsg(session, "conn_error");
                } else {
                    // 当前时间戳
                    Long timestamp = System.currentTimeMillis();
                    // 时间信息Map
                    Map<String, Long> timeInfo = new HashMap<>();
                    timeInfo.put("begin", timestamp);
                    timeInfo.put("duration", duration.longValue() * 60000L);
                    REALTIME_CLIENTS.put(sessionId, timeInfo);
                    REALTIME_CARS.put(sessionId, carIds);

                    int carNoBufLen = carNoBuf.length();
                    if (carNoBufLen > 0) {
                        carNoBuf.deleteCharAt(carNoBufLen - 1);
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append('{');
                        strBuf.append("\"biz\":\"error\",\"carNos\":\"").append(carNoBuf).append('\"');
                        strBuf.append('}');
                        String error = strBuf.toString();
                        WebSocketUtil.sendTextMsg(session, error);
                    }
                }
                break;
            case BIZ_CANCEL:
                Long cancelSessionId = ((Integer) msgMap.get("session")).longValue();
                List<Long> ids = REALTIME_CARS.get(cancelSessionId);
                if (!EmptyObjectUtil.isEmptyList(ids)) {
                    ids.parallelStream().forEach(carId -> dealRealtimeCancel(user, carId, cancelSessionId, token));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理实时监控取消
     *
     * @param user      {@link User} 操作员
     * @param carId     {@link Long} 车辆ID
     * @param sessionId {@link WebSocketSession#getId()}
     * @param token     {@link String} UUID令牌
     */
    private void dealRealtimeCancel(User user, Long carId, Long sessionId, String token) {
        if (carId != null && carId != 0) {
            // 车载终端设备ID
            Integer terminalId = vehicleService.getTerminalIdById(carId);
            if (terminalId != null && terminalId != 0) {
                VehicleManageLog vehicleManageLog = new VehicleManageLog(user, 0);
                Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TRACK
                        | LogTypeConst.TYPE_REALTIME_CANCEL | LogTypeConst.RESULT_DONE;
                String description = new StringBuffer("车辆轨迹实时监控：").append(user.getName()).append("通过网页取消车辆")
                        .append(vehicleService.getCarNumberById(carId)).append("的实时监控").toString();
                Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
                if (logId == null || logId == 0L) {
                    return;
                }
                String result = "";
                int cacheId = 0;
                try {
                    cacheId = addUdpCache(UdpBizId.REALTIME_MONITOR_CANCEL_REQUEST, logId, description);
                    ByteBuffer src = SendPacketBuilder.buildProtocol0x1302(terminalId, sessionId);
                    boolean isSend = udpServer.send(src);
                    if (!isSend) {
                        removeUdpCache(cacheId, description);
                        result = "失败，指令发送异常！";
                        logger.error("UDP发送数据异常！");
                        return;
                    }
                    addUdpTimeoutTask(src, cacheId, false);
                } catch (Exception e) {
                    removeUdpCache(cacheId, description);
                    result = "失败，实时监控取消异常！";
                    logger.error("实时监控取消异常：e={}", e.toString());
                    logger.debug("实时监控取消异常堆栈信息：", e);
                } finally {
                    updateLog(vehicleManageLog, type, result);
                }
            }
        }
    }

    /**
     * 更新操作日志
     *
     * @param log    {@link VehicleManageLog}
     * @param type   {@link Integer} 日志类型
     * @param result {@link String} 操作结果
     */
    private void updateLog(VehicleManageLog log, Integer type, String result) {
        if (result.length() > 0) {
            type++;
            log.setType(type);
            log.setResult(result);
        }
        OperateLogUtil.updateVehicleManageLog(log, vehicleManageLogService, logger);
    }

    /**
     * 向父页面发送子页面的WebSocketSession ID
     *
     * @param msgMap    {@link WebSocketMessage} JSON文本的实际对象
     * @param sessionId {@link WebSocketSession#getId()}
     * @param biz       {@link String} 业务类型
     */
    private void sendChildSessionToParent(Map<String, Object> msgMap, Long sessionId, String biz) {
        // 父页面sessionId
        Long parentSessionId = ((Integer) msgMap.get("parentSession")).longValue();
        WebSocketSession parentSession = WEB_SOCKET_CLIENTS.get(parentSessionId);
        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put("biz", biz);
        sessionMap.put("sessionId", sessionId);
        StringBuffer strBuf = new StringBuffer();
        strBuf.append('{');
        strBuf.append("\"biz\":\"").append(biz).append("\",\"sessionId\":").append(sessionId);
        strBuf.append('}');
        WebSocketUtil.sendTextMsg(parentSession, strBuf.toString());
    }

    /**
     * 处理车辆轨迹上报业务
     *
     * @param msgMap {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealCarTrackReport(Map<String, Object> msgMap) {
        JSONObject jsonObj = (JSONObject) msgMap.get("track");
        Map<String, Object> trackMap = JSONUtil.parseJSONObject(jsonObj);
        // 车辆ID
        Long id = ((Integer) msgMap.get("vehicle_id")).longValue();
        // 车牌号
        String carNumber = (String) trackMap.get("vehicle_number");
        // 运输公司
        String carCom = VehicleCompanyRelationCache.getComOfCar(id);
        // 经度
        float longitude = (float) trackMap.get("longitude");
        // 纬度
        float latitude = (float) trackMap.get("latitude");
        // 速度
        int velocity = (int) trackMap.get("speed");
        // 角度
        int angle = (int) trackMap.get("angle");
        // 车辆状态
        String status = getCarStatus((int) trackMap.get("vehicle_status"));
        // 报警情况
        boolean alarm = isAlarm((int) trackMap.get("vehicle_alarm_status"), (String) trackMap.get("lock_status_info"));
        StringBuffer strBuf = new StringBuffer();
        strBuf.append('{');
        strBuf.append("\"biz\":\"track\",");
        strBuf.append("\"id\":").append(id.toString()).append(',');
        strBuf.append("\"carNumber\":\"").append(carNumber).append('\"').append(',');
        strBuf.append("\"carCom\":\"").append(carCom).append('\"').append(',');
        strBuf.append("\"longitude\":").append(longitude).append(',');
        strBuf.append("\"latitude\":").append(latitude).append(',');
        strBuf.append("\"velocity\":").append(velocity).append(',');
        strBuf.append("\"angle\":").append(angle).append(',');
        strBuf.append("\"status\":\"").append(status).append('\"').append(',');
        strBuf.append("\"alarm\":").append(alarm).append(',');
        strBuf.append("\"online\":1"); // online：0.离线；1.在线
        strBuf.append('}');
        broadcastCarTrackAndOffline(id, strBuf.toString());
    }

    /**
     * 处理车辆离线业务
     *
     * @param msgMap {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealCarOffLine(Map<String, Object> msgMap) {
        Long id = ((Integer) msgMap.get("vehicle_id")).longValue();
        StringBuffer strBuf = new StringBuffer();
        strBuf.append('{');
        strBuf.append("\"biz\":\"track\",");
        strBuf.append("\"id\":").append(id.toString()).append(',');
        strBuf.append("\"online\":0"); // online：0.离线；1.在线
        strBuf.append('}');
        broadcastCarTrackAndOffline(id, strBuf.toString());
    }

    /**
     * 处理轨迹列表上报业务
     *
     * @param msgMap {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealCarTrackList(Map<String, Object> msgMap) {
        JSONObject jsonObj = (JSONObject) msgMap.get("tracklist");
        Map<String, Object> map = JSONUtil.parseJSONObject(jsonObj);
        // 车辆ID
        Long id = ((Integer) msgMap.get("vehicle_id")).longValue();
        // 车牌号
        String carNumber = (String) map.get("vehicle_number");
        // 运输公司
        String carCom = VehicleCompanyRelationCache.getComOfCar(id);
        // 轨迹列表JSONArray
        JSONArray jsonArray = (JSONArray) map.get("tracklist");
        // 轨迹列表
        List<Map<String, Object>> list = JSONUtil.parseJSONArray(jsonArray);
        for (int i = 0, len = list.size(); i < len; i++) {
            Map<String, Object> trackMap = list.get(i);
            // 经度
            float longitude = (float) trackMap.get("longitude");
            // 纬度
            float latitude = (float) trackMap.get("latitude");
            // 速度
            int velocity = (int) trackMap.get("speed");
            // 角度
            int angle = (int) trackMap.get("angle");
            // 车辆状态
            String status = getCarStatus((int) trackMap.get("vehicle_status"));
            // 报警情况
            boolean alarm = isAlarm((int) trackMap.get("vehicle_alarm_status"),
                    (String) trackMap.get("lock_status_info"));
            StringBuffer strBuf = new StringBuffer();
            strBuf.append('{');
            strBuf.append("\"biz\":\"track\",");
            strBuf.append("\"id\":").append(id.toString()).append(',');
            strBuf.append("\"carNumber\":\"").append(carNumber).append('\"').append(',');
            strBuf.append("\"carCom\":\"").append(carCom).append('\"').append(',');
            strBuf.append("\"longitude\":").append(longitude).append(',');
            strBuf.append("\"latitude\":").append(latitude).append(',');
            strBuf.append("\"velocity\":").append(velocity).append(',');
            strBuf.append("\"angle\":").append(angle).append(',');
            strBuf.append("\"status\":\"").append(status).append('\"').append(',');
            strBuf.append("\"alarm\":").append(alarm).append(',');
            strBuf.append("\"online\":1"); // online：0.离线；1.在线
            strBuf.append('}');
            String track = strBuf.toString();
            sendToFocusPage(id, track);
            // 轨迹缓存只保留最后一条轨迹
            if (i == len - 1) {
                CAR_TRACKS.put(id, track);
                sendToGeneralPage(track);
            }
        }

    }

    /**
     * 广播车辆轨迹和离线信息
     *
     * @param carId     {@link Long} 车辆ID
     * @param trackJson {@link String} 轨迹或离线信息
     */
    private void broadcastCarTrackAndOffline(Long carId, String trackJson) {
        CAR_TRACKS.put(carId, trackJson);
        sendToGeneralPage(trackJson);
        sendToFocusPage(carId, trackJson);
        sendToRealtimePage(carId, trackJson);
    }

    /**
     * 处理上报轨迹转发到普通监控页面
     *
     * @param track 车辆轨迹
     */
    private void sendToGeneralPage(String track) {
        // 普通监控页面全发
        GENERAL_CLIENTS.keySet().parallelStream().forEach(sessionId -> {
            WebSocketSession session = WEB_SOCKET_CLIENTS.get(sessionId);
            if (session.isOpen()) {
                WebSocketUtil.sendTextMsg(session, track);
            } else {
                GENERAL_CLIENTS.remove(sessionId);
            }
        });
    }

    /**
     * 处理上报轨迹转发到实时监控页面
     *
     * @param carId 车辆ID
     * @param track 车辆轨迹
     */
    private void sendToRealtimePage(Long carId, String track) {
        // 实时监控页面只发关注的车辆
        REALTIME_CARS.keySet().parallelStream().forEach(sessionId -> {
            if (REALTIME_CARS.get(sessionId).contains(carId)) {
                Long current = System.currentTimeMillis();
                Map<String, Long> timeInfo = REALTIME_CLIENTS.get(sessionId);
                Long begin = timeInfo.get("begin");
                Long duration = timeInfo.get("duration");
                boolean timeout = current - begin > duration;// 监控超时
                dealTimeOut(timeout, sessionId, track, REALTIME_CLIENTS, REALTIME_CARS);
            }
        });
    }

    /**
     * 处理上报轨迹转发到重点监控页面
     *
     * @param carId 车辆ID
     * @param track 车辆轨迹
     */
    private void sendToFocusPage(Long carId, String track) {
        // 重点监控页面只发关注的车辆
        FOCUS_CARS.keySet().parallelStream().forEach(sessionId -> {
            if (FOCUS_CARS.get(sessionId).equals(carId)) {
                Long current = System.currentTimeMillis();
                Long timestamp = FOCUS_CLIENTS.get(sessionId);
                boolean timeout = current - timestamp > 3600000L;// 监控超时
                dealTimeOut(timeout, sessionId, track, FOCUS_CLIENTS, FOCUS_CARS);
            }
        });
    }

    /**
     * 处理监控超时
     *
     * @param timeout     是否超时
     * @param sessionId   {@link WebSocketSession#getId()}
     * @param track       轨迹信息
     * @param clientCache 监控WebSocket客户端缓存
     * @param carCahe     监控车辆缓存
     */
    private void dealTimeOut(boolean timeout, Long sessionId, String track, Map<Long, ?> clientCache,
                             Map<Long, ?> carCahe) {
        WebSocketSession session = WEB_SOCKET_CLIENTS.get(sessionId);
        if (session.isOpen()) {
            if (timeout) {
                WebSocketUtil.sendTextMsg(session, "timeout");
                return;
            }
            WebSocketUtil.sendTextMsg(session, track);
        } else {
            clientCache.remove(sessionId);
            carCahe.remove(sessionId);
        }
    }

    /**
     * 车辆状态
     *
     * @param status 状态值（0：未知 | 1：在油库 | 2：在途中 | 3：在加油站 | 4：返程中 | 5：应急）
     * @return 车辆状态名称
     */
    private String getCarStatus(int status) {
        switch (status) {
            case 0:
                return "未知";
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
                break;
        }
        return "未知";
    }

    /**
     * 是否报警
     *
     * @param carAlarmStatus    车辆报警状态
     * @param lockStatusInfoHex 锁状态16进制字符串
     * @return
     */
    private boolean isAlarm(int carAlarmStatus, String lockStatusInfoHex) {
        if ((carAlarmStatus & AlarmBitMarkConst.VALID_TERMINAL_ALARM_BITS) > 0) {
            return true;
        }
        byte[] lockStatusInfo = BytesUtil.hexStringToBytes(lockStatusInfoHex);
        return VehicleAlarmUtil.isLockAlarm(lockStatusInfo);
    }

    /**
     * 添加UDP业务缓存
     *
     * @param bizId       {@link Short} 业务ID
     * @param logId       {@link Long} 日志记录ID
     * @param description {@link String} 任务描述
     * @return cacheId {@link Integer} 缓存ID
     */
    private int addUdpCache(short bizId, long logId, String description) {
        short serialNo = (short) (SerialNumberCache.getSerialNumber(bizId) + 1);
        int cacheId = AsynUdpCommCache.buildCacheId(bizId, serialNo);
        AsynUdpCommCache.putLogCache(cacheId, logId);
        AsynUdpCommCache.putTaskCache(cacheId, description);
        return cacheId;
    }

    /**
     * 添加重点监控重发业务缓存
     *
     * @return cacheId {@link Integer} 缓存ID
     */
    private int addRepeatCache() {
        short serialNo = (short) (SerialNumberCache.getSerialNumber(UdpBizId.FOCUS_MONITOR_REQUEST) + 1);
        int cacheId = AsynUdpCommCache.buildCacheId(UdpBizId.FOCUS_MONITOR_REQUEST, serialNo);
        AsynUdpCommCache.putLogCache(cacheId, 0L);
        REPEAT_CACHE.add(cacheId);
        return cacheId;
    }

    /**
     * 移除UDP业务缓存
     *
     * @param cacheId     {@link Integer} 缓存ID
     * @param description {@link String} 任务描述
     */
    private void removeUdpCache(int cacheId, String description) {
        AsynUdpCommCache.removeLogCache(cacheId);
        if (description != null) {
            AsynUdpCommCache.removeTaskCache(cacheId);
            cacheId = 0;
            return;
        }
        REPEAT_CACHE.remove(cacheId);
    }

    /**
     * 添加UDP超时任务
     *
     * @param src      {@link ByteBuffer} 待发送数据包
     * @param cacheId  {@link Integer} 缓存ID
     * @param isRepeat {@link Boolean} 是否重点监控重发
     */
    private void addUdpTimeoutTask(ByteBuffer src, int cacheId, boolean isRepeat) {
        TimeOutTask task = new TimeOutTask(src, cacheId, 15L);
        if (isRepeat) {
            task.executeFocusRepeatTask();
        } else {
            task.executeRemoteControlTask();
        }
    }

    /**
     * 处理UDP轨迹上报
     *
     * @param carId     {@link Long} 车辆ID
     * @param trackList {@link String} 轨迹列表
     */
    public void dealTrackUploadByUdp(long carId, List<String> trackList) {
        if (EmptyObjectUtil.isEmptyList(trackList)) {
            logger.error("UDP上报轨迹列表信息为空！");
            return;
        }
        int size = trackList.size();
        if (size == 1) {
            String track = trackList.get(0);
            CAR_TRACKS.put(carId, track);
            sendToGeneralPage(track);
            sendToFocusPage(carId, track);
            sendToRealtimePage(carId, track);
            return;
        }
        for (int i = 0; i < size; i++) {
            String track = trackList.get(i);
            sendToFocusPage(carId, track);
            // 轨迹缓存只保留最后一条轨迹
            if (i == size - 1) {
                CAR_TRACKS.put(carId, track);
                sendToGeneralPage(track);
            }
        }
    }

    /**
     * 广播远程操作请求
     *
     * @param isFail {@link Integer} 是否失败（1 失败，0 成功）
     * @param task    {@link String} 请求任务描述
     * @param result  {@link String} 请求发起结果
     */
    public void broadcastLog(Integer isFail, String task, String result) {
        StringBuffer msgBuf = new StringBuffer();
        msgBuf.append('{');
        msgBuf.append("\"biz\":\"log\",");
        msgBuf.append("\"fail\":").append(isFail).append(',');
        msgBuf.append("\"task\":\"").append(task).append('\"').append(',');
        msgBuf.append("\"result\":\"").append(result).append('\"');
        msgBuf.append('}');
        sendToGeneralPage(msgBuf.toString());
    }

    /**
     * 处理UDP应答结果
     *
     * @param replyBizId {@link Short} UDP应答业务ID
     * @param cacheId    {@link Integer} UDP缓存ID
     * @param task       {@link String} UDP任务描述
     * @param resultFlag {@link Boolean} UDP结果标识
     * @param result     {@link String} UDP任务结果
     */
    public void dealUdpReply(short replyBizId, int cacheId, String task, boolean resultFlag, String result) {
        broadcastLog(resultFlag == true ? 0 : 1, task, result);

        switch (replyBizId) {
            case UdpBizId.REALTIME_MONITOR_RESPONSE:
                if (!resultFlag) {
                    Long sessionId = UDP_CACHE.get(cacheId);
                    if (sessionId != null) {
                        UDP_CACHE.remove(cacheId);
                        WebSocketSession session = WEB_SOCKET_CLIENTS.get(sessionId);
                        List<Long> carIds = REALTIME_CARS.get(sessionId);
                        Long carId = REALTIME_CARS_CACHE.get(cacheId);
                        REALTIME_CARS_CACHE.remove(cacheId);
                        carIds.remove(carId);
                        if (carIds.isEmpty()) {
                            REALTIME_CARS.remove(sessionId);
                        } else {
                            REALTIME_CARS.put(sessionId, carIds);
                        }
                        StringBuffer errorBuf = new StringBuffer();
                        errorBuf.append('{');
                        errorBuf.append("\"biz\":\"error\",");
                        errorBuf.append("\"carNos\":\"").append(vehicleService.getCarNumberById(carId)).append('\"');
                        errorBuf.append('}');
                        WebSocketUtil.sendTextMsg(session, errorBuf.toString());
                    }
                }
                break;
            case UdpBizId.FOCUS_MONITOR_RESPONSE:
                Long sessionId = UDP_CACHE.get(cacheId);
                if (sessionId != null) {
                    UDP_CACHE.remove(cacheId);
                    WebSocketSession session = WEB_SOCKET_CLIENTS.get(sessionId);
                    boolean isRepeat = REPEAT_CACHE.contains(cacheId);
                    if (isRepeat) {
                        REPEAT_CACHE.remove(cacheId);
                        if (!resultFlag) {
                            FOCUS_CLIENTS.remove(sessionId);
                            FOCUS_CARS.remove(sessionId);
                            WebSocketUtil.sendTextMsg(session, "broken");
                        }
                    } else {
                        if (resultFlag) {
                            WebSocketUtil.sendTextMsg(session, "success");
                        } else {
                            FOCUS_CLIENTS.remove(sessionId);
                            FOCUS_CARS.remove(sessionId);
                            WebSocketUtil.sendTextMsg(session, "error");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * UDP通信异常报警
     */
    public void dealUdpCommAlarm() {
        logger.warn("UDP通信异常！");
        broadcastLog(1, "", "UDP通信异常！");
    }

    /**
     * 消除UDP通信异常报警
     */
    public void removeUdpCommAlarm() {
        logger.info("UDP通信畅通！");
        broadcastLog(0, "", "UDP通信畅通！");
    }

}