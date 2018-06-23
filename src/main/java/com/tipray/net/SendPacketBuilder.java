package com.tipray.net;

import com.tipray.bean.upgrade.TerminalUpgradeFile;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.cache.SerialNumberCache;
import com.tipray.net.constant.UdpBizId;
import com.tipray.util.ArraysUtil;
import com.tipray.util.BytesConverterByLittleEndian;
import com.tipray.util.BytesUtil;
import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * UDP请求业务数据包构建
 *
 * @author chenlong
 * @version 1.0 2018-04-08
 */
public class SendPacketBuilder {
    /**
     * 链路维护心跳
     *
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1100() {
        return buildSendPacket(UdpProtocol.COMM_SERVER_ID, UdpBizId.LINK_HEARTBEAT_DETECTION, null);
    }

    /**
     * 公共配置更新
     *
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1201(ByteBuffer dataBuffer) {
        return buildSendPacket(UdpProtocol.COMM_SERVER_ID, UdpBizId.TERMINAL_COMMON_CONFIG_UPDATE_REQUEST, dataBuffer);
    }

    /**
     * 公共配置更新
     *
     * @param commonConfig {@link byte} 公共配置文件列表，用位标识下列更新文件：<br>
     *                     1 emergency_card_info.db<br>
     *                     2 management_card_info.db<br>
     *                     3 in_out_oildepot_card_info.db<br>
     *                     4 in_out_oildepot_dev_info.db<br>
     *                     5 oildepot_info.db
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1201(byte commonConfig) {
        ByteBuffer dataBuffer = buildDataBufForCommonConfigUpdate(commonConfig);
        return buildProtocol0x1201(dataBuffer);
    }

    /**
     * 车辆轨迹信息定时上传间隔参数更新
     *
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1202(ByteBuffer dataBuffer) {
        return buildSendPacket(UdpProtocol.COMM_SERVER_ID, UdpBizId.TERMINAL_TRACK_CONFIG_UPDATE_REQUEST, dataBuffer);
    }

    /**
     * 车辆轨迹信息定时上传间隔参数更新
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1202(int terminalId) {
        ByteBuffer dataBuffer = buildDataBufForTrackConfig(terminalId);
        return buildProtocol0x1202(dataBuffer);
    }

    /**
     * 配送卡信息更新下发，车载终端设备ID为空时不发
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1203(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.TRANSPORT_CARD_UPDATE_REQUEST, dataBuffer);
    }

    /**
     * 配送卡信息更新下发，车载终端设备ID为空时不发
     *
     * @param terminalId    {@link int} 车载终端设备ID
     * @param transportCard {@link long} 配送卡ID
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1203(int terminalId,
                                                 long transportCard) {
        ByteBuffer dataBuffer = buildDataBufForTranscardUpdate(transportCard);
        return buildProtocol0x1203(terminalId, dataBuffer);
    }

    /**
     * 车辆完成绑定下发
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1204(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.CAR_BIND_REQUEST, dataBuffer);
    }

    /**
     * 车辆完成绑定下发
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param carNumber  {@link String} 车牌号
     * @param storeNum   {@link byte} 仓数
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1204(int terminalId,
                                                 String carNumber,
                                                 byte storeNum) {
        ByteBuffer dataBuffer = buildDataBufForCarBind(carNumber, storeNum);
        return buildProtocol0x1204(terminalId, dataBuffer);
    }

    /**
     * 监听待绑定锁控制下发
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1205(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.LOCK_LISTEN_REQUEST, dataBuffer);
    }

    /**
     * 监听待绑定锁控制下发
     *
     * @param terminalId  {@link int} 车载终端设备ID
     * @param listenState {@link byte} 监听状态<br>
     *                    1 开始监听<br>
     *                    2 结束监听
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1205(int terminalId,
                                                 byte listenState) {
        ByteBuffer dataBuffer = buildDataBufForLockListen(listenState);
        return buildProtocol0x1205(terminalId, dataBuffer);
    }

    /**
     * 锁监听待绑定列表清除下发
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1206(int terminalId) {
        // 无数据体
        return buildSendPacket(terminalId, UdpBizId.LOCK_CLEAR_REQUEST, null);
    }

    /**
     * 锁绑定触发开启关闭控制下发
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x120A(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.LOCK_BIND_TRIGGER_REQUEST, dataBuffer);
    }

    /**
     * 锁绑定触发开启关闭控制下发
     *
     * @param terminalId   {@link int} 车载终端设备ID
     * @param triggerState {@link byte} 触发状态<br>
     *                     1 开始进行锁绑定触发<br>
     *                     2 结束锁绑定触发
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x120A(int terminalId,
                                                 byte triggerState) {
        ByteBuffer dataBuffer = buildDataBufForLockTrigger(triggerState);
        return buildProtocol0x120A(terminalId, dataBuffer);
    }

    /**
     * 锁绑定变更下发
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1207(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.LOCK_BIND_MODIFY_REQUEST, dataBuffer);
    }

    /**
     * 锁绑定变更下发
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param bindType   {@link byte} 变更类型（1 增加；2删除；3修改）
     * @param locks      {@link Lock} 绑定的锁列表
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1207(int terminalId,
                                                 byte bindType,
                                                 List<Lock> locks) {
        ByteBuffer dataBuffer = buildDataBufForLockBind(bindType, locks);
        return buildProtocol0x1207(terminalId, dataBuffer);
    }

    /**
     * 车台软件升级下发
     *
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1208(ByteBuffer dataBuffer) {
        return buildSendPacket(UdpProtocol.COMM_SERVER_ID, UdpBizId.TERMINAL_SOFTWARE_UPGRADE_REQUEST, dataBuffer);
    }

    /**
     * 车台软件升级下发
     *
     * @param terminalIds {@link String} 车台设备ID，英文逗号“,”分隔
     * @param upgradeInfo {@link byte[]} 车台软件升级信息流
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1208(String terminalIds, byte[] upgradeInfo) {
        ByteBuffer dataBuffer = buildDataBufForTerminalSoftwareUpgrade(terminalIds, upgradeInfo);
        return buildProtocol0x1208(dataBuffer);
    }

    /**
     * 车台软件升级下发
     *
     * @param terminalIds {@link String} 车台设备ID，英文逗号“,”分隔
     * @param upgradeType {@link byte} 升级类型
     * @param ftpPath     {@link String} ftp更新路径，UTF-8编码
     * @param files       {@link TerminalUpgradeFile} 升级文件列表
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1208(String terminalIds,
                                                 byte upgradeType,
                                                 String ftpPath,
                                                 List<TerminalUpgradeFile> files) {
        ByteBuffer dataBuffer = buildDataBufForTerminalSoftwareUpgrade(terminalIds, upgradeType, ftpPath, files);
        return buildProtocol0x1208(dataBuffer);
    }

    /**
     * 车台软件升级下发
     *
     * @param terminalIdList {@link Integer} 车台设备ID集合
     * @param upgradeInfo    {@link byte[]} 车台软件升级信息流
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1208(List<Integer> terminalIdList, byte[] upgradeInfo) {
        ByteBuffer dataBuffer = buildDataBufForTerminalSoftwareUpgrade(terminalIdList, upgradeInfo);
        return buildProtocol0x1208(dataBuffer);
    }

    /**
     * 车台软件升级下发
     *
     * @param terminalIdList {@link Integer} 车台设备ID集合
     * @param upgradeType    {@link byte} 升级类型
     * @param ftpPath        {@link String} ftp更新路径，UTF-8编码
     * @param files          {@link TerminalUpgradeFile} 升级文件列表
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1208(List<Integer> terminalIdList,
                                                 byte upgradeType,
                                                 String ftpPath,
                                                 List<TerminalUpgradeFile> files) {
        ByteBuffer dataBuffer = buildDataBufForTerminalSoftwareUpgrade(terminalIdList, upgradeType, ftpPath, files);
        return buildProtocol0x1208(dataBuffer);
    }

    /**
     * 车台功能启用配置下发
     *
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1209(ByteBuffer dataBuffer) {
        return buildSendPacket(UdpProtocol.COMM_SERVER_ID, UdpBizId.TERMINAL_FUNCTION_ENABLE_REQUEST, dataBuffer);
    }

    /**
     * 车台功能启用配置下发
     *
     * @param func {@link int} 启用功能
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1209(int func) {
        ByteBuffer dataBuffer = buildDataBufForTerminalFunctionEnable(func);
        return buildProtocol0x1209(dataBuffer);
    }

    /**
     * 车辆轨迹实时上报请求
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1301(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.REALTIME_MONITOR_REQUEST, dataBuffer);
    }

    /**
     * 车辆轨迹实时上报请求
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param sessionId  {@link WebSocketSession#getId()}
     * @param interval   {@link short} 监控间隔（秒）
     * @param duration   {@link short} 监控时长（分）
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1301(int terminalId,
                                                 long sessionId,
                                                 short interval,
                                                 short duration) {
        ByteBuffer dataBuffer = buildDataBufForRealtimeMonitor(sessionId, interval, duration);
        return buildProtocol0x1301(terminalId, dataBuffer);
    }

    /**
     * 车辆轨迹实时上报取消
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1302(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.REALTIME_MONITOR_CANCEL_REQUEST, dataBuffer);
    }

    /**
     * 车辆轨迹实时上报取消
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param sessionId  {@link WebSocketSession#getId()}
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1302(int terminalId,
                                                 long sessionId) {
        ByteBuffer dataBuffer = buildDataBufForMonitorCancel(sessionId);
        return buildProtocol0x1302(terminalId, dataBuffer);
    }

    /**
     * 车辆轨迹列表实时上报请求
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1303(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.FOCUS_MONITOR_REQUEST, dataBuffer);
    }

    /**
     * 车辆轨迹列表实时上报请求
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param sessionId  {@link WebSocketSession#getId()}
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1303(int terminalId,
                                                 long sessionId) {
        ByteBuffer dataBuffer = buildDataBufForFocusMonitor(sessionId);
        return buildProtocol0x1303(terminalId, dataBuffer);
    }

    /**
     * 车辆轨迹列表列表上报取消
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1304(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.FOCUS_MONITOR_CANCEL_REQUEST, dataBuffer);
    }

    /**
     * 车辆轨迹列表列表上报取消
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param sessionId  {@link WebSocketSession#getId()}
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1304(int terminalId,
                                                 long sessionId) {
        ByteBuffer dataBuffer = buildDataBufForMonitorCancel(sessionId);
        return buildProtocol0x1304(terminalId, dataBuffer);
    }

    /**
     * 远程换站请求
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 协议数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1401(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.REMOTE_CHANGE_STATION_REQUEST, dataBuffer);
    }

    /**
     * 远程报警消除
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1402(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.REMOTE_ALARM_ELIMINATE_REQUEST, dataBuffer);
    }

    /**
     * 远程报警消除
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param alarmEliId {@link int} 报警消除ID
     * @param deviceType {@link byte} 设备类型<br>
     *                   1 车台<br>
     *                   2 锁
     * @param deviceId   {@link int} 设备ID
     * @param alarmType  {@link byte} 报警类型（用位标识）<br>
     *                   车台：<br>
     *                   1 未施封越界<br>
     *                   2 时钟电池报警<br>
     *                   锁：<br>
     *                   1 通讯异常<br>
     *                   2 电池低电压报警<br>
     *                   3 异常开锁报警<br>
     *                   4 进入应急
     * @param userName   {@link String} 操作员姓名，UTF-8编码
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1402(int terminalId,
                                                 int alarmEliId,
                                                 byte deviceType,
                                                 int deviceId,
                                                 byte alarmType,
                                                 String userName) {
        ByteBuffer dataBuffer = buildDataBufForAlarmEliminate(alarmEliId, deviceType, deviceId, alarmType, userName);
        return buildProtocol0x1402(terminalId, dataBuffer);
    }

    /**
     * 远程车辆进出请求
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1403(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.REMOTE_CAR_IN_OUT_REQUEST, dataBuffer);
    }

    /**
     * 远程车辆进出请求
     *
     * @param terminalId  {@link int} 车载终端设备ID
     * @param remoteId    {@link int} 远程控制ID
     * @param readerId    {@link int} 可支持（道闸）转发的读卡器ID
     * @param controlType {@link byte} 操作类型<br>
     *                    0 未知<br>
     *                    1 入库<br>
     *                    2 出库<br>
     *                    3 入加油站<br>
     *                    4 出加油站<br>
     *                    5 进入应急<br>
     *                    6 取消应急<br>
     *                    8 待进油区<br>
     *                    9 进油区<br>
     *                    10 出油区<br>
     * @param stationId   {@link int} 站点ID（加油站ID、油库ID）
     * @param userName    {@link String} 操作员姓名，UTF-8编码
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1403(int terminalId,
                                                 int remoteId,
                                                 int readerId,
                                                 byte controlType,
                                                 int stationId,
                                                 String userName) {
        ByteBuffer dataBuffer = buildDataBufForCarInOut(remoteId, readerId, controlType, stationId, userName);
        return buildProtocol0x1403(terminalId, dataBuffer);
    }

    /**
     * 远程车辆状态强制变更
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1404(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.REMOTE_CAR_STATUS_ALTER_REQUEST, dataBuffer);
    }

    /**
     * 远程车辆状态强制变更
     *
     * @param terminalId      {@link int} 车载终端设备ID
     * @param remoteControlId {@link int} 远程控制ID
     * @param carStatus       {@link byte} 车辆状态<br>
     *                        1 在油库<br>
     *                        2 在途<br>
     *                        3 在加油站<br>
     *                        4 返程<br>
     *                        5 应急<br>
     *                        6 待进油区<br>
     *                        7 在油区<br>
     * @param stationType     {@link Integer} 站点类型（1：在油库 | 2：在加油站）
     * @param stationId       {@link int} 站点ID（加油站ID、油库ID）
     * @param userName        {@link String} 操作员姓名，UTF-8编码
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1404(int terminalId,
                                                 int remoteControlId,
                                                 byte carStatus,
                                                 byte stationType,
                                                 int stationId,
                                                 String userName) {
        ByteBuffer dataBuffer = buildDataBufForCarStatusAlter(remoteControlId, carStatus, stationType, stationId, userName);
        return buildProtocol0x1404(terminalId, dataBuffer);
    }

    /**
     * 远程开锁重置
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param dataBuffer {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1405(int terminalId,
                                                 ByteBuffer dataBuffer) {
        return buildSendPacket(terminalId, UdpBizId.LOCK_OPEN_RESET_REQUEST, dataBuffer);
    }

    /**
     * 远程开锁重置
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @param lockNum    {@link byte} 锁个数
     * @param list       {@link Integer} 锁设备ID和远程操作ID列表
     * @param userName   {@link String} 操作员姓名，UTF-8编码
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildProtocol0x1405(int terminalId,
                                                 byte lockNum,
                                                 List<Integer> list,
                                                 String userName) {
        ByteBuffer dataBuffer = buildDataBufForLockReset(lockNum, list, userName);
        return buildProtocol0x1405(terminalId, dataBuffer);
    }

    /**
     * 构建公共配置更新业务数据体
     *
     * @param commonConfig {@link byte} 公共配置文件列表，用位标识下列更新文件：<br>
     *                     1 emergency_card_info.db<br>
     *                     2 management_card_info.db<br>
     *                     3 in_out_oildepot_card_info.db<br>
     *                     4 in_out_oildepot_dev_info.db<br>
     *                     5 oildepot_info.db
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForCommonConfigUpdate(byte commonConfig) {
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(1);
        // 添加公共配置文件列表，1个字节
        dataBuffer.put(commonConfig);
        return dataBuffer;
    }

    /**
     * 构建车辆轨迹信息定时上传间隔参数更新业务数据体
     *
     * @param terminalId {@link int} 车载终端设备ID
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForTrackConfig(int terminalId) {
// 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(4);
        // 添加车载终端设备ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(terminalId));
        return dataBuffer;
    }

    /**
     * 构建配送卡信息更新下发业务数据体
     *
     * @param transportCard {@link long} 配送卡ID
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForTranscardUpdate(long transportCard) {
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(8);
        // 添加配送卡ID，8个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(transportCard));
        return dataBuffer;
    }

    /**
     * 构建车辆完成绑定下发业务数据体
     *
     * @param carNumber {@link String} 车牌号
     * @param storeNum  {@link byte} 仓数
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForCarBind(String carNumber,
                                                    byte storeNum) {
        byte[] carNoBuf = carNumber.getBytes(StandardCharsets.UTF_8);
        int carNoBufLen = carNoBuf.length;
        // 缓冲区容量
        int capacity = carNoBufLen + 2;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加车牌号长度，1个字节
        dataBuffer.put((byte) carNoBufLen);
        // 添加车牌号(UTF8字节流，包含中文)，最长20个有效字节，不包括 '\0'
        dataBuffer.put(carNoBuf);
        // 添加仓数，1个字节
        dataBuffer.put(storeNum);
        return dataBuffer;
    }

    /**
     * 构建监听待绑定锁控制下发业务数据体
     *
     * @param listenState {@link byte} 监听状态<br>
     *                    1 开始监听<br>
     *                    2 结束监听
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForLockListen(byte listenState) {
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(1);
        // 添加监听状态，1个字节
        dataBuffer.put(listenState);
        return dataBuffer;
    }

    /**
     * 构建锁绑定触发开启关闭控制下发业务数据体
     *
     * @param triggerState {@link byte} 触发状态<br>
     *                     1 开始进行锁绑定触发<br>
     *                     2 结束锁绑定触发
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForLockTrigger(byte triggerState) {
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(1);
        // 添加监听状态，1个字节
        dataBuffer.put(triggerState);
        return dataBuffer;
    }

    /**
     * 构建锁绑定变更下发业务数据体
     *
     * @param bindType {@link byte} 变更类型（1 增加；2删除；3修改）
     * @param locks    {@link Lock} 绑定的锁列表
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForLockBind(byte bindType,
                                                     List<Lock> locks) {
        // 锁数量
        int lockNum = locks.size();
        // 缓冲区容量（每把锁8个字节）
        int capacity = 2 + 8 * lockNum;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加变更类型，1个字节
        dataBuffer.put(bindType);
        // 添加锁个数，1个字节
        dataBuffer.put((byte) lockNum);
        for (Lock lock : locks) {
            // 添加单个锁信息，8个字节
            // 添加锁设备ID，4个字节
            dataBuffer.put(BytesConverterByLittleEndian.getBytes(lock.getLockId()));
            // 添加仓号，1个字节
            dataBuffer.put(lock.getStoreId().byteValue());
            // 添加仓位，1个字节
            dataBuffer.put(lock.getSeat().byteValue());
            // 添加相同仓位锁索引号，1个字节
            dataBuffer.put(lock.getSeatIndex().byteValue());
            // 添加是否允许开锁（1 不能，2 能），1个字节
            dataBuffer.put(lock.getAllowOpen().byteValue());
        }
        return dataBuffer;
    }

    /**
     * 构建车台软件升级下发业务数据体
     *
     * @param terminalIds {@link String} 车台设备ID，英文逗号“,”分隔
     * @param upgradeType {@link byte} 升级类型
     * @param ftpPath     {@link String} ftp更新路径，UTF-8编码
     * @param files       {@link TerminalUpgradeFile} 升级文件列表
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForTerminalSoftwareUpgrade(String terminalIds,
                                                                    byte upgradeType,
                                                                    String ftpPath,
                                                                    List<TerminalUpgradeFile> files) {
        byte[] upgradeInfo = buildTerminalSoftwareUpgradeInfo(upgradeType, ftpPath, files);
        return buildDataBufForTerminalSoftwareUpgrade(terminalIds, upgradeInfo);
    }

    /**
     * 构建车台软件升级下发业务数据体
     *
     * @param terminalIds {@link String} 车台设备ID，英文逗号“,”分隔
     * @param upgradeInfo {@link byte[]} 车台软件升级信息流
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForTerminalSoftwareUpgrade(String terminalIds, byte[] upgradeInfo) {
        String[] terminalIdStrs = terminalIds.split(",");
        int terminalNum = terminalIdStrs.length;
        // 缓冲区容量
        int capacity = 1 + 4 * terminalNum + upgradeInfo.length;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加设备数量，1个字节
        dataBuffer.put((byte) terminalNum);
        for (String terminalId : terminalIdStrs) {
            // 添加设备ID，4个字节
            dataBuffer.put(BytesConverterByLittleEndian.getBytes(Integer.parseInt(terminalId, 10)));
        }
        // 添加车台软件升级信息流
        dataBuffer.put(upgradeInfo);
        return dataBuffer;
    }

    /**
     * 构建车台软件升级下发业务数据体
     *
     * @param terminalIdList {@link Integer} 车台设备ID集合
     * @param upgradeType    {@link byte} 升级类型
     * @param ftpPath        {@link String} ftp更新路径，UTF-8编码
     * @param files          {@link TerminalUpgradeFile} 升级文件列表
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForTerminalSoftwareUpgrade(List<Integer> terminalIdList,
                                                                    byte upgradeType,
                                                                    String ftpPath,
                                                                    List<TerminalUpgradeFile> files) {
        byte[] upgradeInfo = buildTerminalSoftwareUpgradeInfo(upgradeType, ftpPath, files);
        return buildDataBufForTerminalSoftwareUpgrade(terminalIdList, upgradeInfo);
    }

    /**
     * 构建车台软件升级下发业务数据体
     *
     * @param terminalIdList {@link Integer} 车台设备ID集合
     * @param upgradeInfo    {@link byte[]} 车台软件升级信息流
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForTerminalSoftwareUpgrade(List<Integer> terminalIdList, byte[] upgradeInfo) {
        int terminalNum = terminalIdList.size();
        // 缓冲区容量
        int capacity = 1 + 4 * terminalNum + upgradeInfo.length;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加设备数量，1个字节
        dataBuffer.put((byte) terminalNum);
        for (Integer terminalId : terminalIdList) {
            // 添加设备ID，4个字节
            dataBuffer.put(BytesConverterByLittleEndian.getBytes(terminalId));
        }
        // 添加车台软件升级信息流
        dataBuffer.put(upgradeInfo);
        return dataBuffer;
    }

    /**
     * 构建车台软件升级信息流
     *
     * @param upgradeType {@link byte} 升级类型
     * @param ftpPath     {@link String} ftp更新路径，UTF-8编码
     * @param files       {@link TerminalUpgradeFile} 升级文件列表
     * @return {@link byte[]} 车台软件升级信息流
     */
    public static byte[] buildTerminalSoftwareUpgradeInfo(byte upgradeType,
                                                          String ftpPath,
                                                          List<TerminalUpgradeFile> files) {
        byte[] ftpPathBuf = ftpPath.getBytes(StandardCharsets.UTF_8);
        int ftpPathBufLen = ftpPathBuf.length;
        int fileNum = files.size();
        // 缓冲区容量
        int capacity = 3 + ftpPathBufLen;
        // 构建车台升级信息
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加升级类型，1个字节
        dataBuffer.put(upgradeType);
        // 添加ftp更新路径长度，1个字节
        dataBuffer.put((byte) ftpPathBufLen);
        // 添加ftp更新路径，UTF-8编码
        dataBuffer.put(ftpPathBuf);
        // 添加升级文件个数，1个字节
        dataBuffer.put((byte) fileNum);
        for (TerminalUpgradeFile updateFile : files) {
            byte[] fileNamebuf = updateFile.getName().getBytes(StandardCharsets.UTF_8);
            int fileNameLen = fileNamebuf.length;
            byte[] bytes = dataBuffer.array();
            capacity += fileNameLen + 10;
            dataBuffer = ByteBuffer.allocate(capacity);
            dataBuffer.put(bytes);
            // 添加文件类型，1个字节
            dataBuffer.put(updateFile.getType().byteValue());
            // 添加文件名长度，1个字节
            dataBuffer.put((byte) fileNameLen);
            // 添加文件名，UTF-8编码
            dataBuffer.put(fileNamebuf);
            // 添加文件大小，4个字节
            dataBuffer.put(BytesConverterByLittleEndian.getBytes(updateFile.getSize()));
            // 添加文件CRC32，4个字节
            dataBuffer.put(ArraysUtil.reverse(BytesUtil.hexStringToBytes(updateFile.getCrc32())));
        }
        return dataBuffer.array();
    }

    /**
     * 构建车台功能启用配置下发业务数据体
     *
     * @param func {@link int} 启用功能
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForTerminalFunctionEnable(int func) {
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(4);
        // 添加启用功能，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(func));
        return dataBuffer;
    }

    /**
     * 构建车辆轨迹实时上报请求业务数据体
     *
     * @param sessionId {@link WebSocketSession#getId()}
     * @param interval  {@link short} 监控间隔（秒）
     * @param duration  {@link short} 监控时长（分）
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForRealtimeMonitor(long sessionId,
                                                            short interval,
                                                            short duration) {
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(12);
        // 添加sessionId，8个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(sessionId));
        // 添加上发间隔，2个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(interval));
        // 添加持续时间，2个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(duration));
        return dataBuffer;
    }

    /**
     * 构建车辆轨迹列表实时上报请求业务数据体
     *
     * @param sessionId {@link WebSocketSession#getId()}
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForFocusMonitor(long sessionId) {
        // 持续时间（秒）
        short duration = 25;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(10);
        // 添加sessionId，8个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(sessionId));
        // 添加持续时间，2个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(duration));
        return dataBuffer;
    }

    /**
     * 构建车辆轨迹监控取消业务数据体
     *
     * @param sessionId {@link WebSocketSession#getId()}
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForMonitorCancel(long sessionId) {
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(8);
        // 添加sessionId，8个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(sessionId));
        return dataBuffer;
    }

    /**
     * 构建远程报警消除业务数据体
     *
     * @param alarmEliminateId {@link int} 报警消除ID
     * @param deviceType       {@link byte} 设备类型<br>
     *                         1 车台<br>
     *                         2 锁
     * @param deviceId         {@link int} 设备ID
     * @param alarmType        {@link byte} 报警类型（用位标识）<br>
     *                         车台：<br>
     *                         1 未施封越界<br>
     *                         2 时钟电池报警<br>
     *                         锁：<br>
     *                         1 通讯异常<br>
     *                         2 电池低电压报警<br>
     *                         3 异常开锁报警<br>
     *                         4 进入应急
     * @param userName         {@link String} 操作员姓名，UTF-8编码
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForAlarmEliminate(int alarmEliminateId,
                                                           byte deviceType,
                                                           int deviceId,
                                                           byte alarmType,
                                                           String userName) {
        byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
        int userNameBufLen = userNameBuf.length;
        // 缓冲区容量
        int capacity = 11 + userNameBufLen;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加远程操作ID，1个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(alarmEliminateId));
        // 添加设备类型，1个字节
        dataBuffer.put(deviceType);
        // 添加设备ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(deviceId));
        // 添加报警类型，1个字节
        dataBuffer.put(alarmType);
        // 添加姓名长度，1个字节
        dataBuffer.put((byte) userNameBufLen);
        // 添加操作员姓名，UTF-8编码
        dataBuffer.put(userNameBuf);
        return dataBuffer;
    }

    /**
     * 构建远程车辆进出请求业务数据体
     *
     * @param remoteId    {@link int} 远程控制ID
     * @param readerId    {@link int} 可支持（道闸）转发的读卡器ID
     * @param controlType {@link byte} 操作类型<br>
     *                    0 未知<br>
     *                    1 入库<br>
     *                    2 出库<br>
     *                    3 入加油站<br>
     *                    4 出加油站<br>
     *                    5 进入应急<br>
     *                    6 取消应急<br>
     *                    8 待进油区<br>
     *                    9 进油区<br>
     *                    10 出油区<br>
     * @param stationId   {@link int} 站点ID（加油站ID、油库ID）
     * @param userName    {@link String} 操作员姓名，UTF-8编码
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForCarInOut(int remoteId,
                                                     int readerId,
                                                     byte controlType,
                                                     int stationId,
                                                     String userName) {
        byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
        int userNameBufLen = userNameBuf.length;
        // 缓冲区容量
        int capacity = 14 + userNameBufLen;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加远程控制ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(remoteId));
        // 添加读卡器ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(readerId));
        // 添加操作类型，1个字节
        dataBuffer.put(controlType);
        // 添加站点ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(stationId));
        // 添加姓名长度，1个字节
        dataBuffer.put((byte) userNameBufLen);
        // 添加操作员姓名，UTF-8编码
        dataBuffer.put(userNameBuf);
        return dataBuffer;
    }

    /**
     * 构建远程车辆状态强制变更业务数据体
     *
     * @param remoteId    {@link int} 远程控制ID
     * @param carStatus   {@link byte} 车辆状态<br>
     *                    1 在油库<br>
     *                    2 在途<br>
     *                    3 在加油站<br>
     *                    4 返程<br>
     *                    5 应急<br>
     *                    6 待进油区<br>
     *                    7 在油区<br>
     * @param stationType {@link Integer} 站点类型（1：在油库 | 2：在加油站）
     * @param stationId   {@link int} 站点ID（加油站ID、油库ID）
     * @param userName    {@link String} 操作员姓名，UTF-8编码
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForCarStatusAlter(int remoteId,
                                                           byte carStatus,
                                                           byte stationType,
                                                           int stationId,
                                                           String userName) {
        byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
        int userNameBufLen = userNameBuf.length;
        // 缓冲区容量
        int capacity = 11 + userNameBufLen;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 远程控制ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(remoteId));
        // 添加车辆状态，1个字节
        dataBuffer.put(carStatus);
        // 添加站点类型，1个字节
        dataBuffer.put(stationType);
        // 添加站点ID，4个字节
        dataBuffer.put(BytesConverterByLittleEndian.getBytes(stationId));
        // 添加姓名长度，1个字节
        dataBuffer.put((byte) userNameBufLen);
        // 添加操作员姓名，UTF-8编码
        dataBuffer.put(userNameBuf);
        return dataBuffer;
    }

    /**
     * 构建远程开锁重置业务数据体
     *
     * @param lockNum  {@link byte} 锁个数
     * @param list     {@link Integer} 锁设备ID和远程操作ID列表
     * @param userName {@link String} 操作员姓名，UTF-8编码
     * @return {@link ByteBuffer} 数据体
     */
    public static ByteBuffer buildDataBufForLockReset(byte lockNum,
                                                      List<Integer> list,
                                                      String userName) {
        byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
        int userNameBufLen = userNameBuf.length;
        // 缓冲区容量
        int capacity = 2 + 8 * lockNum + userNameBufLen;
        // 构建协议数据体
        ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
        // 添加锁数量，1个字节
        dataBuffer.put(lockNum);
        for (int i = 0, len = lockNum * 2; i < len; i += 2) {
            // 添加远程操作ID，4个字节
            dataBuffer.put(BytesConverterByLittleEndian.getBytes(list.get(i)));
            // 添加锁设备ID，4个字节
            dataBuffer.put(BytesConverterByLittleEndian.getBytes(list.get(i + 1)));
        }
        // 添加姓名长度，1个字节
        dataBuffer.put((byte) userNameBufLen);
        // 添加操作员姓名，UTF-8编码
        dataBuffer.put(userNameBuf);
        return dataBuffer;
    }

    /**
     * 构建发送数据包
     *
     * @param remoteAddress {@link int} 目的地址(通信服务端地址/设备ID)
     * @param biz           {@link short} 业务ID
     * @param dataBuffer    {@link ByteBuffer} 数据体
     * @return {@link ByteBuffer} 待发送数据
     */
    public static ByteBuffer buildSendPacket(int remoteAddress,
                                             short biz,
                                             ByteBuffer dataBuffer) {
        // 序列号2个字节
        short serialNo = SerialNumberCache.getNextSerialNumber(biz);
        // 协议
        UdpProtocol udpProtocol = new UdpProtocol(remoteAddress, biz, serialNo, dataBuffer);
        return udpProtocol.dealProtocolOfRequestToByteBuffer();
    }

    private SendPacketBuilder() {
    }
}
