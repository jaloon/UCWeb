package com.tipray.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tipray.util.StringUtil;

/**
 * WebServiceFilter
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class WebServiceFilter implements Filter {
	private final Logger log = LoggerFactory.getLogger("HTTP_WS_LOG");

	private static final String TOKEN_NAME = "webservice_token_id";

	@Override
	public void init(FilterConfig fConfig) {

	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		long begin = System.currentTimeMillis();
		WSHttpServletRequestWrapper requestWrapper = null;

		String type = request.getContentType();
		if ((null != type) && (type.startsWith("text/xml"))) {
			if (request instanceof HttpServletRequest) {
				requestWrapper = new WSHttpServletRequestWrapper((HttpServletRequest) request);
			}
			if (null != requestWrapper) {
				byte[] reqbuf = requestWrapper.getBody();
				if (reqbuf.length > 0) {
					// 从XML中获取 <token 的位置
					String xmlHead = new String(reqbuf, "UTF-8");
					if (log.isDebugEnabled()) {
						log.debug(xmlHead);
					}
					setToken(request, xmlHead);
					byte[] fXmlData = formatterXmlData(xmlHead);
					if (fXmlData != null) {
						requestWrapper.setBody(fXmlData);
					}
				}
			} else {
				log.info("requestWrapper is null");
			}
		} else if ((null != type) && (type.startsWith("application/xop+xml"))) {// DocumentService改用MTOM形式来支持大块文件的传输

		}

		if (null == requestWrapper) {
			chain.doFilter(request, response);
		} else {
			chain.doFilter(requestWrapper, response);
		}

		log.trace("doFilter.Time: {}", System.currentTimeMillis() - begin);
		log.debug("doFilter: void");
	}

	/**
	 * 为session设置登录令牌
	 * 
	 * @param request
	 * @param xmlData
	 */
	private void setToken(ServletRequest request, String xmlData) {
		String tokenTag = "<token>", tokenETag = "</token>";
		try {
			int iTokenMarkBegin = xmlData.indexOf(tokenTag);
			int iTokenMarkEnd = xmlData.indexOf(tokenETag);
			if (iTokenMarkBegin < 0 || iTokenMarkEnd < iTokenMarkBegin) {
				return;
			}
			String token = xmlData.substring(iTokenMarkBegin + tokenTag.length(), iTokenMarkEnd);

			if (null != token && !token.isEmpty()) {
				HttpSession session = ((HttpServletRequest) request).getSession();
				if (null != session) {
					session.setAttribute(TOKEN_NAME, token);
					log.info("doFilter,session:{} set token:{}", session.getId(), token);
				}
			} else {
				log.info("doFilter,token:{}", token);
			}
		} catch (Exception e) {
			log.error("doFilter,set token error:{}", e.getMessage());
		}
	}

	/**
	 * 格式化xml请求数据，避免内容乱码导致异常
	 * 
	 * @param xmlData
	 * @return
	 */
	private byte[] formatterXmlData(String xmlData) {
		try {
			String contentTag = "<content>", contentETag = "</content>";

			int iBegin = xmlData.indexOf(contentTag);
			int iEnd = xmlData.indexOf(contentETag);
			if (iBegin < 0 || iEnd < iBegin) {
				return null;
			}
			String content = xmlData.substring(iBegin + contentTag.length(), iEnd);
			if (StringUtil.isNotEmpty(content) && !(content.startsWith("<![CDATA[") && content.endsWith("]]>"))) {
				xmlData = xmlData.replace(content, "<![CDATA[" + content + "]]>");
				return xmlData.getBytes("UTF-8");
			}
		} catch (Exception e) {
			log.error("doFilter,formatterXmlData error:{}", e.getMessage());
		}
		return null;
	}
}
