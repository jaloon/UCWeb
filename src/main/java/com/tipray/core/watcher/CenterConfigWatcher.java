package com.tipray.core.watcher;

/**
 * 中心配置文件监听
 *
 * @author chenlong
 * @version 1.0 2018-07-04
 */
public class CenterConfigWatcher extends FileWatcher {
    private static final String CENTER_CONF_FILE_NAME = "center.properties";

    public CenterConfigWatcher() {
        super(CENTER_CONF_FILE_NAME);
    }

    public CenterConfigWatcher(String filename) {
        super(filename);
    }

    @Override
    protected void doOnChange() {
        // CenterConfig.loadConfig(file);
    }
}
