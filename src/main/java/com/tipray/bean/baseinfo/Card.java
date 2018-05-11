/*
 * Device.java
 * Copyright(C) 厦门天锐科技有限公司
 * All rights reserved.
 * -----------------------------------------------
 * 2017-11-06 Created
 */
package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

import java.util.List;

/**
 * 卡信息表
 *
 * @author chenlong
 * @version 1.0 2017-11-06
 */
public class Card extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 卡ID
     */
    private Long cardId;
    /**
     * 卡类型
     */
    private Integer type;
    /**
     * 卡类型名称
     */
    private String typeName;
    /**
     * 持有人
     */
    private String director;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 身份证号
     */
    private String identityCard;
    /**
     * 油库
     */
    private List<OilDepot> oilDepots;
    /**
     * 加油站
     */
    private List<GasStation> gasStations;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public List<OilDepot> getOilDepots() {
        return oilDepots;
    }

    public void setOilDepots(List<OilDepot> oilDepots) {
        this.oilDepots = oilDepots;
    }

    public List<GasStation> getGasStations() {
        return gasStations;
    }

    public void setGasStations(List<GasStation> gasStations) {
        this.gasStations = gasStations;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (cardId != null) {
            sb.append(", cardId=").append(cardId);
        }
        if (type != null) {
            sb.append(", type=").append(type);
        }
        if (typeName != null) {
            sb.append(", typeName='").append(typeName).append('\'');
        }
        if (director != null) {
            sb.append(", director='").append(director).append('\'');
        }
        if (phone != null) {
            sb.append(", phone='").append(phone).append('\'');
        }
        if (identityCard != null) {
            sb.append(", identityCard='").append(identityCard).append('\'');
        }
        if (oilDepots != null) {
            sb.append(", oilDepots=").append(oilDepots);
        }
        if (gasStations != null) {
            sb.append(", gasStations=").append(gasStations);
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0, 2, "Card{");
            sb.append('}');
        }
        return sb.toString();
    }
}