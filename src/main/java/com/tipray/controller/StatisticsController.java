package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.Record;
import com.tipray.bean.record.*;
import com.tipray.constant.StatisticsMode;
import com.tipray.core.annotation.PermissionAnno;
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
    private SealRecordService sealRecordService;
    @Resource
    private DistributionRecordService distributionRecordService;
    @Resource
    private ChangeRecordService changeRecordService;
    @Resource
    private UsageRecordService usageRecordService;
    @Resource
    private UnlockResetRecordService unlockResetRecordService;
    @Resource
    private TerminalEventRecordService terminalEventRecordService;

    @PermissionAnno("statisticsManage")
    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("dispatch record bmap page, mode={}, id={}", mode, id);
        }
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
            case StatisticsMode.SEAL_RECORD:
                record = sealRecordService.getRecordById(id);
                break;
            case StatisticsMode.DISTRIBUTION_RECORD:
                record = distributionRecordService.getRecordById(id);
                break;
            case StatisticsMode.CHANGE_STATION_RECORD:
                record = changeRecordService.getRecordById(id);
                break;
            case StatisticsMode.USAGE_RECORD:
                record = usageRecordService.getRecordById(id);
                break;
            case StatisticsMode.RESET_RECORD:
                record = unlockResetRecordService.getRecordById(id);
                break;
            case StatisticsMode.EVENT_RECORD:
                record = terminalEventRecordService.getRecordById(id);
                break;
            default:
                break;
        }
        modelMap.put("record", record);
        return "normal/statistics/viewBmap.jsp";
    }

    @PermissionAnno("alarmRecordModule")
    @RequestMapping(value = "findAlarmRecordsForPage.do")
    @ResponseBody
    public GridPage<AlarmRecord> findAlarmRecordsForPage(@ModelAttribute AlarmRecord alarmRecord,
                                                         @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("alarm record list page, alarmRecord={}, page={}", alarmRecord, page);
        }
        return alarmRecordService.findRecordsForPage(alarmRecord, page);
    }

    @PermissionAnno("remoteRecordModule")
    @RequestMapping(value = "findRemoteRecordsForPage.do")
    @ResponseBody
    public GridPage<RemoteRecord> findRemoteRecordsForPage(@ModelAttribute RemoteRecord remoteRecord,
                                                           @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("remote record list page, remoteRecord={}, page={}", remoteRecord, page);
        }
        return remoteRecordService.findRecordsForPage(remoteRecord, page);
    }

    @PermissionAnno("lockStatusModule")
    @RequestMapping(value = "findLockRecordsForPage.do")
    @ResponseBody
    public GridPage<LockRecord> findLockRecordsForPage(@ModelAttribute LockRecord lockRecord,
                                                       @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("lock record list page, lockRecord={}, page={}", lockRecord, page);
        }
        return lockRecordService.findRecordsForPage(lockRecord, page);
    }

    @PermissionAnno("sealRecordModule")
    @RequestMapping(value = "findSealRecordsForPage.do")
    @ResponseBody
    public GridPage<SealRecord> findSealRecordsForPage(@ModelAttribute SealRecord sealRecord,
                                                        @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("inout record list page, sealRecord={}, page={}", sealRecord, page);
        }
        return sealRecordService.findRecordsForPage(sealRecord, page);
    }

    @PermissionAnno("transRecordModule")
    @RequestMapping(value = "findDistributionRecordsForPage.do")
    @ResponseBody
    public GridPage<DistributionRecord> findDistributionRecordsForPage(
            @ModelAttribute DistributionRecord distributionRecord, @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("distribution record list page, distributionRecord={}, page={}", distributionRecord, page);
        }
        return distributionRecordService.findRecordsForPage(distributionRecord, page);
    }

    @PermissionAnno("changeRecordModule")
    @RequestMapping(value = "findChangeRecordsForPage.do")
    @ResponseBody
    public GridPage<ChangeRecord> findChangeRecordsForPage(@ModelAttribute ChangeRecord changeRecord,
                                                           @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("change record list page, changeRecord={}, page={}", changeRecord, page);
        }
        return changeRecordService.findRecordsForPage(changeRecord, page);
    }

    @PermissionAnno("usageRecordModule")
    @RequestMapping(value = "findUsageRecordsForPage.do")
    @ResponseBody
    public GridPage<UsageRecord> findUsageRecordsForPage(@ModelAttribute UsageRecord usageRecord,
                                                         @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("usage record list page, usageRecord={}, page={}", usageRecord, page);
        }
        return usageRecordService.findRecordsForPage(usageRecord, page);
    }

    @PermissionAnno("resetRecordModule")
    @RequestMapping(value = "findResetRecordsForPage.do")
    @ResponseBody
    public GridPage<UnlockResetRecord> findResetRecordsForPage(@ModelAttribute UnlockResetRecord resetRecord,
                                                               @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("reset record list page, resetRecord={}, page={}", resetRecord, page);
        }
        return unlockResetRecordService.findRecordsForPage(resetRecord, page);
    }

    @PermissionAnno("eventRecordModule")
    @RequestMapping(value = "findEventRecordsForPage.do")
    @ResponseBody
    public GridPage<TerminalEventRecord> findEventRecordsForPage(@ModelAttribute TerminalEventRecord eventRecord,
                                                                 @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("event record list page, eventRecord={}, page={}", eventRecord, page);
        }
        return terminalEventRecordService.findRecordsForPage(eventRecord, page);
    }

    /**
     * 报警信息查询
     *
     * @return
     */
    @PermissionAnno("alarmList")
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
        if (logger.isDebugEnabled()) {
            logger.debug("find distributions by station id, gasStationId={}", gasStationId);
        }
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
        if (logger.isDebugEnabled()) {
            logger.debug("find distributions by vehicle, carNumber={}, storeId={}", carNumber, storeId);
        }
        return distributionRecordService.findDistributionsByVehicle(carNumber, storeId);
    }

    /**
     * 根据报警ID获取报警记录
     * @param alarmId 报警ID
     * @return 报警记录
     */
    @RequestMapping(value = "getAlarmRecordById.do")
    @ResponseBody
    public AlarmRecord getAlarmRecordById(Long alarmId){
        return alarmRecordService.getRecordById(alarmId);
    }

}
