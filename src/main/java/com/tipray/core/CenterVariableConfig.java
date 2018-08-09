package com.tipray.core;

import com.tipray.cache.RC4KeyCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 中心变量配置
 *
 * @author chenlong
 * @version 1.0 2018-08-01
 */
public class CenterVariableConfig {
    private static final Logger logger = LoggerFactory.getLogger(CenterVariableConfig.class);

    private static boolean rc4Net;
    private static String rc4Key;

    private static boolean validateLocal;
    private static boolean validateAppdev;
    private static boolean validateAppver;


    public static final synchronized boolean isRc4Net() {
        return rc4Net;
    }

    public static final synchronized String getRc4Key() {
        return rc4Key;
    }

    public static final synchronized boolean isValidateLocal() {
        return validateLocal;
    }

    public static final synchronized boolean isValidateAppdev() {
        return validateAppdev;
    }

    public static final synchronized boolean isValidateAppver() {
        return validateAppver;
    }

    /**
     * 加载中心配置文件
     *
     * @param file 中心配置文件
     */
    public static synchronized void loadConfig(File file) {
        if (!file.exists()) {
            logger.warn("中心配置文件不存在！");
            return;
        }
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));

            String rc4NetStr = properties.getProperty("rc4.net");
            if (rc4NetStr == null || rc4NetStr.trim().isEmpty()) {
                rc4Net = false;
            } else {
                rc4Net = rc4NetStr.equals("1");
            }
            rc4Key = properties.getProperty("rc4.key");
            RC4KeyCache.loadLocalRc4();

            String validateLocalStr = properties.getProperty("validate.local");
            String validateAppdevStr = properties.getProperty("validate.appdev");
            String validateAppverStr = properties.getProperty("validate.appver");

            if (validateLocalStr == null || validateLocalStr.trim().isEmpty()) {
                validateLocal = false;
            } else {
                validateLocal = validateLocalStr.equals("1");
            }
            if (validateAppdevStr == null || validateAppdevStr.trim().isEmpty()) {
                validateAppdev = false;
            } else {
                validateAppdev = validateAppdevStr.equals("1");
            }
            if (validateAppverStr == null || validateAppverStr.trim().isEmpty()) {
                validateAppver = false;
            } else {
                validateAppver = validateAppverStr.equals("1");
            }

        } catch (IOException e) {
            logger.error("加载中心变量配置文件异常！", e);
        }
    }
}
