package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Driver;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.DriverService;
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
 * 司机管理控制器
 * 
 * @author chenlong
 * @version 1.0 2017-11-22
 *
 */
@Controller
@RequestMapping("/manage/driver")
public class DriverController extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

	@Resource
	private DriverService driverService;
	@Resource
	private InfoManageLogService infoManageLogService;

	@PermissionAnno("driverModule")
	@RequestMapping(value = "dispatch.do")
	public String dispatch(String mode, Long id, ModelMap modelMap) {
		if (logger.isDebugEnabled()) {
            logger.debug("dispatch driver edit page, mode={}, id={}", mode, id);
        }
		modelMap.put("mode", mode);
		Driver driver = new Driver();
		if (id != null && id > 0) {
			driver = driverService.getDriverById(id);
		}
		modelMap.put("driver", driver);
		return "normal/driver/driverEdit.jsp";
	}

	@PermissionAnno("editDriver")
	@RequestMapping(value = "add.do")
	@ResponseBody
	public Message addDriver(@ModelAttribute Driver driver) {
        if (logger.isDebugEnabled()) {
            logger.debug("add driver, driver={}", driver);
        }
		InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
		Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DRIVER
				| LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
		StringBuffer description = new StringBuffer("添加司机：").append(driver.getName()).append('。');
		try {
			driverService.addDriver(driver);
			description.append("成功！");
			return Message.success();
		} catch (Exception e) {
			type++;
			description.append("失败！");
			logger.error("添加司机异常！", e);
			return Message.error(e);
		} finally {
			OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
		}
	}

	@PermissionAnno("editDriver")
	@RequestMapping(value = "update.do")
	@ResponseBody
	public Message updateDriver(Driver driver) {
        if (logger.isDebugEnabled()) {
            logger.debug("update driver, driver={}", driver);
        }
		InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
		Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DRIVER
				| LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
		StringBuffer description = new StringBuffer("修改司机：").append(driver.getName()).append('。');
		try {
			driverService.updateDriver(driver);
			description.append("成功！");
			return Message.success();
		} catch (Exception e) {
			type++;
			description.append("失败！");
            logger.error("修改司机异常！", e);
			return Message.error(e);
		} finally {
			OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
		}
	}

	@PermissionAnno("editDriver")
	@RequestMapping(value = "delete.do")
	@ResponseBody
	public Message deleteDriver(Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete driver, id={}", id);
        }
		InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
		Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DRIVER
				| LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
		StringBuffer description = new StringBuffer("删除司机：");
		try {
			driverService.deleteDriverById(id);
			description.append("成功！");
			return Message.success();
		} catch (ServiceException e) {
			type++;
			description.append("失败！");
            logger.error("删除司机异常！", e);
			return Message.error(e);
		} finally {
			OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
		}
	}

	@PermissionAnno("viewDriver")
	@RequestMapping(value = "ajaxFindForPage.do")
	@ResponseBody
	public GridPage<Driver> ajaxFinddriversForPage(@ModelAttribute Driver driver, @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("driver list page, driver={}, page={}", driver, page);
        }
        return driverService.findDriversForPage(driver, page);
	}

	@RequestMapping(value = "findFreeDrivers.do")
	@ResponseBody
	public List<Driver> findFreeDrivers() {
	    return driverService.findFreeDrivers();
	}

	@RequestMapping(value = "getDriverById.do")
	@ResponseBody
	public Driver getDriverById(Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("get driver by id, id={}", id);
        }
	    return driverService.getDriverById(id);
	}
}
