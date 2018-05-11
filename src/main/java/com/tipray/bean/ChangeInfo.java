package com.tipray.bean;

import java.io.Serializable;

/**
 * 换站信息
 *
 * @author chenlong
 * @version 1.0 2017-12-25
 */
public class ChangeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 操作员ID
     */
    private Long userId;
    /**
     * 车辆ID
     */
    private Long carId;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 配送单号
     */
    private String invoice;
    /**
     * 仓号
     */
    private Integer storeId;
    /**
     * 油库ID
     */
    private Long oildepotId;
    /**
     * 原加油站ID
     */
    private Long gasstationId;
    /**
     * 变更后加油站ID
     */
    private Long changedGasstationId;
    /**
     * 是否手机操作（0：否，1：是）
     */
    private Integer isApp;
    /**
     * 手机定位经度
     */
    private Float longitude;
    /**
     * 手机定位纬度
     */
    private Float latitude;
    /**
     * 手机定位是否有效
     */
    private Integer isLocationValid;
    /**
     * 原配送ID
     */
    private Long transportId;
    /**
     * 变更后配送ID
     */
    private Long changedTransportId;
    /**
     * 配送状态
     */
    private Integer transportStatus;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Long getOildepotId() {
        return oildepotId;
    }

    public void setOildepotId(Long oildepotId) {
        this.oildepotId = oildepotId;
    }

    public Long getGasstationId() {
        return gasstationId;
    }

    public void setGasstationId(Long gasstationId) {
        this.gasstationId = gasstationId;
    }

    public Long getChangedGasstationId() {
        return changedGasstationId;
    }

    public void setChangedGasstationId(Long changedGasstationId) {
        this.changedGasstationId = changedGasstationId;
    }

    public Integer getIsApp() {
        return isApp;
    }

    public void setIsApp(Integer isApp) {
        this.isApp = isApp;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Integer getIsLocationValid() {
        return isLocationValid;
    }

    public void setIsLocationValid(Integer isLocationValid) {
        this.isLocationValid = isLocationValid;
    }

    public Long getTransportId() {
        return transportId;
    }

    public void setTransportId(Long transportId) {
        this.transportId = transportId;
    }

    public Long getChangedTransportId() {
        return changedTransportId;
    }

    public void setChangedTransportId(Long changedTransportId) {
        this.changedTransportId = changedTransportId;
    }

    public Integer getTransportStatus() {
        return transportStatus;
    }

    public void setTransportStatus(Integer transportStatus) {
        this.transportStatus = transportStatus;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (userId != null) {
            sb.append(", userId=").append(userId);
        }
        if (carId != null) {
            sb.append(", carId=").append(carId);
        }
        if (carNumber != null) {
            sb.append(", carNumber='").append(carNumber).append('\'');
        }
        if (invoice != null) {
            sb.append(", invoice='").append(invoice).append('\'');
        }
        if (storeId != null) {
            sb.append(", storeId=").append(storeId);
        }
        if (carId != null) {
            sb.append(", oildepotId=").append(oildepotId);
        }
        if (gasstationId != null) {
            sb.append(", gasstationId=").append(gasstationId);
        }
        if (changedGasstationId != null) {
            sb.append(", changedGasstationId=").append(changedGasstationId);
        }
        if (isApp != null) {
            sb.append(", isApp=").append(isApp);
        }
        if (longitude != null) {
            sb.append(", longitude=").append(longitude);
        }
        if (latitude != null) {
            sb.append(", latitude=").append(latitude);
        }
        if (isLocationValid != null) {
            sb.append(", isLocationValid=").append(isLocationValid);
        }
        if (transportId != null) {
            sb.append(", transportId=").append(transportId);
        }
        if (changedTransportId != null) {
            sb.append(", changedTransportId=").append(changedTransportId);
        }
        if (transportStatus != null) {
            sb.append(", transportStatus=").append(transportStatus);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
            sb.insert(0, "ChangeInfo{");
            sb.append('}');
        }
        return sb.toString();
    }
}
