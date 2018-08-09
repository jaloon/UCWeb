package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Card;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.Handset;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.service.CardService;
import com.tipray.service.GasStationService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 加油站管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-11-22
 */
@Controller
@RequestMapping("/manage/gasstation")
/* @Scope("prototype") */
public class GasStationController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(GasStationController.class);

    @Resource
    private GasStationService gasStationService;
    @Resource
    private HandsetService handsetService;
    @Resource
    private CardService cardService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @PermissionAnno("gasstationModule")
    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        logger.info("dispatch gasStation edit page, mode={}, id={}", mode, id);
        modelMap.put("mode", mode);
        GasStation gasStation = new GasStation();
        List<Card> cards = new ArrayList<Card>();
        Handset handset = new Handset();
        if (id != null && id > 0) {
            gasStation = gasStationService.getGasStationById(id);
            cards = cardService.findByGasStationId(id);
            handset = handsetService.getByGasStationId(id);
        }
        modelMap.put("gasStation", gasStation);
        modelMap.put("handset", handset);
        modelMap.put("cards", cards);
        return "normal/gasstation/gasstationEdit.jsp";
    }

    @PermissionAnno("editGasstation")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addGasStation(@ModelAttribute GasStation gasStation) {
        logger.info("add gasStation, gasStation={}", gasStation);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_GAS_STATION
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加加油站：").append(gasStation.getName()).append('。');
        try {
            gasStationService.addGasStation(gasStation);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加加油站异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editGasstation")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateGasStation(@ModelAttribute GasStation gasStation, Integer handset, String cardIds) {
        logger.info("update gasStation, gasStation={}", gasStation);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_GAS_STATION
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改加油站：").append(gasStation.getName()).append('。');
        try {
            gasStationService.updateGasStation(gasStation, handset, cardIds);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改加油站异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editGasstation")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteGasStation(Long id) {
        logger.info("delete gasStation, id={}", id);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_GAS_STATION
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除加油站：");
        try {
            gasStationService.deleteGasStationById(id);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("删除加油站异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @RequestMapping(value = "findUnusedHandset.do")
    @ResponseBody
    public List<Integer> findUnusedHandset() {
        return handsetService.findUnusedHandset();
    }

    @RequestMapping(value = "isExist.do")
    @ResponseBody
    public Boolean isGasStationExist(GasStation gasStation) {
        logger.info("gasStation exist, gasStation={}", gasStation);
        return gasStationService.isGasStationExist(gasStation) != null;
    }

    @RequestMapping(value = "getGasStationList.do")
    @ResponseBody
    public List<GasStation> findAllGasStations() {
        return gasStationService.findAllGasStations();
    }

    @PermissionAnno("viewGasstation")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<GasStation> ajaxFindGasStationsForPage(@ModelAttribute GasStation gasStation,
                                                           @ModelAttribute Page page) {
        logger.info("gasStation list page, gasStation={}, page={}", gasStation, page);
        return gasStationService.findGasStationsForPage(gasStation, page);
    }

}
