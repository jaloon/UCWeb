package com.tipray.net;

import com.tipray.bean.ResponseMsg;
import com.tipray.constant.CenterConfigConst;
import com.tipray.util.BytesUtil;
import com.tipray.util.HttpRequestUtil;
import com.tipray.util.JSONUtil;
import com.tipray.util.RC4Util;

import java.util.Arrays;

/**
 * RC4密钥
 *
 * @author chenlong
 * @version 1.0 2018-06-29
 */
public enum Rc4Key {
    INSTANCE;

    private static final String RC4_URL = new StringBuffer(CenterConfigConst.PLTONE_URL).append("/api/getCenterRc4.do").toString();
    private static final String RC4_PARAM = new StringBuffer("id=").append(CenterConfigConst.CENTER_ID).append("&ver=")
            .append(CenterConfigConst.CENTER_VER).toString();

    /**
     * 中心rc4密钥
     */
    private byte[] RC4_KEY;
    /**
     * 中心rc4秘钥版本
     */
    private byte RC4_VER;

    Rc4Key() {
        initRc4();
    }

    /**
     * 初始化RC4秘钥和版本号
     */
    private void initRc4() {
        try {
            String msgJson = HttpRequestUtil.sendGet(RC4_URL, RC4_PARAM);
            ResponseMsg responseMsg = JSONUtil.parseToObject(msgJson, ResponseMsg.class);
            if (responseMsg.getId() > 0) {
                throw new IllegalArgumentException(responseMsg.getMsg() + RC4_PARAM);
            }
            String rc4Hex = (String) responseMsg.getMsg();
            byte[] encryptedData = BytesUtil.hexStringToBytes(rc4Hex);
            byte[] key = RC4Util.getKeyByDeviceId(CenterConfigConst.CENTER_ID);
            byte[] decryptedData = RC4Util.rc4(encryptedData, key);
            RC4_KEY = Arrays.copyOf(decryptedData, decryptedData.length - 1);
            RC4_VER = decryptedData[decryptedData.length - 1];
        } catch (Exception e) {
            // logger.error("获取RC4密钥异常：\n{}", e.toString());
            throw new IllegalArgumentException("获取RC4密钥失败");
        }
    }

    public byte[] getRc4Key() {
        return RC4_KEY;
    }

    public byte getRc4Ver() {
        return RC4_VER;
    }
}
