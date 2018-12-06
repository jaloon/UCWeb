package com.tipray.bean.lock;

import java.io.Serializable;

/**
 * app接口锁信息
 *
 * @author chenlong
 * @version 1.0 2018-12-06
 */
public class LockForApp implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long vehicle_id;
    private Long lock_id;
    private Integer store_id;
    private Integer seat;
    private Integer seat_index;
    private Integer lock_device_id;
    private Integer is_allowed_open;
    private Integer bind_status;
    private Integer is_has_bind;
    private Integer lock_index;
    private Integer switch_status;
    private String device_remark;
    private String remark;

    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public Long getLock_id() {
        return lock_id;
    }

    public void setLock_id(Long lock_id) {
        this.lock_id = lock_id;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public Integer getSeat() {
        return seat;
    }

    public void setSeat(Integer seat) {
        this.seat = seat;
    }

    public Integer getSeat_index() {
        return seat_index;
    }

    public void setSeat_index(Integer seat_index) {
        this.seat_index = seat_index;
    }

    public Integer getLock_device_id() {
        return lock_device_id;
    }

    public void setLock_device_id(Integer lock_device_id) {
        this.lock_device_id = lock_device_id;
    }

    public Integer getIs_allowed_open() {
        return is_allowed_open;
    }

    public void setIs_allowed_open(Integer is_allowed_open) {
        this.is_allowed_open = is_allowed_open;
    }

    public Integer getBind_status() {
        return bind_status;
    }

    public void setBind_status(Integer bind_status) {
        this.bind_status = bind_status;
    }

    public Integer getIs_has_bind() {
        return is_has_bind;
    }

    public void setIs_has_bind(Integer is_has_bind) {
        this.is_has_bind = is_has_bind;
    }

    public Integer getLock_index() {
        return lock_index;
    }

    public void setLock_index(Integer lock_index) {
        this.lock_index = lock_index;
    }

    public Integer getSwitch_status() {
        return switch_status;
    }

    public void setSwitch_status(Integer switch_status) {
        this.switch_status = switch_status;
    }

    public String getDevice_remark() {
        return device_remark;
    }

    public void setDevice_remark(String device_remark) {
        this.device_remark = device_remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
