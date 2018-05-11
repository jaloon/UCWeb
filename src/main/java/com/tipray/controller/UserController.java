package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.User;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.UserService;
import com.tipray.util.MD5Util;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作员管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/user")
/* @Scope("prototype") */
public class UserController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        logger.info("dispatch user edit page, mode={}, id={}", mode, id);
        modelMap.put("mode", mode);
        User user = new User();
        if (id > 0) {
            user = userService.getUserById(id);
            user.setPassword(null);
        }
        modelMap.put("user", user);
        return "normal/user/userEdit.jsp";
    }

    @PermissionAnno("userModule")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addUser(@ModelAttribute User user, Long roleId) {
        logger.info("add user, user={}, roleId={}", user, roleId);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_USER
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加操作员：").append(user.getAccount()).append('。');
        try {
            userService.addUser(user, roleId);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加操作员异常：user={}, e={}", user, e.toString());
            logger.debug("添加操作员异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("userModule")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateUser(User user, Long roleId) {
        logger.info("update user, user={}, roleId={}", user, roleId);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_USER
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改操作员：").append(user.getAccount()).append('。');
        try {
            userService.updateUser(user, roleId);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改操作员异常：user={}, e={}", user, e.toString());
            logger.debug("修改操作员异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("userModule")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteUser(Long id) {
        logger.info("delete user, id={}", id);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_USER
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除操作员：");
        try {
            description.append(userService.getUserById(id).getAccount()).append('。');
            userService.deleteUserById(id);
            description.append("成功！");
            return Message.success();
        } catch (ServiceException e) {
            type++;
            description.append("失败！");
            logger.error("删除操作员异常：id={}, e={}", id, e.toString());
            logger.debug("删除操作员异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("userModule")
    @RequestMapping(value = "reset.do")
    @ResponseBody
    public Message resetPassword(User user) {
        logger.info("reset password, user={}", user);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_USER
                | LogTypeConst.TYPE_PASSWORD_RESET | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("重置密码：");
        try {
            user.setPassword("123456");
            userService.updatePassword(user);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("重置密码异常：user={}, e={}", user, e.toString());
            logger.debug("重置密码异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("userModule")
    @RequestMapping(value = "updatePwd.do")
    @ResponseBody
    public Message updatePassword(User user, String oldPwd) {
        logger.info("update password, user={}", user);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_USER
                | LogTypeConst.TYPE_PASSWORD_ALTER | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改密码：");
        try {
            User u = userService.getUserById(user.getId());
            description.append(u.getAccount()).append("的密码。");
            String up = u.getPassword();
            String op = MD5Util.md5Encode(oldPwd);
            if (!up.equals(op)) {
                return Message.error("oldPwdError");
            }
            userService.updatePassword(user);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改密码异常：user={}, e={}", user, e.toString());
            logger.debug("修改密码异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @RequestMapping("isExist.do")
    @ResponseBody
    public Boolean isUserExist(@ModelAttribute User user) {
        logger.info("user exist, user={}", user);
        User bean = userService.getUserByAccount(user.getAccount());
        return bean != null && user.getId() != bean.getId();
    }

    @PermissionAnno("userModule")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<User> ajaxFindUsersForPage(@ModelAttribute User user, @ModelAttribute Page page) {
        logger.info("user list page, user={}, page={}", user, page);
        user.setRole(ThreadVariable.getRole());
        GridPage<User> gridPage = userService.findUsersForPage(user, page);
        return gridPage;
    }

    @PermissionAnno("userModule")
    @RequestMapping(value = "findForPage.do")
    @ResponseBody
    public String findUsersForPage(User user, Page page, ModelMap modelMap) {
        logger.info("find users for page, user={}, page={}", user, page);
        modelMap.put("user", user);
        modelMap.put("gridPage", userService.findUsersForPage(user, page));
        return "normal/user/userList.jsp";
    }

    @RequestMapping(value = "findAllUsers.do")
    @ResponseBody
    public Object findAllUsers() {
        List<Object> list = new ArrayList<>();
        userService.findAllUsers().parallelStream().forEach(user -> {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("label", user.getAccount());
            map.put("value", user.getId());
            list.add(map);
        });
        return list;
    }

}
