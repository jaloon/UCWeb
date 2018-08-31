package com.tipray.core.job;

import com.tipray.bean.alarm.AlarmInfo;
import com.tipray.bean.baseinfo.AppSync;
import com.tipray.bean.track.LastTrack;
import com.tipray.cache.RC4KeyCache;
import com.tipray.constant.CenterConst;
import com.tipray.core.CenterVariableConfig;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.service.AlarmRecordService;
import com.tipray.service.AppService;
import com.tipray.service.SqliteSyncService;
import com.tipray.service.VehicleService;
import com.tipray.util.JSONUtil;
import com.tipray.util.OkHttpUtil;
import com.tipray.websocket.handler.AlarmWebSocketHandler;
import com.tipray.websocket.handler.MonitorWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 定时任务
 *
 * @author chenlong
 * @version 1.0 2018-04-18
 */
@Component
public class ScheduledJob {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledJob.class);
    @Resource
    private NioUdpServer udpServer;
    @Resource
    private VehicleService vehicleService;
    @Resource
    private AppService appService;
    @Resource
    private AlarmRecordService alarmRecordService;
    @Resource
    private SqliteSyncService sqliteSyncService;
    // @Resource
    // private TrackDao trackDao;
    private AlarmWebSocketHandler alarmWebSocketHandler = new AlarmWebSocketHandler();
    private MonitorWebSocketHandler monitorWebSocketHandler = new MonitorWebSocketHandler();
    /**
     * APP配置信息同步路径
     */
    private static final String APP_SYNC_URL = new StringBuffer().append(CenterConst.PLTONE_URL)
            .append("/api/appSync.do?id=").append(CenterConst.CENTER_ID).toString();

    /**
     * 定时更新RC4秘钥信息任务
     */
    public void executeUpdateRC4Key() {
        if (CenterVariableConfig.isRc4Net()) {
            RC4KeyCache.loadPltoneRc4();
        }
    }

    /**
     * UDP链路维护心跳任务
     */
    public void executeUdpHeartbeat() {
        try {
            udpServer.send(SendPacketBuilder.buildProtocol0x1100());
        } catch (Exception e) {
            logger.error("UDP心跳包发送异常：{}", e.toString());
        }
    }

    /**
     * APP设备和版本信息定时同步任务
     */
    public void executeAppSync() {
        try {
            String json = OkHttpUtil.get(APP_SYNC_URL);
            AppSync appsync = JSONUtil.parseToObject(json, AppSync.class);
            appService.sync(appsync);
        } catch (Exception e) {
            logger.error("APP配置信息同步异常！", e);
        }
    }

    /**
     * 最新报警信息更新
     */
    public void executeAlarmInfoUpdate() {
        List<AlarmInfo> alarmInfos = alarmRecordService.findNotElimitedAlarmInfo();
        alarmWebSocketHandler.pushAlarmIfos(alarmInfos);
    }

    /**
     * 车辆在线状态监测
     */
    public void executeVehicleOnlineMonitor() {
        Map<Long, String> onlineCars = vehicleService.monitorVehicleOnline();
        monitorWebSocketHandler.monitorVehicleOnline(onlineCars);
    }

    /**
     * 最新车辆轨迹查询
     */
    public void executeLastTtracksQuery() {
        // trackDao.updateTracks(UpdateTrack.random());
        List<LastTrack> lastTracks = vehicleService.findLastTracks();
        if (lastTracks != null) {
            monitorWebSocketHandler.pushLastTracks(lastTracks);
        }
    }

    /**
     * 车辆基础配置信息sqlite文件同步任务
     */
    public void executeSqliteSync() {
        sqliteSyncService.syncSqliteFile();
    }
}
