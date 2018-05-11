package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.Record;
import com.tipray.bean.record.*;
import com.tipray.constant.StatisticsMode;
import com.tipray.core.base.BaseAction;
import com.tipray.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 查询统计控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/statistics")
/* @Scope("prototype") */
public class StatisticsController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);
    @Resource
    private AlarmRecordService alarmRecordService;
    @Resource
    private RemoteRecordService remoteRecordService;
    @Resource
    private LockRecordService lockRecordService;
    @Resource
    private InOutRecordService inOutRecordService;
    @Resource
    private DistributionRecordService distributionRecordService;
    @Resource
    private ChangeRecordService changeRecordService;
    @Resource
    private CardAndDeviceUseRecordService cardAndDeviceUseRecordService;

    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        logger.info("dispatch record bmap page, mode={}, id={}", mode, id);
        modelMap.put("mode", mode);
        Record record = null;
        switch (mode) {
            case StatisticsMode.ALARM_RECORD:
                record = alarmRecordService.getRecordById(id);
                break;
            case StatisticsMode.REMOTE_CONTROL_RECORD:
                record = remoteRecordService.getRecordById(id);
                break;
            case StatisticsMode.LOCK_ACTION_RECORD:
                record = lockRecordService.getRecordById(id);
                break;
            case StatisticsMode.CAR_IN_OUT_RECORD:
                record = inOutRecordService.getRecordById(id);
                break;
            case StatisticsMode.DISTRIBUTION_RECORD:
                record = distributionRecordService.getRecordById(id);
                break;
            case StatisticsMode.REMOTE_CHANGE_STATION_RECORD:
                record = changeRecordService.getRecordById(id);
                break;
            case StatisticsMode.DEVICE_USE_RECORD:
                record = cardAndDeviceUseRecordService.getRecordById(id);
                break;
            default:
                break;
        }
        modelMap.put("record", record);
        return "normal/statistics/viewBmap.jsp";
    }

    @RequestMapping(value = "findAlarmRecordsForPage.do")
    @ResponseBody
    public GridPage<AlarmRecord> findAlarmRecordsForPage(@ModelAttribute AlarmRecord alarmRecord,
                                                         @ModelAttribute Page page) {
        logger.info("alarm record list page, alarmRecord={}, page={}", alarmRecord, page);
        GridPage<AlarmRecord> gridPage = alarmRecordService.findRecordsForPage(alarmRecord, page);
        return gridPage;
    }

    @RequestMapping(value = "findRemoteRecordsForPage.do")
    @ResponseBody
    public GridPage<RemoteRecord> findRemoteRecordsForPage(@ModelAttribute RemoteRecord remoteRecord,
                                                           @ModelAttribute Page page) {
        logger.info("remote record list page, remoteRecord={}, page={}", remoteRecord, page);
        GridPage<RemoteRecord> gridPage = remoteRecordService.findRecordsForPage(remoteRecord, page);
        return gridPage;
    }

    @RequestMapping(value = "findLockRecordsForPage.do")
    @ResponseBody
    public GridPage<LockRecord> findLockRecordsForPage(@ModelAttribute LockRecord lockRecord,
                                                       @ModelAttribute Page page) {
        logger.info("lock record list page, lockRecord={}, page={}", lockRecord, page);
        GridPage<LockRecord> gridPage = lockRecordService.findRecordsForPage(lockRecord, page);
        return gridPage;
    }

    @RequestMapping(value = "findInOutRecordsForPage.do")
    @ResponseBody
    public GridPage<InOutRecord> findInOutRecordsForPage(@ModelAttribute InOutRecord inOutRecord,
                                                         @ModelAttribute Page page) {
        logger.info("inout record list page, inOutRecord={}, page={}", inOutRecord, page);
        GridPage<InOutRecord> gridPage = inOutRecordService.findRecordsForPage(inOutRecord, page);
        return gridPage;
    }

    @RequestMapping(value = "findDistributionRecordsForPage.do")
    @ResponseBody
    public GridPage<DistributionRecord> findDistributionRecordsForPage(
            @ModelAttribute DistributionRecord distributionRecord, @ModelAttribute Page page) {
        logger.info("distribution record list page, distributionRecord={}, page={}", distributionRecord, page);
        GridPage<DistributionRecord> gridPage = distributionRecordService.findRecordsForPage(distributionRecord, page);
        return gridPage;
    }

    @RequestMapping(value = "findChangeRecordsForPage.do")
    @ResponseBody
    public GridPage<ChangeRecord> findChangeRecordsForPage(@ModelAttribute ChangeRecord changeRecord,
                                                           @ModelAttribute Page page) {
        logger.info("change record list page, changeRecord={}, page={}", changeRecord, page);
        GridPage<ChangeRecord> gridPage = changeRecordService.findRecordsForPage(changeRecord, page);
        return gridPage;
    }

    @RequestMapping(value = "findDeviceRecordsForPage.do")
    @ResponseBody
    public GridPage<CardAndDeviceUseRecord> findDeviceRecordsForPage(@ModelAttribute CardAndDeviceUseRecord useRecord,
                                                                     @ModelAttribute Page page) {
        logger.info("use record list page, useRecord={}, page={}", useRecord, page);
        GridPage<CardAndDeviceUseRecord> gridPage = cardAndDeviceUseRecordService.findRecordsForPage(useRecord, page);
        return gridPage;
    }

    /**
     * 报警信息查询
     *
     * @return
     */
    @RequestMapping(value = "findAlarms.do")
    @ResponseBody
    public List<Map<String, Object>> findAlarms() {
        return alarmRecordService.findNotElimitedForApp();
    }

    /**
     * 加油站未完成配送信息查询
     *
     * @param gasStationId 加油站ID
     * @return
     */
    @RequestMapping(value = "findDistributionsByGasStationId.do")
    @ResponseBody
    public List<Map<String, Object>> findDistributionsByGasStationId(Long gasStationId) {
        logger.info("find distributions by station id, gasStationId={}", gasStationId);
        return distributionRecordService.findDistributionsByGasStationId(gasStationId);
    }

    /**
     * 车辆未完成配送信息查询（仓号为空时查询车辆全部未完成配送单）
     *
     * @param carNumber 车牌号
     * @param storeId   仓号
     * @return
     */
    @RequestMapping(value = "findDistributionsByVehicle.do")
    @ResponseBody
    public List<Map<String, Object>> findDistributionsByVehicle(String carNumber, Integer storeId) {
        logger.info("find distributions by vehicle, carNumber={}, storeId={}", carNumber, storeId);
        return distributionRecordService.findDistributionsByVehicle(carNumber, storeId);
    }

}
