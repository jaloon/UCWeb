package com.tipray.core.filter;

import com.tipray.core.CenterVariableConfig;
import com.tipray.core.ThreadVariable;
import com.tipray.util.JSONUtil;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 远程操作密码验证过滤器
 *
 * @author chenlong
 * @version 1.0 2018-11-01
 */
public class RemoteOperatorFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RemoteOperatorFilter.class);
    private static final String PASS_CHECK_MILLIS = "PASS_CHECK_MILLIS";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String url = request.getRequestURI();
        if (!url.contains("_request")) {
            chain.doFilter(req, resp);
            return;
        }
        HttpSession session = request.getSession();
        Object checkObj = session.getAttribute(PASS_CHECK_MILLIS);
        long lastCheckMillis = checkObj == null ? 0L : (long) checkObj;
        long currentMillis = System.currentTimeMillis();
        // 距上次密码验证超过密码校验超时时间
        if (currentMillis - lastCheckMillis >= CenterVariableConfig.getPasscheckTimeout() * 1000L) {
            String account = ThreadVariable.getUser().getAccount();
            String password = request.getParameter("password");
            int isApp = convertStringToInt(request.getParameter("is_app"), 0);
            try {
                SpringBeanUtil.getSessionService().userCheck(account, password, isApp);
                // 验证通过，更新校验时间
                session.setAttribute(PASS_CHECK_MILLIS, currentMillis);
                chain.doFilter(req, resp);
            } catch (Exception e) {
                logger.error("操作员{}通过{}对车辆进行远程操作，密码[{}]校验失败：{}",
                        account, isApp > 0 ? "手机APP" : "网页", password, e.getMessage());
                response.getWriter().print(JSONUtil.stringify(ResponseMsgUtil.exception(e)));
            }
        } else {
            // 未超时，直接放行
            chain.doFilter(req, resp);
        }
    }

    /**
     * 字符串转int
     *
     * @param value        数值字符串
     * @param defaultValue 默认值
     * @return int数值
     */
    private int convertStringToInt(String value, int defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void destroy() {

    }
}
