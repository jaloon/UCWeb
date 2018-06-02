package com.tipray.bean.track;

/**
 * 最新轨迹
 *
 * @author chenlong
 * @version 1.0 2018-06-01
 */
public class LastTrack extends TrackInfo {
    private static final long serialVersionUID = 1L;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 运输公司ID
     */
    private Long comId;
    /**
     * 运输公司名称
     */
    private String carCom;

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Long getComId() {
        return comId;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public String getCarCom() {
        return carCom;
    }

    public void setCarCom(String carCom) {
        this.carCom = carCom;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LastTrack{");
        sb.append("id=").append(getId());
        sb.append(", carId=").append(getCarId());
        sb.append("carNumber='").append(carNumber).append('\'');
        sb.append(", carCom='").append(carCom).append('\'');
        sb.append(", coorValid=").append(getCoorValid());
        sb.append(", longitude=").append(getLongitude());
        sb.append(", latitude=").append(getLatitude());
        sb.append(", carStatus=").append(getCarStatus());
        sb.append(", terminalAlarm=").append(getTerminalAlarm());
        sb.append(", angle=").append(getAngle());
        sb.append(", speed=").append(getSpeed());
        byte[] lockStatusInfo = getLockStatusInfo();
        if (lockStatusInfo != null && lockStatusInfo.length > 0) {
            sb.append(", lockStatusInfo=");
            sb.append('[');
            for (int i = 0, len = lockStatusInfo.length; i < len; ++i)
                sb.append(i == 0 ? "" : ", ").append(lockStatusInfo[i]);
            sb.append(']');
        }
        sb.append(", trackTime=").append(getTrackTime());
        sb.append(", createTime=").append(getCreateTime());
        sb.append('}');
        return sb.toString();
    }
}
