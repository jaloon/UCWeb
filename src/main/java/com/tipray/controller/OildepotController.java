package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Card;
import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.constant.PageActionMode;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.service.CardService;
import com.tipray.service.InOutReaderService;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.OilDepotService;
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
import java.util.Map;

/**
 * 油库管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-11-13
 */
@Controller
@RequestMapping("/manage/oildepot")
/* @Scope("prototype") */
public class OildepotController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(OildepotController.class);

    @Resource
    private OilDepotService oilDepotService;
    @Resource
    private InOutReaderService inOutReaderService;
    @Resource
    private CardService cardService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @PermissionAnno("oilDepotModule")
    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        logger.info("dispatch oilDepot edit page, mode={}, id={}", mode, id);
        modelMap.put("mode", mode);
        OilDepot oilDepot = new OilDepot();
        List<InOutReader> readers = new ArrayList<InOutReader>();
        List<Card> cards = new ArrayList<Card>();
        if (id != null && id > 0) {
            oilDepot = oilDepotService.getOilDepotById(id);
            readers = inOutReaderService.findByOilDepotId(id);
            cards = cardService.findByOilDepotId(id);
            if (PageActionMode.EDIT.equals(mode)) {
                modelMap.put("barrierCount", oilDepotService.barrierCount(id));
            }
        }
        modelMap.put("oilDepot", oilDepot);
        modelMap.put("readers", readers);
        modelMap.put("cards", cards);

        return "normal/oildepot/oildepotEdit.jsp";
    }

    @PermissionAnno("editOildepot")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addOilDepot(@ModelAttribute OilDepot oilDepot) {
        logger.info("add oilDepot, oilDepot={}", oilDepot);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_OIL_DEPOT
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加油库：").append(oilDepot.getName()).append('。');
        try {
            oilDepotService.addOilDepot(oilDepot);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加油库异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editOildepot")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateOilDepot(@ModelAttribute OilDepot oilDepot, String readersJson, String cardIds) {
        logger.info("update oilDepot, oilDepot={}", oilDepot);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_OIL_DEPOT
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改油库：").append(oilDepot.getName()).append('。');
        try {
            oilDepotService.updateOilDepot(oilDepot, readersJson, cardIds);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改油库异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editOildepot")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteOilDepot(Long id) {
        logger.info("delete card, id={}", id);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_OIL_DEPOT
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除油库：");
        try {
            oilDepotService.deleteOilDepotById(id);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("删除油库异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @RequestMapping(value = "findUnusedReaders.do")
    @ResponseBody
    public List<InOutReader> findUnusedReaders() {
        return oilDepotService.findUnusedReaders();
    }

    @RequestMapping(value = "isExist.do")
    @ResponseBody
    public Boolean isOilDepotExist(OilDepot oilDepot) {
        logger.info("oildepot exist, oilDepot={}", oilDepot);
        return oilDepotService.isOilDepotExist(oilDepot);
    }

    @PermissionAnno("viewOildepot")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<OilDepot> ajaxFindOilDepotsForPage(@ModelAttribute OilDepot oilDepot, @ModelAttribute Page page) {
        logger.info("oilDepot list page, oilDepot={}, page={}", oilDepot, page);
        return oilDepotService.findOilDepotsForPage(oilDepot, page);
    }

    @RequestMapping(value = "getAllOilDepotsAndGasStations.do")
    @ResponseBody
    public Map<String, Object> getIdAndNameOfAllOilDepotsAndGasStations() {
        return oilDepotService.getIdAndNameOfAllOilDepotsAndGasStations();
    }

    @RequestMapping(value = "getIdAndNameOfAllOilDepotsAndGasStations.do")
    @ResponseBody
    public Map<String, Object> getIdAndNameOfAllOilDepotsAndGasStations(Long depotVer, Long stationVer) {
        logger.info("get id and name of all depots and stations, depotVer={}, stationVer={}", depotVer, stationVer);
        return oilDepotService.getIdAndNameOfAllOilDepotsAndGasStations(depotVer, stationVer);
    }

}
