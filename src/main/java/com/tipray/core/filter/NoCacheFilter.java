package com.tipray.core.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 防止浏览器缓存页面或请求结果
 *
 * @author chenlong
 * @version 1.0 2018-09-18
 */
public class NoCacheFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;

        // 告诉浏览器数据可以缓存多长时间，-1或0表示不缓存
        response.setDateHeader("Expires", -1);
        // 支持HTTP 1.1，告诉浏览器要不要缓存数据，如“no-cache”
        response.setHeader("Cache_Control", "no-cache");
        // 支持HTTP 1.0，告诉浏览器要不要缓存数据，如“no-cache”
        response.setHeader("Pragma", "no-cache");

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {

    }
}
