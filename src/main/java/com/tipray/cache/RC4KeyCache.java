package com.tipray.cache;

import com.tipray.bean.RC4Key;
import com.tipray.bean.ResponseMsg;
import com.tipray.constant.CenterConst;
import com.tipray.core.CenterVariableConfig;
import com.tipray.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

/**
 * RC4密钥缓存
 *
 * @author chenlong
 * @version 1.0 2018-07-03
 */
public class RC4KeyCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(RC4KeyCache.class);
    private static final RC4Key RC4_KEY = new RC4Key();
    private static final String PLTONE_URL = CenterConst.PLTONE_URL;
    private static final Integer CENTER_ID = CenterConst.CENTER_ID;
    private static final Integer CENTER_VER = CenterConst.CENTER_VER;
    private static final String RC4_URL = new StringBuffer(PLTONE_URL).append("/api/getCenterRc4.do?id=")
            .append(CENTER_ID).append("&ver=").append(CENTER_VER).toString();

    static {
        if (initRc4()) {
            LOGGER.info("初始化RC4密钥成功！");
        } else {
            LOGGER.error("初始化RC4密钥失败！");
            throw new IllegalArgumentException("初始化RC4密钥失败！");
        }
    }

    /**
     * 初始化 RC4密钥缓存
     * @return 是否初始化成功
     */
    private static boolean initRc4() {
        boolean initRc4 = loadLocalRc4();
        if (!initRc4 || CenterVariableConfig.isRc4Net()) {
            initRc4 = loadPltoneRc4();
            if (!initRc4) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取RC4密钥信息
     *
     * @return RC4密钥信息
     */
    public static RC4Key getRC4Key() {
        synchronized (RC4_KEY) {
            return RC4_KEY;
        }
    }

    /**
     * 设置RC4密钥
     *
     * @param rc4Hex   RC4密钥加密16进制字符串
     * @param centerId 用户中心ID
     * @return 是否设置成功
     */
    public static boolean setRc4Key(String rc4Hex, Integer centerId) {
        try {
            byte[] encryptedData = BytesUtil.hexStringToBytes(rc4Hex);
            byte[] key = RC4Util.getKeyByDeviceId(centerId);
            byte[] decryptedData = RC4Util.rc4(encryptedData, key);
            int index = decryptedData.length - 1;
            synchronized (RC4_KEY) {
                RC4_KEY.setKey(Arrays.copyOf(decryptedData, index));
                RC4_KEY.setVer(decryptedData[index]);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("设置RC4密钥异常！", e);
            return false;
        }
    }

    /**
     * 装载本地RC4密钥
     *
     * @return 是否装载成功
     */
    public static boolean loadLocalRc4() {
        String rc4Hex = CenterVariableConfig.getRc4Key();
        if (rc4Hex == null || rc4Hex.trim().isEmpty()) {
            LOGGER.error("加载RC4密钥配置异常，RC4密钥为空！");
            return false;
        }
        return setRc4Key(rc4Hex, CENTER_ID);
    }


    /**
     * 装载普利通RC4密钥
     *
     * @return 是否装载成功
     */
    public static boolean loadPltoneRc4() {
        try {
            String rc4Hex = CenterVariableConfig.getRc4Key();

            String msgJson = OkHttpUtil.get(RC4_URL);
            ResponseMsg responseMsg = JSONUtil.parseToObject(msgJson, ResponseMsg.class);
            if (responseMsg.getId() > 0) {
                LOGGER.error("获取RC4密钥失败：{}", responseMsg.getMsg());
                return false;
            }
            String newRc4Hex = (String) responseMsg.getMsg();
            if (rc4Hex.equalsIgnoreCase(newRc4Hex)) {
                return true;
            }
            rc4Hex = newRc4Hex;
            Properties properties = new Properties();
            properties.load(RC4KeyCache.class.getClassLoader().getResourceAsStream("center-constant.properties"));
            properties.setProperty("rc4.key", rc4Hex);
            return setRc4Key(rc4Hex, CENTER_ID);
        } catch (Exception e) {
            LOGGER.error("获取RC4密钥异常！", e);
            return false;
        }
    }
}
