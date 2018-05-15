package com.tipray.controller;

import com.tipray.bean.Message;
import com.tipray.bean.ResponseMsg;
import com.tipray.bean.Session;
import com.tipray.bean.baseinfo.Role;
import com.tipray.bean.baseinfo.User;
import com.tipray.constant.reply.PermissionErrorEnum;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.LoginException;
import com.tipray.core.exception.PermissionException;
import com.tipray.service.PermissionService;
import com.tipray.service.SessionService;
import com.tipray.util.HttpServletUtil;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * session控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/session")
public class SessionController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    @Autowired
    private SessionService sessionService;
    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "login.do")
    @ResponseBody
    public ResponseMsg login(@ModelAttribute User user,
                             @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
                             ModelMap modelMap, HttpServletRequest request,
                             HttpServletResponse response) {
        try {
            HttpSession httpSession = request.getSession();
            String sessionId = httpSession.getId();
            if (isLogin(sessionId, user)) {
                return ResponseMsgUtil.success("已登录！");
            }
            Session session = new Session();
            session.setIsApp(isApp);
            session.setLoginDate(new Date());
            session.setOperateDate(new Date());
            session.setUuid(sessionId);
            session.setIp(HttpServletUtil.getHost(request));
            session = sessionService.login(user, session, isApp);

            // 添加session缓存
            SessionUtil.cacheSession(request, response, session);

            modelMap.put("isLogined", session != null);
            ResponseMsg msg;
            if (isApp == null || isApp == 0) {
                msg = ResponseMsgUtil.success();
            } else {
                Role appRole = session.getUser().getAppRole();
                String roleName = appRole.getName();
                String permissionIds = appRole.getPermissionIds();
                List<Map<String, Object>> permissions = permissionService.findByIdsForLogin(permissionIds);
                Map<String, Object> map = new HashMap<>();
                map.put("serverTime", new Date());
                map.put("role", roleName);
                map.put("permissions", permissions);
                msg = ResponseMsgUtil.success(map);
            }
            logger.info("操作员{}登录成功！", user.getAccount());
            return msg;
        } catch (LoginException e) {
            logger.error("操作员{}登录异常：{}", user.getAccount(), e.getMessage());
            return ResponseMsgUtil.excetion(e);
        } catch (PermissionException e) {
            logger.error("操作员{}登录异常：账户未配置APP角色！", user.getAccount());
            return ResponseMsgUtil.error(PermissionErrorEnum.PERMISSION_DENIED);
        }
    }

    @RequestMapping(value = "logout.do")
    @ResponseBody
    public Message logout(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            sessionService.logout(request);
            return Message.success();
        } catch (LoginException e) {
            logger.error("退出异常：{}", e.getMessage());
            return Message.error(e);
        }
    }

    @RequestMapping(value = "isAlive.do")
    @ResponseBody
    public Message isAlive() {
        return new Message(SUCCESS);
    }

    /**
     * 是否已登录
     *
     * @param sessionId
     * @param user
     * @return
     */
    private boolean isLogin(String sessionId, User user) {
        Session session = sessionService.getSessionByUUID(sessionId);
        // 当前浏览器已有用户登录
        if (session != null && !SessionUtil.isLoginTimeout(session)) {
            String loginedAccount = session.getUser().getAccount();
            String loginingAccount = user.getAccount();
            // 是否同一用户
            boolean isSameUser = loginedAccount.equalsIgnoreCase(loginingAccount);
            if (isSameUser) {
                logger.info("操作员{}已登录，不必重复登录！", loginedAccount);
                return true;
            }
            logger.warn("当前浏览器已被操作员{}登录！", loginedAccount);
            // 退出已登录用户
            sessionService.deleteSessionByUUID(sessionId);
            logger.info("操作员{}已退出，操作员{}登录准备就绪。", loginedAccount, loginingAccount);
            return false;
        }
        return false;
    }

}