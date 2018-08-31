package com.tipray.bean.sqlite;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * 出入库卡
 *
 * @author chenlong
 * @version 1.0 2018-08-22
 */
public class InOutCard extends BaseSqlite implements Serializable {
    private static final long serialVersionUID = 1L;
    private long cardId;
    private int type;
    private long stationId;
    private String ownerId;

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof InOutCard) {
            InOutCard inOutCard = (InOutCard) o;
            return new EqualsBuilder()
                    .append(cardId, inOutCard.cardId)
                    .append(type, inOutCard.type)
                    .append(stationId, inOutCard.stationId)
                    // .append(ownerId, inOutCard.ownerId) 暂不考虑ownerId
                    .isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(cardId)
                .append(type)
                .append(stationId)
                // .append(ownerId) 暂不考虑ownerId
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InOutCard{");
        sb.append("cardId=").append(cardId);
        sb.append(", type=").append(type);
        sb.append(", stationId=").append(stationId);
        // sb.append(", ownerId='").append(ownerId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
