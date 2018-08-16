package com.tipray.websocket.protocol;

/**
 * WebSocket业务ID
 *
 * @author chenlong
 * @version 1.0 2018-08-10
 */
public class WebSocketBizId {
    /**
     * WebSocket关闭
     */
    public static final int WEB_SOCKET_CLOSE = 0;
    /**
     * WebSocket开启
     */
    public static final int WEB_SOCKET_OPEN = 1;

    /**
     * 消除报警
     */
    public static final int ELIMINATE_ALARM = 100;
    /**
     * 消除报警回复
     */
    public static final int ELIMINATE_ALARM_REPLY = 101;
    /**
     * 新报警
     */
    public static final int NEW_ALARM = 110;
    /**
     * 缓存报警
     */
    public static final int CACHE_ALARM = 111;

}
