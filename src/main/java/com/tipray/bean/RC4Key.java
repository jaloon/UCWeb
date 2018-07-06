package com.tipray.bean;

import java.io.Serializable;

/**
 * RC4密钥信息
 *
 * @author chenlong
 * @version 1.0 2018-07-05
 */
public class RC4Key implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 密钥
     */
    private byte[] key;
    /**
     * 密钥版本
     */
    private byte ver;

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte getVer() {
        return ver;
    }

    public void setVer(byte ver) {
        this.ver = ver;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RC4Key{");
        sb.append("key=");
        if (key == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < key.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(key[i]);
            sb.append(']');
        }
        sb.append(", ver=").append(ver);
        sb.append('}');
        return sb.toString();
    }
}
