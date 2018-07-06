package com.tipray.cache;

import com.tipray.bean.RC4Key;
import com.tipray.bean.ResponseMsg;
import com.tipray.constant.CenterConfigConst;
import com.tipray.util.BytesUtil;
import com.tipray.util.HttpRequestUtil;
import com.tipray.util.JSONUtil;
import com.tipray.util.RC4Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * RC4密钥缓存
 *
 * @author chenlong
 * @version 1.0 2018-07-03
 */
public class RC4KeyCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(RC4KeyCache.class);
    private static final RC4Key RC4_KEY = new RC4Key();
    private static final String PLTONE_URL = CenterConfigConst.PLTONE_URL;
    private static final Integer CENTER_ID = CenterConfigConst.CENTER_ID;
    private static final Integer CENTER_VER = CenterConfigConst.CENTER_VER;
    private static final String HTTP_URL = new StringBuffer(PLTONE_URL).append("/api/getCenterRc4.do").toString();
    private static final String HTTP_PARAM = new StringBuffer("id=").append(CENTER_ID).append("&ver=").append(CENTER_VER).toString();

    static {
        initRc4();
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
     * 初始化RC4密钥
     *
     * @return 是否初始化成功
     */
    public static boolean initRc4() {
        try {
            String msgJson = HttpRequestUtil.sendGet(HTTP_URL, HTTP_PARAM);
            ResponseMsg responseMsg = JSONUtil.parseToObject(msgJson, ResponseMsg.class);
            if (responseMsg.getId() > 0) {
                throw new IllegalArgumentException(responseMsg.getMsg() + HTTP_PARAM);
            }
            String rc4Hex = (String) responseMsg.getMsg();
            byte[] encryptedData = BytesUtil.hexStringToBytes(rc4Hex);
            byte[] key = RC4Util.getKeyByDeviceId(CENTER_ID);
            byte[] decryptedData = RC4Util.rc4(encryptedData, key);
            synchronized (RC4_KEY) {
                RC4_KEY.setKey(Arrays.copyOf(decryptedData, decryptedData.length - 1));
                RC4_KEY.setVer(decryptedData[decryptedData.length - 1]);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("获取RC4密钥异常！", e);
            return false;
        }
    }
}
