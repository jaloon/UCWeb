package com.tipray.mq;

import java.io.Serializable;

/**
 * 轨迹队列元素
 *
 * @author chenlong
 * @version 1.0 2018-05-29
 */
public class TrackQueueElement implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 车台设备ID */
    private int terminalId;
    /** 接收到的轨迹数据 */
    private byte[] receiveBuf;

    public int getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }

    public byte[] getReceiveBuf() {
        return receiveBuf;
    }

    public void setReceiveBuf(byte[] receiveBuf) {
        this.receiveBuf = receiveBuf;
    }

    public TrackQueueElement() {
        super();
    }

    public TrackQueueElement(int terminalId, byte[] receiveBuf) {
        super();
        this.terminalId = terminalId;
        this.receiveBuf = receiveBuf;
    }
}
