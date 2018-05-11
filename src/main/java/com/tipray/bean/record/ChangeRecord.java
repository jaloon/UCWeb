package com.tipray.bean.record;

import com.tipray.bean.Record;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.bean.baseinfo.User;

/**
 * 远程换站记录
 *
 * @author chenlong
 * @version 1.0 2017-12-18
 */
public class ChangeRecord extends Record {
    private static final long serialVersionUID = 1L;
    /**
     * 操作员
     */
    private User user;
    /**
     * 配送单号
     */
    private String invoice;
    /**
     * 油库
     */
    private OilDepot oilDepot;
    /**
     * 仓号
     */
    private String storeId;
    /**
     * 原加油站
     */
    private GasStation gasStation;
    /**
     * 换站加油站
     */
    private GasStation changedStation;
    /**
     * 换站状态
     */
    private Integer status;
    /**
     * 换站状态名称
     */
    private String statusName;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public OilDepot getOilDepot() {
        return oilDepot;
    }

    public void setOilDepot(OilDepot oilDepot) {
        this.oilDepot = oilDepot;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public GasStation getGasStation() {
        return gasStation;
    }

    public void setGasStation(GasStation gasStation) {
        this.gasStation = gasStation;
    }

    public GasStation getChangedStation() {
        return changedStation;
    }

    public void setChangedStation(GasStation changedStation) {
        this.changedStation = changedStation;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (getCarNumber() != null) {
            sb.append(", carNumber='").append(getCarNumber()).append('\'');
        }
        if (user != null) {
            sb.append(", user=").append(user);
        }
        if (invoice != null) {
            sb.append(", invoice='").append(invoice).append('\'');
        }
        if (oilDepot != null) {
            sb.append(", oilDepot=").append(oilDepot);
        }
        if (storeId != null) {
            sb.append(", storeId='").append(storeId).append('\'');
        }
        if (gasStation != null) {
            sb.append(", gasStation=").append(gasStation);
        }
        if (changedStation != null) {
            sb.append(", changedStation=").append(changedStation);
        }
        if (status != null) {
            sb.append(", status=").append(status);
        }
        if (statusName != null) {
            sb.append(", statusName='").append(statusName).append('\'');
        }
        if (getLongitude() != null) {
            sb.append(", longitude=").append(getLongitude());
        }
        if (getLatitude() != null) {
            sb.append(", latitude=").append(getLatitude());
        }
        if (getStation() != null) {
            sb.append(", station='").append(getStation()).append('\'');
        }
        if (getVelocity() != null) {
            sb.append(", velocity=").append(getVelocity());
        }
        if (getAngle() != null) {
            sb.append(", angle=").append(getAngle());
        }
        if (getTerminalAlarmStatus() != null) {
            sb.append(", terminalAlarmStatus=").append(getTerminalAlarmStatus());
        }
        if (getLockStatusInfo() != null) {
            sb.append(", lockStatusInfo=");
            sb.append('[');
            for (int i = 0, len = getLockStatusInfo().length; i < len; ++i)
                sb.append(i == 0 ? "" : ", ").append(getLockStatusInfo()[i]);
            sb.append(']');
        }
        if (getIsApp() != null) {
            sb.append(", isApp=").append(getIsApp());
        }
        if (getBegin() != null) {
            sb.append(", begin='").append(getBegin()).append('\'');
        }
        if (getEnd() != null) {
            sb.append(", end='").append(getEnd()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0, 2, "ChangeRecord{");
            sb.append('}');
        }
        return sb.toString();
    }
}
