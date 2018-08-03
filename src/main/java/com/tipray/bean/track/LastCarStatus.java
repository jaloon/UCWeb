package com.tipray.bean.track;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆最新状态
 *
 * @author chenlong
 * @version 1.0 2018-07-27
 */
public class LastCarStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private int status;
    private Date time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
