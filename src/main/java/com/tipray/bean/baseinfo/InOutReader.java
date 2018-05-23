package com.tipray.bean.baseinfo;

import com.tipray.core.base.BaseBean;

/**
 * 出入库读卡器
 *
 * @author chenlong
 */
public class InOutReader extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 读卡器设备ID
     */
    private Integer devId;
    /**
     * 读卡器类型（0：未指定 | 1：入库读卡器 | 2：出库读卡器 | 3： 出入库读卡器）
     */
    private Integer type;
    /**
     * 读卡器类型名称
     */
    private String typeName;
    /**
     * 是否用于道闸转发通知（0：未指定 | 1：入库道闸读卡器 | 2：出库道闸读卡器 | 3： 出入库道闸读卡器）
     */
    private Integer barrier;
    /**
     * 道闸通知
     */
    private String barrierName;
    /**
     * 设备型号
     */
    private String model;
    /**
     * 所属油库ID
     */
    private Long oilDepotId;

    public Integer getDevId() {
        return devId;
    }

    public void setDevId(Integer devId) {
        this.devId = devId;
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

    public Integer getBarrier() {
        return barrier;
    }

    public void setBarrier(Integer barrier) {
        this.barrier = barrier;
    }

    public String getBarrierName() {
        return barrierName;
    }

    public void setBarrierName(String barrierName) {
        this.barrierName = barrierName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getOilDepotId() {
        return oilDepotId;
    }

    public void setOilDepotId(Long oilDepotId) {
        this.oilDepotId = oilDepotId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (devId != null) {
            sb.append(", devId=").append(devId);
        }
        if (type != null) {
            sb.append(", type=").append(type);
        }
        if (typeName != null) {
            sb.append(", typeName='").append(typeName).append('\'');
        }
        if (barrier != null) {
            sb.append(", barrier=").append(barrier);
        }
        if (barrierName != null) {
            sb.append(", barrierName='").append(barrierName).append('\'');
        }
        if (model != null) {
            sb.append(", model='").append(model).append('\'');
        }
        if (oilDepotId != null) {
            sb.append(", oilDepotId=").append(oilDepotId);
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0, 2, "InOutReader{");
            sb.append('}');
        }
        return sb.toString();
    }
}
