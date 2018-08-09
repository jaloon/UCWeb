package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.TransportCard;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.TransportCardService;
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
 * 配送卡管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/transcard")
/* @Scope("prototype") */
public class TransportCardController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(TransportCardController.class);

    @Resource
    private TransportCardService transportCardService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @PermissionAnno("transcardModule")
    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        logger.info("dispatch transportCard edit page, mode={}, id={}", mode, id);
        modelMap.put("mode", mode);
        TransportCard transportCard = new TransportCard();
        if (id != null && id > 0) {
            transportCard = transportCardService.getTransportCardById(id);
        }
        modelMap.put("transcard", transportCard);
        return "normal/transcard/transcardEdit.jsp";
    }

    @PermissionAnno("editTranscard")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addTransportCard(@ModelAttribute TransportCard transportCard) {
        logger.info("add transportCard, transportCard={}", transportCard);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DISTRIBUTION_CARD
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加配送卡：").append(transportCard.getTransportCardId()).append('。');
        try {
            transportCardService.addTransportCard(transportCard);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加配送卡异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editTranscard")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateTransportCard(TransportCard transportCard) {
        logger.info("update transportCard, transportCard={}", transportCard);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DISTRIBUTION_CARD
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改配送卡：").append(transportCard.getTransportCardId()).append('。');
        try {
            transportCardService.updateTransportCard(transportCard);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改配送卡异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editTranscard")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteTransportCard(Long id) {
        logger.info("delete transportCard, id={}", id);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DISTRIBUTION_CARD
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除配送卡：");
        try {
            transportCardService.deleteTransportCardById(id);
            description.append("成功！");
            return Message.success();
        } catch (ServiceException e) {
            type++;
            description.append("失败！");
            logger.error("删除配送卡异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("viewTranscard")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<TransportCard> ajaxFindTransportCardsForPage(@ModelAttribute TransportCard transportCard,
                                                                 @ModelAttribute Page page) {
        logger.info("transportCard list page, transportCard={}, page={}", transportCard, page);
        GridPage<TransportCard> gridPage = transportCardService.findTransportCardsForPage(transportCard, page);
        return gridPage;
    }

    @RequestMapping(value = "findUnusedTranscards.do")
    @ResponseBody
    public List<TransportCard> findUnusedTranscards() {
        return transportCardService.findUnusedTranscards();
    }
}
