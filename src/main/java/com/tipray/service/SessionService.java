package com.tipray.service;

import com.tipray.bean.Session;
import com.tipray.bean.baseinfo.User;
import com.tipray.core.exception.LoginException;
import com.tipray.core.exception.PermissionException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * SessionService
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
public interface SessionService {
    /**
     * 添加session
     *
     * @param session
     */
    void addSession(Session session);

    /**
     * 更新session
     *
     * @param session
     */
    void updateSession(Session session);

    /**
     * 更新操作时间
     *
     * @param uuid
     */
    void updateOperateDate(String uuid);

    /**
     * 根据session ID删除session
     *
     * @param uuid
     */
    void deleteSessionByUUID(String uuid);

    /**
     * 根据用户ID删除session
     *
     * @param userId
     */
    void deleteSessionByUser(Long userId);

    /**
     * 根据session ID获取session
     *
     * @param uuid
     * @return
     */
    Session getSessionByUUID(String uuid);

    /**
     * 根据用户ID获取session
     *
     * @param userId
     * @return
     */
    Session getSessionByUser(Long userId);

    /**
     * 根据上一次登录的sessionId，获取本次登录的sessionId(同一用户)
     *
     * @param sessionId
     * @return
     */
    Session getSessionByOldUuid(String sessionId);

    /**
     * 登录操作
     *
     * @param user
     * @param session
     * @param isApp
     * @return
     * @throws LoginException
     * @throws PermissionException
     */
    Session login(User user, Session session, Integer isApp) throws LoginException, PermissionException;

    /**
     * 用户校验
     *
     * @param user
     * @param isApp
     * @return
     * @throws LoginException
     * @throws PermissionException
     */
    User userCheck(User user, Integer isApp) throws LoginException, PermissionException;

    /**
     * 退出当前登录账号
     *
     * @param request
     */
    void logout(HttpServletRequest request) throws LoginException;

    /**
     * 删除过期session
     *
     * @param timeoutDate 过期临界时间
     */
    void deleteTimeOutSession(Date timeoutDate);

    /**
     * 获取session列表
     * @return
     */
    Map<String, Session> findSessions();
}
