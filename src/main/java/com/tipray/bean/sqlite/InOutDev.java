package com.tipray.bean.sqlite;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * 出入库读卡器
 *
 * @author chenlong
 * @version 1.0 2018-08-22
 */
public class InOutDev extends BaseSqlite implements Serializable {
    private static final long serialVersionUID = 1L;
    private int devId;
    private int type;
    private long stationId;

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof InOutDev) {
            InOutDev inOutDev = (InOutDev) o;
            return new EqualsBuilder()
                    .append(devId, inOutDev.devId)
                    .append(type, inOutDev.type)
                    .append(stationId, inOutDev.stationId)
                    .isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(devId)
                .append(type)
                .append(stationId)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InOutDev{");
        sb.append("devId=").append(devId);
        sb.append(", type=").append(type);
        sb.append(", stationId=").append(stationId);
        sb.append('}');
        return sb.toString();
    }
}
