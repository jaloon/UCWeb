package com.tipray.core.watcher;

import com.tipray.core.CenterVariableConfig;

/**
 * 中心变量配置变化监听
 *
 * @author chenlong
 * @version 1.0 2018-08-01
 */
public class CenterVariableWatcher extends FileWatcher {
    private static final String CENTER_VARIABLE_FILE_NAME = "center-variable.properties";

    public CenterVariableWatcher() {
        super(CENTER_VARIABLE_FILE_NAME);
    }

    public CenterVariableWatcher(String filename) {
        super(filename);
    }

    @Override
    protected void doOnChange() {
        CenterVariableConfig.loadConfig(file);
    }
}
