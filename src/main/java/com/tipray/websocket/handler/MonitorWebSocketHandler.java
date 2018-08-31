package com.tipray.websocket.handler;

import com.tipray.bean.VehicleRealtimeStatus;
import com.tipray.bean.VehicleTerminalConfig;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.baseinfo.User;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.bean.track.LastTrack;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.LogTypeConst;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.UserService;
import com.tipray.service.VehicleManageLogService;
import com.tipray.service.VehicleService;
import com.tipray.util.*;
import com.tipray.websocket.WebSocketCloseStatusEnum;
import com.tipray.websocket.WebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import javax.annotation.Resource;
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
    private static final Map<Long, ConcurrentWebSocketSessionDecorator> WEB_SOCKET_CLIENTS = new ConcurrentHashMap<>();
    /**
     * 普通监控客户端缓存Map&lt;WebSocketSession-Id, User-comId&gt;
     */
    private static final Map<Long, Long> GENERAL_CLIENTS = new ConcurrentHashMap<>();
    /**
     * 重点监控客户端缓存Map&lt;WebSocketSession-Id, 重点监控发起时间&gt;
     */
    private static final Map<Long, Long> FOCUS_CLIENTS = new ConcurrentHashMap<>();
    /**
     * 重点监控车辆缓存Map&lt;WebSocketSession-Id, 车辆ID&gt;
     */
    private static final Map<Long, Long> FOCUS_CARS = new ConcurrentHashMap<>();
    /**
     * 实时监控客户端缓存Map&lt;WebSocketSession-Id, 实时监控发起时间和监控时长&gt;
     */
    private static final Map<Long, Map<String, Long>> REALTIME_CLIENTS = new ConcurrentHashMap<>();
    /**
     * 实时监控车辆缓存Map&lt;WebSocketSession-Id, 车辆ID列表&gt;
     */
    private static final Map<Long, List<Long>> REALTIME_CARS = new ConcurrentHashMap<>();
    /**
     * 轨迹缓存
     */
    private static final TrackCache TRACK_CACHE = TrackCache.INSTANCE;
    /**
     * 在线车辆缓存
     */
    private static final Map<Long, String> ONLINE_CARS = new ConcurrentHashMap<>();
    /**
     * UDP缓存Map&lt;UDP缓存ID, WebSocketSession-Id&gt;
     */
    private static final Map<Integer, Long> UDP_CACHE = new ConcurrentHashMap<>();
    /**
     * 实时监控车辆缓存Map&lt;UDP缓存ID, 车辆Id&gt;
     */
    private static final Map<Integer, Long> REALTIME_CARS_CACHE = new ConcurrentHashMap<>();
    /**
     * 重点监控重发缓存Set&lt;UDP缓存ID&gt;
     */
    private static final Set<Integer> REPEAT_CACHE = new HashSet<>();
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
    private UserService userService;
    @Resource
    private VehicleService vehicleService;
    @Resource
    private VehicleManageLogService vehicleManageLogService;
    @Resource
    private NioUdpServer udpServer;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long sessionId = Long.parseLong(session.getId(), 16);
        ConcurrentWebSocketSessionDecorator sessionDecorator = WebSocketUtil.decoratorSession(session);
        WEB_SOCKET_CLIENTS.put(sessionId, sessionDecorator);
        logger.info("monitor connection {} established.", sessionId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (message.getPayload() instanceof ByteBuffer) {
            // IE浏览器不间断发送0字节的PongMessage以维持WebSocket连接，服务端忽略此消息
            return;
        }
        logger.debug("handleMessage：" + message.getPayload());
        String msgText = null;
        try {
            msgText = URLDecoder.decode((String) message.getPayload(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("接收WebSocket信息解码异常：{}", e.getMessage());
            return;
        }
        Map<String, Object> msgMap;
        try {
            msgMap = JSONUtil.parseToMap(msgText);
        } catch (Exception e) {
            logger.error("JSON解析异常：{}", e.getMessage());
            return;
        }
        Long sessionId = Long.parseLong(session.getId(), 16);
        ConcurrentWebSocketSessionDecorator sessionDecorator = WEB_SOCKET_CLIENTS.get(sessionId);
        String biz = (String) msgMap.get("biz");
        switch (biz) {
            case GENERAL_MONITOR:
                dealGeneralMonitor(sessionId, sessionDecorator, msgMap);
                break;
            case FOCUS_MONITOR:
                dealFocusMonitor(sessionId, sessionDecorator, msgMap);
                break;
            case REALTIME_MONITOR:
                dealRealtimeMonitor(sessionId, sessionDecorator, msgMap);
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
     * 移除monitor缓存
     *
     * @param sessionId {@link WebSocketSession#getId()}
     */
    private void removeMonitorCache(Long sessionId) {
        WEB_SOCKET_CLIENTS.remove(sessionId);
        GENERAL_CLIENTS.remove(sessionId);
        // FOCUS_CLIENTS.remove(sessionId);
        // FOCUS_CARS.remove(sessionId);
        // REALTIME_CLIENTS.remove(sessionId);
        // REALTIME_CARS.remove(sessionId);
        // UDP_CACHE.keySet().forEach(cacheId -> {
        //     if (UDP_CACHE.get(cacheId).equals(sessionId)) {
        //         UDP_CACHE.remove(cacheId);
        //         REALTIME_CARS_CACHE.remove(cacheId);
        //     }
        // });
    }

    /**
     * 处理普通监控
     *
     * @param sessionId {@link Long} WebSocketSession ID
     * @param session   {@link ConcurrentWebSocketSessionDecorator}
     * @param msgMap    {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealGeneralMonitor(Long sessionId,
                                    ConcurrentWebSocketSessionDecorator session,
                                    Map<String, Object> msgMap) {
        String userIdStr = (String) msgMap.get("user");
        if (StringUtil.isEmpty(userIdStr)) {
            logger.warn("普通监控：获取用户信息失败！");
            WebSocketUtil.sendConcurrentMsg(session, BIZ_REPEAT);
            return;
        }
        Long userId;
        try {
            userId = Long.parseLong((String) msgMap.get("user"), 10);
        } catch (Exception e) {
            logger.warn("普通监控：收到的用户ID【{}】不是整数！", userIdStr);
            WebSocketUtil.sendConcurrentMsg(session, BIZ_REPEAT);
            return;
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            logger.warn("ID为【{}】的用户不存在！", userIdStr);
            WebSocketUtil.sendConcurrentMsg(session, BIZ_REPEAT);
        } else {
            Long comId = user.getComId();
            GENERAL_CLIENTS.put(sessionId, comId);
            try {
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("biz", "session");
                sessionMap.put("sessionId", sessionId);
                sessionMap.put("userId", user.getId());
                sessionMap.put("userAccount", user.getAccount());
                sessionMap.put("userName", user.getName());
                session.sendMessage(new TextMessage(JSONUtil.stringify(sessionMap)));
                if (!ONLINE_CARS.isEmpty()) {
                    Map<String, Object> onlineCarsCahe = new HashMap<>();
                    onlineCarsCahe.put("biz", "onlineCache");
                    onlineCarsCahe.put("cars", ONLINE_CARS.keySet());
                    session.sendMessage(new TextMessage(JSONUtil.stringify(onlineCarsCahe)));
                    String trackMsg = TRACK_CACHE.cacheTracks(comId);
                    if (StringUtil.isNotEmpty(trackMsg)) {
                        WebSocketUtil.sendConcurrentMsg(session, trackMsg);
                    }
                }
            } catch (Exception e) {
                logger.error("普通监控：WebSocket通信异常！", e);
            }
        }
    }

    /**
     * 处理重点监控指令
     *
     * @param sessionId        {@link Long} WebSocketSession ID
     * @param sessionDecorator {@link ConcurrentWebSocketSessionDecorator}
     * @param msgMap           {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealFocusMonitor(Long sessionId,
                                  ConcurrentWebSocketSessionDecorator sessionDecorator,
                                  Map<String, Object> msgMap) {
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
                    WebSocketUtil.sendConcurrentMsg(sessionDecorator, "parse_error");
                    return;
                }
                // 车辆ID
                Long carId = ((Integer) msgMap.get("carId")).longValue();
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
                    WebSocketUtil.sendConcurrentMsg(sessionDecorator, "db_error");
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
                        WebSocketUtil.sendConcurrentMsg(sessionDecorator, "conn_error");
                        return;
                    }
                    addUdpTimeoutTask(src, cacheId, false);
                    vehicleManageLog.setUdpBizId(UdpBizId.FOCUS_MONITOR_REQUEST);
                    UDP_CACHE.put(cacheId, sessionId);
                    // 当前时间戳（重点监控发起时间）
                    Long timestamp = System.currentTimeMillis();
                    FOCUS_CLIENTS.put(sessionId, timestamp);
                    FOCUS_CARS.put(sessionId, carId);
                    WebSocketUtil.sendConcurrentMsg(sessionDecorator, TRACK_CACHE.cacheTrack(carId));
                } catch (Exception e) {
                    removeUdpCache(cacheId, description);
                    result = "失败，重点监控异常！";
                    logger.error("重点监控异常！", e);
                    WebSocketUtil.sendConcurrentMsg(sessionDecorator, "conn_error");
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
                    // FOCUS_CLIENTS.remove(sessionId);
                    // FOCUS_CARS.remove(sessionId);
                    WebSocketUtil.sendConcurrentMsg(sessionDecorator, "conn_broken");
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
        FOCUS_CLIENTS.remove(sessionId);
        FOCUS_CARS.remove(sessionId);
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
                    vehicleManageLog.setUdpBizId(UdpBizId.FOCUS_MONITOR_CANCEL_REQUEST);
                } catch (Exception e) {
                    removeUdpCache(cacheId, description);
                    result = "失败，重点监控取消异常！";
                    logger.error("重点监控取消异常！", e);
                } finally {
                    updateLog(vehicleManageLog, type, result);
                }
            }
        }
    }

    /**
     * 处理实时监控指令
     *
     * @param sessionId        {@link Long} WebSocketSession ID
     * @param sessionDecorator {@link ConcurrentWebSocketSessionDecorator}
     * @param msgMap           {@link WebSocketMessage} JSON文本的实际对象
     */
    private void dealRealtimeMonitor(Long sessionId,
                                     ConcurrentWebSocketSessionDecorator sessionDecorator,
                                     Map<String, Object> msgMap) {
        String bizType = (String) msgMap.get("bizType");
        // 操作员
        User user;
        try {
            user = JSONUtil.parseToObject((String) msgMap.get("user"), User.class);
        } catch (Exception e) {
            logger.error("JSON解析User对象异常：{}", e.toString());
            WebSocketUtil.sendConcurrentMsg(sessionDecorator, "parse_error");
            return;
        }
        // token
        String token = (String) msgMap.get("token");
        switch (bizType) {
            case BIZ_REQUEST:
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
                cars.forEach(car -> {
                    VehicleManageLog vehicleManageLog = new VehicleManageLog(user, 0);
                    Integer type = LogTypeConst.CLASS_VEHICLE_MANAGE | LogTypeConst.ENTITY_TRACK
                            | LogTypeConst.TYPE_REALTIME_MONITOR | LogTypeConst.RESULT_DONE;
                    String description = new StringBuffer("车辆轨迹实时监控：").append(user.getName()).append("通过网页实时监控")
                            .append("车辆" + car.getCarNumber()).append("的轨迹。").toString();
                    Long logId = OperateLogUtil.addVehicleManageLog(vehicleManageLog, type, description, token, vehicleManageLogService, logger);
                    if (logId == null || logId == 0L) {
                        WebSocketUtil.sendConcurrentMsg(sessionDecorator, "db_error");
                        return;
                    }
                    String result = "";
                    int cacheId = 0;
                    try {
                        cacheId = addUdpCache(UdpBizId.REALTIME_MONITOR_REQUEST, logId, description);
                        // 车载终端设备ID
                        Integer terminalId = car.getVehicleDevice().getDeviceId();
                        ByteBuffer src = SendPacketBuilder.buildProtocol0x1301(terminalId, sessionId, interval, duration);
                        boolean isSend = udpServer.send(src);
                        if (!isSend) {
                            removeUdpCache(cacheId, description);
                            result = "失败，指令发送异常！";
                            carNoBuf.append(car.getCarNumber()).append('、');
                            logger.error("UDP发送数据异常！");
                            return;
                        }
                        addUdpTimeoutTask(src, cacheId, false);
                        vehicleManageLog.setUdpBizId(UdpBizId.REALTIME_MONITOR_REQUEST);
                        UDP_CACHE.put(cacheId, sessionId);
                        succuessAtLeastOne = true;
                        carIds.add(car.getId());
                        REALTIME_CARS_CACHE.put(cacheId, car.getId());
                        WebSocketUtil.sendConcurrentMsg(sessionDecorator, TRACK_CACHE.cacheTrack(car.getId()));
                    } catch (Exception e) {
                        removeUdpCache(cacheId, description);
                        result = "失败，实时监控异常！";
                        carNoBuf.append(car.getCarNumber()).append('、');
                        logger.error("实时监控异常！", e);
                    } finally {
                        updateLog(vehicleManageLog, type, result);
                    }

                });
                if (!succuessAtLeastOne) {
                    WebSocketUtil.sendConcurrentMsg(sessionDecorator, "conn_error");
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
                        WebSocketUtil.sendConcurrentMsg(sessionDecorator, error);
                    }
                }
                break;
            case BIZ_CANCEL:
                Long cancelSessionId = ((Integer) msgMap.get("session")).longValue();
                List<Long> ids = REALTIME_CARS.get(cancelSessionId);
                REALTIME_CARS.remove(cancelSessionId);
                if (!EmptyObjectUtil.isEmptyList(ids)) {
                    ids.forEach(carId -> dealRealtimeCancel(user, carId, cancelSessionId, token));
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
                    vehicleManageLog.setUdpBizId(UdpBizId.REALTIME_MONITOR_CANCEL_REQUEST);
                } catch (Exception e) {
                    removeUdpCache(cacheId, description);
                    result = "失败，实时监控取消异常！";
                    logger.error("实时监控取消异常！", e);
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
     * @param sessionId {@link Long} WebSocketSession ID
     * @param biz       {@link String} 业务类型
     */
    private void sendChildSessionToParent(Map<String, Object> msgMap, Long sessionId, String biz) {
        // 父页面sessionId
        Long parentSessionId = ((Integer) msgMap.get("parentSession")).longValue();
        ConcurrentWebSocketSessionDecorator parentSession = WEB_SOCKET_CLIENTS.get(parentSessionId);
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("biz", biz);
        sessionMap.put("sessionId", sessionId);
        StringBuffer strBuf = new StringBuffer();
        strBuf.append('{');
        strBuf.append("\"biz\":\"").append(biz).append("\",\"sessionId\":").append(sessionId);
        strBuf.append('}');
        WebSocketUtil.sendConcurrentMsg(parentSession, strBuf.toString());
    }


    /**
     * 广播车辆轨迹和离线信息
     *
     * @param carId {@link Long} 车辆ID
     * @param info  {@link String} 车辆轨迹或在线信息
     */
    private void broadcastCarTrackAndOnline(Long carId, String info) {
        sendToGeneralPage(info);
        sendToFocusPage(carId, info);
        sendToRealtimePage(carId, info);
    }

    /**
     * 处理上报信息转发到普通监控页面
     *
     * @param msg 上报信息
     */
    private void sendToGeneralPage(String msg) {
        // 普通监控页面全发
        GENERAL_CLIENTS.keySet().forEach(sessionId -> {
            ConcurrentWebSocketSessionDecorator sessionDecorator = WEB_SOCKET_CLIENTS.get(sessionId);
            if (sessionDecorator == null || !sessionDecorator.isOpen()) {
                GENERAL_CLIENTS.remove(sessionId);
                return;
            }
            WebSocketUtil.sendConcurrentMsg(sessionDecorator, msg);
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
        REALTIME_CARS.keySet().forEach(sessionId -> {
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
        FOCUS_CARS.keySet().forEach(sessionId -> {
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
    private void dealTimeOut(boolean timeout,
                             Long sessionId,
                             String track,
                             Map<Long, ?> clientCache,
                             Map<Long, ?> carCahe) {
        ConcurrentWebSocketSessionDecorator sessionDecorator = WEB_SOCKET_CLIENTS.get(sessionId);
        if (sessionDecorator == null || !sessionDecorator.isOpen()) {
            clientCache.remove(sessionId);
            carCahe.remove(sessionId);
            return;
        }
        if (timeout) {
            WebSocketUtil.sendConcurrentMsg(sessionDecorator, "timeout");
            return;
        }
        WebSocketUtil.sendConcurrentMsg(sessionDecorator, track);
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
     * 检测车辆是否在线
     *
     * @param carId {@link Long} 车辆ID
     * @return {@link Boolean} 车辆是否在线
     */
    public boolean checkCarOnline(long carId) {
        return ONLINE_CARS.keySet().contains(carId);
    }

    /**
     * 车辆在线状态定时监控
     *
     * @param onlineCars {@link Long} 在线车辆ID集合
     */
    public void monitorVehicleOnline(Map<Long, String> onlineCars) {
        synchronized (ONLINE_CARS) {
            Set<Long> offlineCars = new HashSet<>(ONLINE_CARS.keySet());
            offlineCars.removeAll(onlineCars.keySet());
            Set<Long> newOnlineCars = onlineCars.keySet();
            newOnlineCars.removeAll(ONLINE_CARS.keySet());
            ONLINE_CARS.putAll(onlineCars);
            if (!EmptyObjectUtil.isEmptySet(offlineCars)) {
                for (Long carId : offlineCars) {
                    broadcastOnline(carId, ONLINE_CARS.get(carId), 0);
                    ONLINE_CARS.remove(carId);
                }
            }
            if (!EmptyObjectUtil.isEmptySet(newOnlineCars)) {
                newOnlineCars.forEach(carId -> broadcastOnline(carId, onlineCars.get(carId), 1));
            }
        }
    }

    /**
     * 处理http车辆在线状态上报
     *
     * @param vehicleId {@link Long} 车辆ID
     * @param online    {@link Integer} 在线状态（0 离线，1 在线）
     */
    public void dealOnlineUpload(Long vehicleId, Integer online) {
        if (vehicleId == null || online == null) {
            logger.warn("在线信息有空值：online={}, vehicleId={}", online, vehicleId);
            return;
        }
        if (online < 0 || online > 1) {
            logger.warn("在线状态越界：online={}", online);
            return;
        }
        String carNumber = SpringBeanUtil.getBean(VehicleService.class).getCarNumberById(vehicleId);
        if (online == 0) {
            ONLINE_CARS.remove(vehicleId);
        } else if (online == 1) {
            ONLINE_CARS.put(vehicleId, carNumber);
        }
        broadcastOnline(vehicleId, carNumber, online);
    }

    private void broadcastOnline(Long carId, String carNumber, Integer online) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append('{');
        strBuf.append("\"biz\":\"online\",");
        strBuf.append("\"carId\":").append(carId.toString()).append(',');
        strBuf.append("\"carNo\":\"").append(carNumber).append('\"').append(',');
        strBuf.append("\"online\":").append(online.toString());
        strBuf.append('}');
        broadcastCarTrackAndOnline(carId, strBuf.toString());
    }

    /**
     * 处理http车辆配置上报
     *
     * @param vehicleId {@link Long} 车辆ID
     */
    public void dealVehicleCfgUpload(Long vehicleId) {
        if (vehicleId == null) {
            return;
        }
        VehicleRealtimeStatus realtimeStatus = vehicleService.getVehicleRealtimeStatus(vehicleId);
        if (realtimeStatus == null) {
            return;
        }
        String carNumber = realtimeStatus.getCarNumber();
        if (StringUtil.isEmpty(carNumber)) {
            return;
        }
        Integer paramStatus = realtimeStatus.getParamStatus();
        if (paramStatus == null) {
            return;
        }
        StringBuffer strBuf = new StringBuffer();
        // 绑定状态信息，状态可同时存在（用位标识各个状态，从低位开始）
        // 0：还未有绑定状态信息
        // 1：全部绑定
        // 2：车辆信息未绑定完整
        // 3：车辆绑定信息未同步
        // 4：配送卡未指定
        // 5：配送卡未同步
        // 6：中心公共参数为空
        // 7：公共参数未同步
        // 8：中心锁绑定信息异常
        // 9：锁绑定信息未同步
        // 10：车辆轨迹上报参数为空
        // 11：车辆轨迹上报参数未同步
        if (paramStatus == 0) {
            strBuf.append(carNumber);
            strBuf.append("，还未有绑定状态信息！");
            broadcastLog(1, "", strBuf.toString());
            return;
        }
        if (((paramStatus >> 1) & 1) > 0) {
            strBuf.append("，车辆信息未绑定完整！");
        }
        if (((paramStatus >> 2) & 1) > 0) {
            strBuf.append("，车辆绑定信息未同步！");
        }
        if (((paramStatus >> 3) & 1) > 0) {
            strBuf.append("，配送卡未指定！");
        }
        if (((paramStatus >> 4) & 1) > 0) {
            strBuf.append("，配送卡未同步！");
        }
        if (((paramStatus >> 5) & 1) > 0) {
            strBuf.append("，中心公共参数为空！");
        }
        if (((paramStatus >> 6) & 1) > 0) {
            strBuf.append("，公共参数未同步！");
        }
        if (((paramStatus >> 7) & 1) > 0) {
            strBuf.append("，中心锁绑定信息异常！");
        }
        if (((paramStatus >> 8) & 1) > 0) {
            strBuf.append("，锁绑定信息未同步！");
        }
        if (((paramStatus >> 9) & 1) > 0) {
            strBuf.append("，车辆轨迹上报参数为空！");
        }
        if (((paramStatus >> 10) & 1) > 0) {
            strBuf.append("，车辆轨迹上报参数未同步！");
        }
        if (((paramStatus >> 11) & 1) > 0) {
            strBuf.append("，车台功能启用配置未同步！");
        }
        if (strBuf.length() > 0) {
            strBuf.insert(0, carNumber);
            broadcastLog(1, "", strBuf.toString());
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
            // CAR_TRACKS.put(carId, track);
            // broadcastCarTrackAndOnline(carId, track);
            // UPDATE_TRACKS.put(carId, track);
            sendToFocusPage(carId, track);
            sendToRealtimePage(carId, track);
            return;
        }
        long duration = 5000L;
        VehicleTerminalConfig conf = vehicleService.getGpsConfByCarId(carId);
        if (conf != null) {
            duration = conf.getScanInterval().longValue() * 1000L;
        }
        for (int i = 0; i < size; i++) {
            String track = trackList.get(i);
            sendToFocusPage(carId, track);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                // no interrupt expect
            }
            // // 轨迹缓存只保留最后一条轨迹
            // if (i == size - 1) {
            //     CAR_TRACKS.put(carId, track);
            //     sendToGeneralPage(track);
            // }
        }
    }

    /**
     * 最新车辆轨迹推送
     */
    public void pushLastTracks(List<LastTrack> lastTracks) {
        TRACK_CACHE.flushCache(lastTracks);
        GENERAL_CLIENTS.entrySet().parallelStream().forEach(entry -> {
            Long sessionId = entry.getKey();
            ConcurrentWebSocketSessionDecorator sessionDecorator = WEB_SOCKET_CLIENTS.get(sessionId);
            if (sessionDecorator == null || !sessionDecorator.isOpen()) {
                GENERAL_CLIENTS.remove(sessionId);
                return;
            }
            String trackMsg = TRACK_CACHE.updateTracks(entry.getValue());
            if (StringUtil.isNotEmpty(trackMsg)) {
                WebSocketUtil.sendConcurrentMsg(sessionDecorator, trackMsg);
            }
        });
    }

    /**
     * 广播远程操作日志
     *
     * @param isFail {@link Integer} 是否失败（1 失败，0 成功）
     * @param task   {@link String} 请求任务描述
     * @param result {@link String} 请求发起结果
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
        if (task != null) {
            broadcastLog(resultFlag ? 0 : 1, task, result);
        }

        switch (replyBizId) {
            case UdpBizId.REALTIME_MONITOR_RESPONSE:
                if (!resultFlag) {
                    Long sessionId = UDP_CACHE.remove(cacheId);
                    if (sessionId != null) {
                        ConcurrentWebSocketSessionDecorator sessionDecorator = WEB_SOCKET_CLIENTS.get(sessionId);
                        // List<Long> carIds = REALTIME_CARS.get(sessionId);
                        Long carId = REALTIME_CARS_CACHE.get(cacheId);
                        REALTIME_CARS_CACHE.remove(cacheId);
                        // carIds.remove(carId);
                        // if (carIds.isEmpty()) {
                        //     REALTIME_CARS.remove(sessionId);
                        // } else {
                        //     REALTIME_CARS.put(sessionId, carIds);
                        // }
                        String carNumber = SpringBeanUtil.getBean(VehicleService.class).getCarNumberById(carId);
                        StringBuffer errorBuf = new StringBuffer();
                        errorBuf.append('{');
                        errorBuf.append("\"biz\":\"error\",");
                        errorBuf.append("\"carNos\":\"").append(carNumber).append('\"');
                        errorBuf.append('}');
                        WebSocketUtil.sendConcurrentMsg(sessionDecorator, errorBuf.toString());
                    }
                }
                break;
            case UdpBizId.FOCUS_MONITOR_RESPONSE:
                Long sessionId = UDP_CACHE.remove(cacheId);
                if (sessionId != null) {
                    ConcurrentWebSocketSessionDecorator sessionDecorator = WEB_SOCKET_CLIENTS.get(sessionId);
                    boolean isRepeat = REPEAT_CACHE.contains(cacheId);
                    if (isRepeat) {
                        REPEAT_CACHE.remove(cacheId);
                        if (!resultFlag) {
                            // FOCUS_CLIENTS.remove(sessionId);
                            // FOCUS_CARS.remove(sessionId);
                            WebSocketUtil.sendConcurrentMsg(sessionDecorator, "conn_broken");
                        }
                    } else {
                        if (resultFlag) {
                            WebSocketUtil.sendConcurrentMsg(sessionDecorator, "success");
                        } else {
                            // FOCUS_CLIENTS.remove(sessionId);
                            // FOCUS_CARS.remove(sessionId);
                            WebSocketUtil.sendConcurrentMsg(sessionDecorator, "conn_error");
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