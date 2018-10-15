package com.tipray.controller;

import com.tipray.bean.ResponseMsg;
import com.tipray.constant.reply.ErrorTagConst;
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
import java.util.Date;
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
        ResponseMsg responseMsg = checkRecordParams(carNumber, beginTime);
        if (responseMsg.getCode() > 0) {
            return responseMsg;
        }
        try {
            Long carId = vehicleService.getIdByCarNo(carNumber);
            if (carId == null) {
                String logMsg = "车辆不存在！";
                logger.warn(logMsg);
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 5, logMsg);
            }
            return ResponseMsgUtil.success(mobileAppService.findSealRecords(carId, carNumber, beginTime));
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
        ResponseMsg responseMsg = checkRecordParams(carNumber, beginTime);
        if (responseMsg.getCode() > 0) {
            return responseMsg;
        }
        try {
            Long carId = vehicleService.getIdByCarNo(carNumber);
            if (carId == null) {
                String logMsg = "车辆不存在！";
                logger.warn(logMsg);
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 5, logMsg);
            }
            return ResponseMsgUtil.success(mobileAppService.findLockRecords(carId, carNumber, beginTime));
        } catch (Exception e) {
            logger.error("按车辆查询最新n条开关锁纪录异常！", e);
            return ResponseMsgUtil.exception(e);
        }
    }

    /**
     * 校验记录参数
     *
     * @param carNumber 车牌号
     * @param beginTime 开始时间
     * @return {@link ResponseMsg}
     */
    private ResponseMsg checkRecordParams(String carNumber, String beginTime) {
        if (StringUtil.isEmpty(carNumber)) {
            String logMsg = "车牌号为空！";
            logger.warn(logMsg);
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 1, logMsg);
        }
        if (StringUtil.isEmpty(beginTime)) {
            String logMsg = "开始时间为空！";
            logger.warn(logMsg);
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 2, logMsg);
        }
        try {
            Date beginDate = new SimpleDateFormat(DateUtil.FORMAT_DATETIME).parse(beginTime);
            if (beginDate.getTime() >= System.currentTimeMillis()) {
                String logMsg = "开始时间比当前时间晚！";
                logger.warn(logMsg);
                return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 3, logMsg);
            }
        } catch (ParseException e) {
            String logMsg = "时间格式不正确！";
            logger.warn(logMsg);
            return ResponseMsgUtil.error(ErrorTagConst.PARAM_CHECK_ERROR_TAG, 4, logMsg);
        }
        return ResponseMsgUtil.success();
    }
}
