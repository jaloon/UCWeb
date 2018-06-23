package com.tipray.websocket;

import com.tipray.bean.track.LastTrack;
import com.tipray.constant.AlarmBitMarkConst;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 轨迹缓存
 *
 * @author chenlong
 * @version 1.0 2018-06-02
 */
public final class TrackCache {
    private Map<Long, Long> carTrackTime = new ConcurrentHashMap<>();
    private Map<Long, Long> carToCom = new ConcurrentHashMap<>();
    private Map<Long, String> tracks = new ConcurrentHashMap<>();
    private Map<Long, String> newTtracks = new ConcurrentHashMap<>();

    /**
     * 刷新缓存
     *
     * @param lastTracks 最新轨迹
     */
    public synchronized void flushCache(List<LastTrack> lastTracks) {
        if (lastTracks == null) {
            return;
        }
        int len = lastTracks.size();
        if (len == 0) {
            return;
        }
        StringBuffer strBuf;
        LastTrack lastTrack;
        String track;
        synchronized (newTtracks) {
            newTtracks.clear();
            for (int i = 0; i < len; i++) {
                lastTrack = lastTracks.get(i);
                Long carId = lastTrack.getCarId();
                Long comId = lastTrack.getComId();
                Long trackMillis = lastTrack.getTrackTime().getTime();
                boolean update = isTrackUpdate(carId, comId, trackMillis);
                if (update) {
                    strBuf = new StringBuffer();
                    strBuf.append('{');
                    strBuf.append("\"carId\":").append(carId).append(',');
                    strBuf.append("\"carNumber\":\"").append(lastTrack.getCarNumber()).append('\"').append(',');
                    strBuf.append("\"carCom\":\"").append(lastTrack.getCarCom()).append('\"').append(',');
                    strBuf.append("\"longitude\":").append(lastTrack.getLongitude()).append(',');
                    strBuf.append("\"latitude\":").append(lastTrack.getLatitude()).append(',');
                    strBuf.append("\"angle\":").append(lastTrack.getAngle()).append(',');
                    strBuf.append("\"velocity\":").append(lastTrack.getSpeed()).append(',');
                    strBuf.append("\"carStatus\":\"").append(parseCarStatus(lastTrack.getCarStatus())).append('\"').append(',');
                    strBuf.append("\"alarm\":\"").append(isAlarm(lastTrack.getTerminalAlarm(), lastTrack.getLockStatusInfo())).append('\"');
                    strBuf.append('}');

                    track = strBuf.toString();
                    tracks.put(carId, track);
                    newTtracks.put(carId, track);
                }
            }
        }
    }

    /**
     * 获取缓存轨迹
     *
     * @param carId 车辆ID
     * @return 缓存轨迹
     */
    public synchronized String cacheTrack(Long carId) {
        String track = tracks.get(carId);
        return track.replace("{", "{\"biz\":\"track\",");
    }

    /**
     * 获取缓存轨迹
     *
     * @param comId 车队ID
     * @return 缓存轨迹
     */
    public synchronized String cacheTracks(Long comId) {
        return queryTracks(comId, tracks);
    }

    /**
     * 获取更新轨迹
     *
     * @param comId 车队ID
     * @return 更新轨迹
     */
    public synchronized String updateTracks(Long comId) {
        return queryTracks(comId, newTtracks);
    }

    /**
     * 查询轨迹
     *
     * @param comId    车队ID
     * @param trackMap 轨迹Map
     * @return 轨迹信息
     */
    private synchronized String queryTracks(Long comId, Map<Long, String> trackMap) {
        StringBuffer strBuf = new StringBuffer();
        if (comId == null || comId == 0) {
            trackMap.values().forEach(track -> strBuf.append(',').append(track));
        } else {
            trackMap.entrySet().parallelStream().forEach(entry -> {
                if (comId.equals(carToCom.get(entry.getKey()))) {
                    strBuf.append(',').append(entry.getValue());
                }
            });
        }
        if (strBuf.length() > 0) {
            strBuf.replace(0, 1, "{\"biz\":\"tracks\",\"tracks\":[");
            strBuf.append("]}");
            return strBuf.toString();
        }
        return null;
    }

    /**
     * 轨迹是否更新
     *
     * @param carId       车辆ID
     * @param comId       车队ID
     * @param trackMillis 轨迹时间毫秒数
     * @return 是/否
     */
    private synchronized boolean isTrackUpdate(Long carId, Long comId, Long trackMillis) {
        Long oldTrackMillis = carTrackTime.get(carId);
        if (oldTrackMillis == null) {
            carToCom.put(carId, comId);
            carTrackTime.put(carId, trackMillis);
            return true;
        }
        if (oldTrackMillis.equals(trackMillis)) {
            return false;
        }
        carTrackTime.put(carId, trackMillis);
        return true;
    }

    /**
     * 解析车辆状态
     *
     * @param carStatus 车辆状态码（0：未知 | 1：在油库 | 2：在途中 | 3：在加油站 | 4：返程中 | 5：应急 | 6: 待入油区 | 7：在油区)
     * @return 车辆状态
     */
    private String parseCarStatus(int carStatus) {
        switch (carStatus) {
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
            case 6:
                return "待入油区";
            case 7:
                return "在油区";
            default:
                break;
        }
        return "未知";
    }

    /**
     * 是否报警
     *
     * @param terminalAlarm 车台报警状态
     * @param lockAlarms    锁状态信息流
     * @return 是/否
     */
    private char isAlarm(int terminalAlarm, byte[] lockAlarms) {
        if ((terminalAlarm & AlarmBitMarkConst.VALID_TERMINAL_ALARM_BITS) > 0) {
            return '是';
        }
        for (int i = 0, len = lockAlarms.length; i < len; i++) {
            byte lockAlarm = lockAlarms[i];
            if ((lockAlarm & AlarmBitMarkConst.VALID_LOCK_ALARM_BITS) > 0) {
                return '是';
            }
        }
        return '否';
    }
}
