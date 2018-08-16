package com.tipray.websocket.protocol;

import java.io.Serializable;

/**
 * WebSocket通讯业务协议封装
 *
 * @author chenlong
 * @version 1.0 2018-08-10
 */
public class WebSocketProtocol implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 业务ID
     */
    private int biz;
    /**
     * 业务信息
     */
    private Object msg;

    public WebSocketProtocol() {
    }

    public WebSocketProtocol(int biz) {
        this.biz = biz;
    }

    public WebSocketProtocol(int biz, Object msg) {
        this.biz = biz;
        this.msg = msg;
    }

    public int getBiz() {
        return biz;
    }

    public void setBiz(int biz) {
        this.biz = biz;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
