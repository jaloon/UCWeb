package com.tipray.controller;

import com.tipray.bean.ResponseMsg;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.core.exception.ArgsCheckException;
import com.tipray.service.MobileAppService;
import com.tipray.service.VehicleService;
import com.tipray.util.DateUtil;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端APP接口
 *
 * @author chenlong
 * @version 1.0 2018-10-11
 */
@RestController
@RequestMapping("app_api")
public class MobileAppInterface {
    private static final Logger logger = LoggerFactory.getLogger(MobileAppInterface.class);
    @Resource
    private MobileAppService mobileAppService;
    @Resource
    private VehicleService vehicleService;

    /**
     * 按轨迹id查询轨迹信息
     */
    @RequestMapping("get_track_by_id")
    public ResponseMsg getTrackById(String trackId) {
        if (logger.isDebugEnabled()) {
            logger.debug("app get track by id: trackId={}", trackId);
        }
        if (StringUtil.isEmpty(trackId)) {
            String logMsg = "轨迹ID为空！";
            logger.warn(logMsg);
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 1, logMsg);
        }
        try {
            Long.parseLong(trackId);
        } catch (NumberFormatException e) {
            String logMsg = "轨迹ID不是整数！";
            logger.warn(logMsg);
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 2, logMsg);
        }
        try {
            Map<String, Object> map = mobileAppService.getTrackById(trackId, null);
            if (map == null) {
                String logMsg = new StringBuilder("轨迹ID[").append(trackId).append("]对应的轨迹不存在！").toString();
                logger.warn(logMsg);
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 3, logMsg);
            }
            return ResponseMsgUtil.success(map);
        } catch (Exception e) {
            logger.error("按轨迹id查询轨迹信息异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 按车辆查询最新n条施解封记录
     */
    @RequestMapping("find_seal_records")
    public ResponseMsg findSealRecords(String carNumber, String beginTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("find seal records: carNumber={}, beginTime={}", carNumber, beginTime);
        }
        long carId;
        try {
            carId = checkCarNumber(carNumber);
            if (StringUtil.isNotEmpty(beginTime)) {
                compareTime(parseTime(beginTime), Long.MAX_VALUE);
            }
        } catch (ArgsCheckException e) {
            logger.warn(e.getMessage());
            return ResponseMsgUtil.error(e);
        }

        try {
            return ResponseMsgUtil.success(mobileAppService.findSealRecords(carId, carNumber, beginTime, now()));
        } catch (Exception e) {
            logger.error("按车辆查询最新n条施解封记录异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 按车辆查询最新n条开关锁纪录
     */
    @RequestMapping("find_lock_records")
    public ResponseMsg findLockRecords(String carNumber, String beginTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("find lock records: carNumber={}, beginTime={}", carNumber, beginTime);
        }
        long carId;
        try {
            carId = checkCarNumber(carNumber);
            checkTime(beginTime, null, false);
        } catch (ArgsCheckException e) {
            logger.warn(e.getMessage());
            return ResponseMsgUtil.error(e);
        }
        try {
            return ResponseMsgUtil.success(mobileAppService.findLockRecords(carId, carNumber, beginTime, now()));
        } catch (Exception e) {
            logger.error("按车辆查询最新n条开关锁纪录异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 按车辆查询n条施解封和开关锁记录，结果中返回两个数组，分别为施解封记录和开关锁记录
     */
    @RequestMapping("find_seal_lock_records")
    public ResponseMsg findSealAndLockRecords(String carNumber, String beginTime, String endTime) {
        long carId;
        try {
            carId = checkCarNumber(carNumber);
            checkTime(beginTime, endTime, true);
        } catch (ArgsCheckException e) {
            logger.warn(e.getMessage());
            return ResponseMsgUtil.error(e);
        }
        try {
            Map<String, Object> map = new HashMap<>(2);
            map.put("seal_records", mobileAppService.findSealRecords(carId, carNumber, beginTime, endTime));
            map.put("lock_records", mobileAppService.findLockRecords(carId, carNumber, beginTime, endTime));
            return ResponseMsgUtil.success(map);
        } catch (Exception e) {
            logger.error("按车辆查询最新n条施解封和开关锁记录异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 按车辆查询历史配送记录
     */
    @RequestMapping("find_transport_records")
    public ResponseMsg findDistRecords(String carNumber, String beginTime, String endTime) {
        long carId;
        try {
            carId = checkCarNumber(carNumber);
            checkTime(beginTime, endTime, true);
        } catch (ArgsCheckException e) {
            logger.warn(e.getMessage());
            return ResponseMsgUtil.error(e);
        }
        try {
            return ResponseMsgUtil.success(mobileAppService.findDistRecords(carId, carNumber, beginTime, endTime));
        } catch (Exception e) {
            logger.error("按车辆查询最新n条开关锁纪录异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 按车辆查询报警记录（包括已消除）
     */
    @RequestMapping("find_alarm_records")
    public ResponseMsg findAlarmRecords(String carNumber, String beginTime, String endTime) {
        long carId;
        try {
            carId = checkCarNumber(carNumber);
            checkTime(beginTime, endTime, true);
        } catch (ArgsCheckException e) {
            logger.warn(e.getMessage());
            return ResponseMsgUtil.error(e);
        }
        try {
            return ResponseMsgUtil.success(mobileAppService.findAlarmRecords(carId, carNumber, beginTime, endTime));
        } catch (Exception e) {
            logger.error("按车辆查询最新n条开关锁纪录异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 校验车牌号
     *
     * @param carNumber 车牌号
     * @return 车辆ID
     * @throws ArgsCheckException if carNumber is null or car Non-exist
     */
    private long checkCarNumber(String carNumber) {
        if (StringUtil.isEmpty(carNumber)) {
            throw new ArgsCheckException("车牌号为空！", 1);
        }
        Long carId = vehicleService.getIdByCarNo(carNumber);
        if (carId == null) {
            throw new ArgsCheckException("车辆不存在！", 2);
        }
        return carId;
    }

    /**
     * 校验时间
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param checkEnd  是否校验结束时间
     * @throws ArgsCheckException if time is null or time format invalid
     */
    private void checkTime(String beginTime, String endTime, boolean checkEnd) {
        if (StringUtil.isEmpty(beginTime)) {
            throw new ArgsCheckException("开始时间为空！", 3);
        }
        if (checkEnd && StringUtil.isEmpty(endTime)) {
            throw new ArgsCheckException("结束时间为空！", 4);
        }

        compareTime(parseTime(beginTime), checkEnd ? parseTime(endTime) : Long.MAX_VALUE);
    }

    /**
     * 转换时间字符串为毫秒值
     *
     * @param time 时间字符串
     * @return 毫秒值
     * @throws ArgsCheckException if time format invalid
     */
    private long parseTime(String time) {
        try {
            return new SimpleDateFormat(DateUtil.FORMAT_DATETIME).parse(time).getTime();
        } catch (ParseException e) {
            throw new ArgsCheckException("时间格式不正确！", 5);
        }
    }

    /**
     * 比较时间
     *
     * @param begin 开始时间毫秒数
     * @param end   结束时间毫秒数
     * @throws ArgsCheckException if begin >= end
     */
    private void compareTime(long begin, long end) {
        if (begin >= System.currentTimeMillis()) {
            throw new ArgsCheckException("开始时间比当前时间晚！", 6);
        }
        if (begin >= end) {
            throw new ArgsCheckException("开始时间比结束时间晚！", 7);
        }
    }

    /**
     * 获取当前时间字符串
     */
    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @RequestMapping("find_vehicles")
    public List<Map<String, Object>> findAllCarsForApp() {
        logger.info("find_vehicles start:");
        List<Map<String, Object>> list = vehicleService.findAllCarsForApp();
        logger.info("find_vehicles end.");
        return list;
    }
}
