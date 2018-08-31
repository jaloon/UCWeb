package com.tipray.core.listener;

import com.tipray.constant.CenterConst;
import com.tipray.webservice.ElockWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.ws.Endpoint;

/**
 * WebService监听器
 *
 * @author chenlong
 * @version 1.0 2018-07-20
 */
public class WebServiceListener  implements ServletContextListener {
    private final Logger logger = LoggerFactory.getLogger(WebServiceListener.class);
    private Endpoint endpoint;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        ElockWebService elockWebService = (ElockWebService) ctx.getBean("elockWebService");
        endpoint = Endpoint.publish(CenterConst.WEBSERVICE_PUBLISH_ADDR, elockWebService);
        logger.info("WebService [{}] started.", CenterConst.WEBSERVICE_REAL_ADDR);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (endpoint != null) {
            endpoint.stop();
        }
        logger.info("WebService stopped");
    }
}
