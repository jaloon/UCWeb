package com.tipray.bean.sqlite;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * 管理卡
 *
 * @author chenlong
 * @version 1.0 2018-08-22
 */
public class ManageCard extends BaseSqlite implements Serializable {
    private static final long serialVersionUID = 1L;
    private long cardId;
    private String ownerName;

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof ManageCard) {
            ManageCard manageCard = (ManageCard) o;
            return new EqualsBuilder()
                    .append(cardId, manageCard.cardId)
                    .append(ownerName, manageCard.ownerName)
                    .isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(cardId)
                .append(ownerName)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ManageCard{");
        sb.append("cardId=").append(cardId);
        sb.append(", ownerName='").append(ownerName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
