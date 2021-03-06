package com.tipray.service.impl;

import com.tipray.bean.Session;
import com.tipray.bean.baseinfo.User;
import com.tipray.constant.reply.PermissionErrorEnum;
import com.tipray.core.exception.LoginException;
import com.tipray.core.exception.PermissionException;
import com.tipray.dao.SessionDao;
import com.tipray.service.SessionService;
import com.tipray.service.UserService;
import com.tipray.util.MD5Util;
import com.tipray.util.SessionUtil;
import com.tipray.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chends
 */
@Service("sessionService")
public class SessionServiceImpl implements SessionService {
    @Autowired
    private UserService userService;
    @Resource
    private SessionDao sessionDao;

    @Override
    public Session getSessionByUser(Long userId, Integer isApp) {
        if (userId == null) {
            return null;
        }
        if (isApp == null) {
            isApp = 0;
        }
        List<Session> sessions = sessionDao.getByUser(userId, isApp);
        if (sessions == null) {
            return null;
        }
        int size = sessions.size();
        if (size == 1) {
            return sessions.get(0);
        }
        if (size > 1) {
            sessionDao.deleteByUser(userId);
        }
        return null;
    }

    @Override
    public Session getSessionByUUID(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return null;
        }
        return sessionDao.getByUUID(uuid);
    }

    @Override
    public Session getSessionByOldUuid(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return null;
        }
        return sessionDao.getByOldUUID(uuid);
    }

    @Transactional
    @Override
    public void deleteSessionByUUID(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return;
        }
        sessionDao.deleteByUUID(uuid);
    }

    @Transactional
    @Override
    public void updateOperateDate(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return;
        }
        Session session = new Session();
        session.setUuid(uuid);
        session.setOperateDate(new Date());
        sessionDao.updateOperateDate(session);
    }

    @Transactional
    @Override
    public void updateSession(Session session) {
        if (session == null) {
            return;
        }
        sessionDao.update(session);
    }

    @Transactional
    @Override
    public void addSession(Session session) {
        if (session == null) {
            return;
        }
        Session oldSession = getSessionByUser(session.getUser().getId(), session.getIsApp());
        if (oldSession == null) {
            sessionDao.add(session);
        } else {
            session.setOldUuid(oldSession.getUuid());
            sessionDao.update(session);
        }
    }

    @Transactional
    @Override
    public void deleteSessionByUser(Long userId) {
        if (userId != null) {
            sessionDao.deleteByUser(userId);
        }
    }

    @Transactional
    @Override
    public Session login(String account, String password, Session session, Integer isApp)
            throws LoginException, PermissionException {
        User userDb = userCheck(account, password, isApp);
        session.setUser(userDb);
        addSession(session);
        return session;
    }

    @Override
    public User userCheck(String account, String password, Integer isApp)
            throws LoginException, PermissionException {
        if (StringUtil.isEmpty(account)) {
            throw new LoginException("账号错误");
        }
        if (StringUtil.isEmpty(password)) {
            throw new LoginException("密码错误");
        }
        if (account.equals("admin") && isApp > 0) {
            throw new PermissionException(PermissionErrorEnum.APP_NOT_ACCEPTABLE);
        }
        User userDb = userService.getUserByAccount(account);
        if (userDb == null) {
            throw new LoginException("账号错误");
        }
        if (isApp == 0) {
            try {
                password = MD5Util.md5Encode(password);
            } catch (NoSuchAlgorithmException e) {
                throw new LoginException("密码加密异常", e);
            }
        }
        if (!password.equals(userDb.getPassword())) {
            throw new LoginException("密码错误");
        }
        if (isApp > 0 && (userDb.getAppRole() == null || userDb.getAppRole().getId() == 0)) {
            throw new PermissionException(PermissionErrorEnum.APP_NOT_ACCEPTABLE);
        }
        return userDb;
    }

    @Transactional
    @Override
    public void logout(HttpServletRequest request) {
        SessionUtil.removeSession(request);
    }

    @Transactional
    @Override
    public void deleteTimeOutSession(Date timeoutDate) {
        sessionDao.deleteTimeOutSession(timeoutDate);
    }

    @Override
    public Map<String, Session> findSessions() {
        return sessionDao.findSessions();
    }
}