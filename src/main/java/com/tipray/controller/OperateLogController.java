package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.core.base.BaseAction;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.VehicleManageLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 操作日志管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/log")
/* @Scope("prototype") */
public class OperateLogController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(OperateLogController.class);
    @Resource
    private InfoManageLogService infoManageLogService;
    @Resource
    private VehicleManageLogService vehicleManageLogService;

    @RequestMapping(value = "ajaxFindInfoLogsForPage.do")
    @ResponseBody
    public GridPage<InfoManageLog> ajaxFindInfoManageLogsForPage(@ModelAttribute InfoManageLog infoManageLog,
                                                                 @ModelAttribute Page page) {
        logger.info("infoManageLog list page, infoManageLog={}, page={}", infoManageLog, page);
        GridPage<InfoManageLog> gridPage = infoManageLogService.findInfoManageLogsForPage(infoManageLog, page);
        return gridPage;
    }

    @RequestMapping(value = "ajaxFindCarLogsForPage.do")
    @ResponseBody
    public GridPage<VehicleManageLog> ajaxFindVehicleManageLogsForPage(@ModelAttribute VehicleManageLog infoManageLog,
                                                                       @ModelAttribute Page page) {
        logger.info("infoManageLog list page, infoManageLog={}, page={}", infoManageLog, page);
        GridPage<VehicleManageLog> gridPage = vehicleManageLogService.findVehicleManageLogsForPage(infoManageLog, page);
        return gridPage;
    }
}
