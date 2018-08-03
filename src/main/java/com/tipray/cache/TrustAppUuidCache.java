package com.tipray.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * APP信任UUID缓存
 *
 * @author chenlong
 * @version 1.0 2018-07-12
 */
public class TrustAppUuidCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrustAppUuidCache.class);
    private static final List<String> UUID_LIST = new ArrayList<>();

    /**
     * 校验手机APP是否可信
     * @param appId APP的UUID
     * @return
     */
    public static boolean checkAppId(String appId) {
        if (appId == null) {
            return false;
        }
        appId = appId.trim();
        if (appId.isEmpty()) {
            return false;
        }
        synchronized (UUID_LIST) {
            if (UUID_LIST.contains(appId)) {
                return true;
            }
            return false;
        }
    }

    public static List<String> getUuidList() {
        return UUID_LIST;
    }

    /**
     * 更新APP信任UUID列表
     *
     * @return
     */
    public static boolean upUuidList(File file) {
        if (file.exists()) {
            synchronized (UUID_LIST) {
                UUID_LIST.clear();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String mac = null;
                    while ((mac = br.readLine()) != null) { //使用readLine方法，一次读一行
                        UUID_LIST.add(mac);
                    }
                    return true;
                } catch (Exception e) {
                    LOGGER.error("更新APP信任UUID列表异常！", e);
                }
            }
        }
        return false;
    }
}
