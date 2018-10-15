package com.tipray.core;

import com.tipray.cache.RC4KeyCache;
import com.tipray.util.FileUtil;
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

    private static boolean emailSqlite;
    private static String[] emailSqliteReceivers;

    public static synchronized boolean isRc4Net() {
        return rc4Net;
    }

    public static synchronized String getRc4Key() {
        return rc4Key;
    }

    public static synchronized boolean isValidateLocal() {
        return validateLocal;
    }

    public static synchronized boolean isValidateAppdev() {
        return validateAppdev;
    }

    public static synchronized boolean isValidateAppver() {
        return validateAppver;
    }

    public static boolean isEmailSqlite() {
        return emailSqlite;
    }

    public static String[] getEmailSqliteReceivers() {
        return emailSqliteReceivers;
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

            rc4Net = FileUtil.getBoolProp(properties, "rc4.net", false);
            rc4Key = properties.getProperty("rc4.key");
            RC4KeyCache.loadLocalRc4();

            validateLocal = FileUtil.getBoolProp(properties, "validate.local", false);
            validateAppdev = FileUtil.getBoolProp(properties, "validate.appdev", false);
            validateAppver = FileUtil.getBoolProp(properties, "validate.appver", false);

            emailSqlite = FileUtil.getBoolProp(properties, "email.sqlite", false);
            String emailSqliteReceiverStr = properties.getProperty("email.sqlite.receiver");
            if (emailSqliteReceiverStr == null || emailSqliteReceiverStr.trim().isEmpty()) {
                emailSqliteReceivers = null;
            } else {
                emailSqliteReceivers = emailSqliteReceiverStr.split(";");
            }

        } catch (IOException e) {
            logger.error("加载中心变量配置文件异常！", e);
        }
    }
}
