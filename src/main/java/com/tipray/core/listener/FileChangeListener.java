package com.tipray.core.listener;

import com.tipray.core.watcher.AppUuidConfigWatcher;
import com.tipray.core.watcher.CenterVariableWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 文件变化监听
 *
 * @author chenlong
 * @version 1.0 2018-07-04
 */
public class FileChangeListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(FileChangeListener.class);
    private AppUuidConfigWatcher appUuidConfigWatcher;
    private CenterVariableWatcher centerVariableWatcher;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        appUuidConfigWatcher = new AppUuidConfigWatcher();
        centerVariableWatcher = new CenterVariableWatcher();
        appUuidConfigWatcher.start();
        centerVariableWatcher.start();
        logger.info("app trust uuid config and center variable file watchers started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        appUuidConfigWatcher.close();
        centerVariableWatcher.close();
        logger.info("app trust uuid config and center variable file watchers stopped.");
    }
}
