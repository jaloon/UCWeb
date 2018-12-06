package com.tipray.bean.lock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

/**
 * 锁动作
 *
 * @author chenlong
 * @version 1.0 2018-12-04
 */
public class LockStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    @JsonIgnore
    private long vehicle_id;
    private int store_id;
    private int seat;
    private int seat_index;
    private int lock_index;
    private int switch_status;
    private List<Integer> alarm;
    @JsonIgnore
    private long switch_time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public int getSeat_index() {
        return seat_index;
    }

    public void setSeat_index(int seat_index) {
        this.seat_index = seat_index;
    }

    public int getLock_index() {
        return lock_index;
    }

    public void setLock_index(int lock_index) {
        this.lock_index = lock_index;
    }

    public int getSwitch_status() {
        return switch_status;
    }

    public void setSwitch_status(int switch_status) {
        this.switch_status = switch_status;
    }

    public List<Integer> getAlarm() {
        return alarm;
    }

    public void setAlarm(List<Integer> alarm) {
        this.alarm = alarm;
    }

    public long getSwitch_time() {
        return switch_time;
    }

    public void setSwitch_time(long switch_time) {
        this.switch_time = switch_time;
    }
}
