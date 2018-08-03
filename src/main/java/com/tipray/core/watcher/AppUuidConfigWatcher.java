package com.tipray.core.watcher;

import com.tipray.cache.TrustAppUuidCache;

/**
 * 中心配置文件监听
 *
 * @author chenlong
 * @version 1.0 2018-07-04
 */
public class AppUuidConfigWatcher extends FileWatcher {
    private static final String APP_UUID_FILE_NAME = "app.uuid";

    public AppUuidConfigWatcher() {
        super(APP_UUID_FILE_NAME);
    }

    public AppUuidConfigWatcher(String filename) {
        super(filename);
    }

    @Override
    protected void doOnChange() {
        TrustAppUuidCache.upUuidList(file);
    }
}
