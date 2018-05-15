package com.tipray.core.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tipray.bean.Session;
import com.tipray.core.GridProperties;
import com.tipray.util.HttpRequestUtil;
import com.tipray.util.SessionUtil;
import com.tipray.util.UUIDUtil;

/**
 * 登录验证过滤器
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class LoginValidateFilter implements Filter {

	private String errorMessage;
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;

			// 令牌检查
//			HttpSession session = request.getSession();
//			String token = (String) session.getAttribute("token");
//			if(!UUIDUtil.verifyUUIDToken(token, session)) {
//				return;
//			} else {
//				session.setAttribute("token", token);
//			}
			
			// 路径白名单检查
			String url = request.getRequestURI();
			if (isResourceRequest(url) || isNotLoginValidate(url, request)) {
				chain.doFilter(servletRequest, servletResponse);
				return;
			}
			// 判断用户是否在登录状态
			if (isLogin(request)) {
				// 更新session时间：心跳检测的请求不更新
				if (url.indexOf("/manage/session/isAlive.do") < 0) {
					SessionUtil.updateSession(request);
				}
				chain.doFilter(servletRequest, servletResponse);
			} else {
				String path = request.getContextPath();
				String requestUrl = HttpRequestUtil.getRequestUrl(request);
				encodeUrl(requestUrl);

				String location = new StringBuffer(path).append("/login.jsp?requestUrl=").append(requestUrl)
						.append("&errorMessage=").append(errorMessage).toString();
//				response.setContentType("text/html");
//				String responsePage = new StringBuffer("<script>document.location.href='").append(location)
//						.append("'</script>").toString();
//				response.getWriter().print(responsePage);
				response.sendRedirect(location);
			}
		} catch (Exception e) {
			log.error("登录过滤器异常：\n{}", e.toString());
			throw new ServletException(e);
		}
	}

	/**
	 * 是否登录
	 * 
	 * @param request
	 * @return
	 */
	private boolean isLogin(HttpServletRequest request) {
		Session session = SessionUtil.getSession(request);
		// 未登录
		if (session == null) {
			errorMessage = "";
			Session oldSession = SessionUtil.getOldSession(request);
			// 异地登录
			if (oldSession != null) {
				errorMessage =  new StringBuffer("<div style=\"font-size:15px;line-height:20px;\">您的账号在异地登录(")
						.append(oldSession.getIp()).append(")<br>如非授权，建议修改密码</div>").toString();
				encodeUrl(errorMessage);
			}
			return false;
		}
		// 登录超时
		if (SessionUtil.isLoginTimeout(session)) {
			SessionUtil.removeSession(session);
			request.getSession().invalidate();
			errorMessage = encodeUrl("因长时间未操作，系统已自动退出，请重新登录");
			return false;
		}
		return true;
	}

	/**
	 * 不用过滤的额外配置 没有登录时，有些请求是必须的，因此不用过滤
	 * 
	 * @param url
	 * @param request
	 * @return
	 */
	private boolean isNotLoginValidate(String url, HttpServletRequest request) {
		for (String path : GridProperties.NOT_VALIDATE_PATH) {
			if (url.startsWith(request.getContextPath() + path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 资源请求
	 * 
	 * @param url
	 * @return
	 */
	private boolean isResourceRequest(String url) {
        return url.endsWith(".jpg") || url.endsWith(".gif") || url.endsWith(".css") || url.endsWith(".js")
                || url.endsWith(".png") || url.endsWith(".bmp") || url.endsWith(".ico") || url.endsWith(".txt")
                || url.endsWith(".apk") || url.endsWith("bootstrap.min.css.map");
    }

	/**
	 * 编码URL字符串
	 * 
	 * @param url
	 * @return
	 */
	private String encodeUrl(String url) {
		try {
			url = URLEncoder.encode(url, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString());
		}
		return url;
	}

	@Override
	public void destroy() {
		this.errorMessage = "";
	}

	@Override
	public void init(FilterConfig filterConfig) {
		this.errorMessage = "";
	}

}