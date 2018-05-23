package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.Handset;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.service.HandsetService;
import com.tipray.service.InfoManageLogService;
import com.tipray.util.OperateLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 手持机管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/handset")
/* @Scope("prototype") */
public class HandsetController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(HandsetController.class);

    @Resource
    private HandsetService handsetService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        logger.info("dispatch handset edit page, mode={}, id={}", mode, id);
        modelMap.put("mode", mode);
        Handset handset = new Handset();
        if (id != null && id > 0) {
            handset = handsetService.getHandsetById(id);
        }
        modelMap.put("handset", handset);
        return "normal/handset/handsetEdit.jsp";
    }

    @PermissionAnno("deviceModule")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addHandset(@ModelAttribute Handset handset) {
        logger.info("add handset, handset={}", handset);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_HANDSET
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加手持机：").append(handset.getDeviceId()).append('。');
        try {
            handsetService.addHandset(handset);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加手持机异常：handset={}, e={}", handset, e.toString());
            logger.debug("添加手持机异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("deviceModule")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateHandset(@ModelAttribute Handset handset) {
        logger.info("update handset, handset={}", handset);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_HANDSET
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改手持机：").append(handset.getDeviceId()).append('。');
        try {
            handsetService.updateHandset(handset);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改手持机异常：handset={}, e={}", handset, e.toString());
            logger.debug("修改手持机异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("deviceModule")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteHandset(Long id) {
        logger.info("delete card, id={}", id);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_HANDSET
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除手持机：");
        try {
            handsetService.deleteHandsetById(id);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("删除手持机异常：id={}, e={}", id, e.toString());
            logger.debug("删除手持机异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @RequestMapping(value = "getGasStationList.do")
    @ResponseBody
    public List<GasStation> getGasStationList() {
        return handsetService.getGasStationList();
    }

    @RequestMapping(value = "findUnaddHandset.do")
    @ResponseBody
    public List<Integer> findUnaddHandset() {
        return handsetService.findUnaddHandset();
    }

    @RequestMapping(value = "findUnconfigGasStation.do")
    @ResponseBody
    public List<GasStation> findUnconfigGasStation() {
        return handsetService.findUnconfigGasStation();
    }

    @PermissionAnno("viewDevice")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<Handset> ajaxFindHandsetsForPage(@ModelAttribute Handset handset, @ModelAttribute Page page) {
        logger.info("handset list page, handset={}, page={}", handset, page);
        GridPage<Handset> gridPage = handsetService.findHandsetsForPage(handset, page);
        return gridPage;
    }

}
