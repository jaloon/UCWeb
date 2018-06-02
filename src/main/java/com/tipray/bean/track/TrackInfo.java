package com.tipray.bean.track;

import java.io.Serializable;
import java.util.Date;

/**
 * 轨迹表信息
 *
 * @author chenlong
 * @version 1.0 2018-05-31
 */
public class TrackInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 轨迹ID
     */
    private Long id;
    /**
     * 车辆ID
     */
    private Long carId;
    /**
     * 坐标是否有效
     */
    private Boolean coorValid;
    /**
     * 经度
     */
    private Float longitude;
    /**
     * 纬度
     */
    private Float latitude;
    /**
     * 车辆状态
     */
    private Integer carStatus;
    /**
     * 车台报警状态
     */
    private Integer terminalAlarm;
    /**
     * 角度
     */
    private Integer angle;
    /**
     * 速度
     */
    private Integer speed;
    /**
     * 锁状态信息
     */
    private byte[] lockStatusInfo;
    /**
     * 轨迹时间
     */
    private Date trackTime;
    /**
     * 轨迹生成时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Boolean getCoorValid() {
        return coorValid;
    }

    public void setCoorValid(Boolean coorValid) {
        this.coorValid = coorValid;
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

    public Integer getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(Integer carStatus) {
        this.carStatus = carStatus;
    }

    public Integer getTerminalAlarm() {
        return terminalAlarm;
    }

    public void setTerminalAlarm(Integer terminalAlarm) {
        this.terminalAlarm = terminalAlarm;
    }

    public Integer getAngle() {
        return angle;
    }

    public void setAngle(Integer angle) {
        this.angle = angle;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public byte[] getLockStatusInfo() {
        return lockStatusInfo;
    }

    public void setLockStatusInfo(byte[] lockStatusInfo) {
        this.lockStatusInfo = lockStatusInfo;
    }

    public Date getTrackTime() {
        return trackTime;
    }

    public void setTrackTime(Date trackTime) {
        this.trackTime = trackTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TrackInfo{");
        sb.append("id=").append(id);
        sb.append(", carId=").append(carId);
        sb.append(", coorValid=").append(coorValid);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", carStatus=").append(carStatus);
        sb.append(", terminalAlarm=").append(terminalAlarm);
        sb.append(", angle=").append(angle);
        sb.append(", speed=").append(speed);
        if (lockStatusInfo != null && lockStatusInfo.length > 0) {
            sb.append(", lockStatusInfo=");
            sb.append('[');
            for (int i = 0, len = lockStatusInfo.length; i < len; ++i)
                sb.append(i == 0 ? "" : ", ").append(lockStatusInfo[i]);
            sb.append(']');
        }
        sb.append(", trackTime=").append(trackTime);
        sb.append(", createTime=").append(createTime);
        sb.append('}');
        return sb.toString();
    }
}
