package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Card;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.service.CardService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/card")
public class CardController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    @Resource
    private CardService cardService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @PermissionAnno("cardModule")
    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("dispatch card edit page, mode={}, id={}", mode, id);
        }
        modelMap.put("mode", mode);
        Map<String, Object> map = new HashMap<String, Object>();
        if (id != null && id > 0) {
            map = cardService.getCardById(id);
        }
        modelMap.put("card", map.get("card"));
        modelMap.put("oilDepots", map.get("oilDepots"));
        modelMap.put("gasStations", map.get("gasStations"));
        return "normal/card/cardEdit.jsp";
    }

    @PermissionAnno("editCard")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addCard(@ModelAttribute Card card) {
        if (logger.isDebugEnabled()) {
            logger.debug("add card, card={}", card);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_CARD
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加卡：").append(card.getCardId()).append('。');
        try {
            cardService.addCard(card);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加卡异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editCard")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateCard(@ModelAttribute Card card) {
        if (logger.isDebugEnabled()) {
            logger.debug("update card, card={}", card);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_CARD
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改卡：").append(card.getCardId()).append('。');
        try {
            cardService.updateCard(card);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改卡异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editCard")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteCard(Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete card, id={}", id);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_CARD
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除卡：");
        try {
            cardService.deleteCardById(id);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("删除卡异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @RequestMapping(value = "isExist.do")
    @ResponseBody
    public Boolean isCardExist(Long cardId) {
        if (logger.isDebugEnabled()) {
            logger.debug("card exist, cardId={}", cardId);
        }
        return cardService.isCardExist(cardId) != null;
    }

    @PermissionAnno("viewCard")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<Card> ajaxFindCardsForPage(@ModelAttribute Card card, @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("card list page, card={}, page={}", card, page);
        }
        return cardService.findCardsForPage(card, page);
    }

    @RequestMapping(value = "findUnusedCard.do")
    @ResponseBody
    public List<Long> findUnusedCard(Integer cardType) {
        if (logger.isDebugEnabled()) {
            logger.debug("find unused card, cardType={}", cardType);
        }
        return cardService.findUnusedCard(cardType);
    }
}