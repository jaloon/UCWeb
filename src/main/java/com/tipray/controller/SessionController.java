package com.tipray.controller;

import com.tipray.bean.Message;
import com.tipray.bean.ResponseMsg;
import com.tipray.bean.Session;
import com.tipray.bean.baseinfo.Role;
import com.tipray.bean.baseinfo.User;
import com.tipray.cache.TrustAppUuidCache;
import com.tipray.constant.reply.LoginErrorEnum;
import com.tipray.constant.reply.PermissionErrorEnum;
import com.tipray.core.CenterVariableConfig;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.LoginException;
import com.tipray.core.exception.PermissionException;
import com.tipray.service.AppService;
import com.tipray.service.PermissionService;
import com.tipray.service.SessionService;
import com.tipray.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private AppService appService;
    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "login.do")
    @ResponseBody
    public ResponseMsg login(@ModelAttribute User user,
                             @RequestParam(value = "is_app", required = false, defaultValue = "0") Integer isApp,
                             @RequestParam(value = "uuid", required = false) String uuid,
                             @RequestParam(value = "system", required = false) String system,
                             @RequestParam(value = "app_ver", required = false) String appVer,
                             ModelMap modelMap, HttpServletRequest request,
                             HttpServletResponse response) {
        try {
            String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
            if (isApp > 0) {
                logger.info("操作员{}请求APP登录，uuid：{}，system：{}，app_ver：{}，user-agent：{}",
                        user.getAccount(), uuid, system, appVer, userAgent);
                if (CenterVariableConfig.isValidateAppdev()) {
                    // APP设备认证
                    if (StringUtil.isEmpty(uuid)) {
                        logger.warn("登录失败：{}", LoginErrorEnum.MOBILE_UUID_NULL);
                        return ResponseMsgUtil.error(LoginErrorEnum.MOBILE_UUID_NULL);
                    }
                    if (CenterVariableConfig.isValidateLocal()) {
                        // 通过本地配置文件验证UUID
                        if (!TrustAppUuidCache.checkAppId(uuid)) {
                            logger.warn("登录失败：{}", LoginErrorEnum.MOBILE_NOT_CERTIFY);
                            return ResponseMsgUtil.error(LoginErrorEnum.MOBILE_NOT_CERTIFY);
                        }
                    } else {
                        // 通过普利通中心同步的APP设备信息数据认证
                        if (appService.isAppdevExist(uuid)) {
                            logger.warn("登录失败：{}", LoginErrorEnum.MOBILE_NOT_CERTIFY);
                            return ResponseMsgUtil.error(LoginErrorEnum.MOBILE_NOT_CERTIFY);
                        }
                    }
                }
                if (CenterVariableConfig.isValidateAppver()) {
                    // APP版本验证
                    if (StringUtil.isEmpty(system)) {
                        logger.warn("登录失败：{}", LoginErrorEnum.MOBILE_SYSTEM_NULL);
                        return ResponseMsgUtil.error(LoginErrorEnum.MOBILE_SYSTEM_NULL);
                    }
                    if (StringUtil.isEmpty(appVer)) {
                        logger.warn("登录失败：{}", LoginErrorEnum.APP_VERSION_NULL);
                        return ResponseMsgUtil.error(LoginErrorEnum.APP_VERSION_NULL);
                    }
                    String minimumVer = appService.getMinverBySystem(system);
                    if (StringUtil.isEmpty(minimumVer)) {
                        logger.warn("登录失败：{}", LoginErrorEnum.APP_VERSION_CONFIG_INVALID);
                        return ResponseMsgUtil.error(LoginErrorEnum.APP_VERSION_CONFIG_INVALID);
                    }
                    if (VersionUtil.compareVer(appVer, minimumVer) < 0) {
                        logger.warn("登录失败：{}", LoginErrorEnum.APP_VERSION_OUTDATED);
                        return ResponseMsgUtil.error(LoginErrorEnum.APP_VERSION_OUTDATED);
                    }
                }
            } else {
                logger.info("操作员{}请求网页登录，user-agent：{}", user.getAccount(), userAgent);
            }
            // HttpSession httpSession = request.getSession();
            // String sessionId = httpSession.getId();
            String sessionId = SessionUtil.getLoginSessionId(request);
            if (isLogin(sessionId, user)) {
                if (isApp == null || isApp == 0) {
                    return ResponseMsgUtil.success("已登录！");
                }
                Role appRole = sessionService.userCheck(user, isApp).getAppRole();
                return ResponseMsgUtil.success(getAppPermissions(appRole));
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
                msg = ResponseMsgUtil.success(getAppPermissions(appRole));
            }
            logger.info("操作员{}登录成功！", user.getAccount());
            return msg;
        } catch (LoginException e) {
            logger.error("操作员{}登录异常：{}", user.getAccount(), e.getMessage());
            return ResponseMsgUtil.exception(e);
        } catch (PermissionException e) {
            logger.error("操作员{}登录异常：账户未配置APP角色！", user.getAccount());
            return ResponseMsgUtil.error(PermissionErrorEnum.APP_NOT_ACCEPTABLE);
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
     * @param sessionId {@link String}
     * @param user {@link User}
     * @return 是否已登录
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

    /**
     * 获取APP角色权限
     * @param appRole {@link Role} APP角色
     * @return {@link Map} APP角色权限信息
     */
    private Map<String, Object> getAppPermissions(Role appRole) {
        String roleName = appRole.getName();
        String permissionIds = appRole.getPermissionIds();
        List<Map<String, Object>> permissions = permissionService.findByIdsForLogin(permissionIds);
        Map<String, Object> map = new HashMap<>();
        map.put("serverTime", new Date());
        map.put("role", roleName);
        map.put("permissions", permissions);
        return map;
    }
}
