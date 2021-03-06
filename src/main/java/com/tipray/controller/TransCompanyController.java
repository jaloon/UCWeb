package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.TransCompanyService;
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
 * 运输公司管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/transcom")
public class TransCompanyController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(TransCompanyController.class);

    @Resource
    private TransCompanyService transCompanyService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @PermissionAnno("transcomModule")
    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("dispatch transCompany edit page, mode={}, id={}", mode, id);
        }
        modelMap.put("mode", mode);
        TransCompany transCompany = new TransCompany();
        if (id != null && id > 0) {
            transCompany = transCompanyService.getTransCompanyById(id);
        }
        modelMap.put("transcom", transCompany);
        return "normal/transcom/transcomEdit.jsp";
    }

    @PermissionAnno("editTranscom")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addTransCompany(@ModelAttribute TransCompany transCompany) {
        if (logger.isDebugEnabled()) {
            logger.debug("add transCompany, transCompany={}", transCompany);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_TRANSPORT_COMPANY
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加运输公司：").append(transCompany.getName()).append('。');
        try {
            transCompanyService.addTransCompany(transCompany);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加运输公司异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editTranscom")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateTransCompany(TransCompany transCompany) {
        if (logger.isDebugEnabled()) {
            logger.debug("update transCompany, transCompany={}", transCompany);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_TRANSPORT_COMPANY
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改运输公司：").append(transCompany.getName()).append('。');
        try {
            transCompanyService.updateTransCompany(transCompany);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改运输公司异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editTranscom")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteTransCompany(Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete transCompany, id={}", id);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_TRANSPORT_COMPANY
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除运输公司：");
        try {
            transCompanyService.deleteTransCompanyById(id);
            description.append("成功！");
            return Message.success();
        } catch (ServiceException e) {
            type++;
            description.append("失败！");
            logger.error("删除运输公司异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @RequestMapping(value = "getCompanyList.do")
    @ResponseBody
    public List<TransCompany> getCompanyList() {
        return transCompanyService.getCompanyList();
    }

    @RequestMapping(value = "getSuperiorCompanyList.do")
    @ResponseBody
    public List<TransCompany> getSuperiorCompanyList() {
        return transCompanyService.findSuperiorCom();
    }

    @RequestMapping(value = "isExist.do")
    @ResponseBody
    public Boolean isExist(String name) {
        if (logger.isDebugEnabled()) {
            logger.debug("transcom exist, name={}", name);
        }
        return transCompanyService.getByName(name) != null;
    }

    @PermissionAnno("viewTranscom")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<TransCompany> ajaxFindTransCompanysForPage(@ModelAttribute TransCompany transCompany,
                                                               @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("transCompany list page, transCompany={}, page={}", transCompany, page);
        }
        return transCompanyService.findTransCompanysForPage(transCompany, page);
    }

}
