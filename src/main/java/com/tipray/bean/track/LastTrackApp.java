package com.tipray.bean.track;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * App最新轨迹
 *
 * @author chenlong
 * @version 1.0 2018-12-04
 */
public class LastTrackApp implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private long vehicle_id;
    private long track_id;
    private int is_lnglat_valid;
    private float longitude;
    private float latitude;
    private int vehicle_status;
    private int vehicle_alarm_status;
    private int angle;
    private int speed;
    @JsonIgnore
    private byte[] lock_status_info;
    private List<LockStatus> locks;
    private Date track_time;
    private long last_valid_track_id;
    private int last_is_lnglat_valid;
    private float last_valid_longitude;
    private float last_valid_latitude;
    private int last_valid_angle;
    private int last_valid_speed;
    private Date last_valid_track_time;

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

    public long getTrack_id() {
        return track_id;
    }

    public void setTrack_id(long track_id) {
        this.track_id = track_id;
    }

    public int getIs_lnglat_valid() {
        return is_lnglat_valid;
    }

    public void setIs_lnglat_valid(int is_lnglat_valid) {
        this.is_lnglat_valid = is_lnglat_valid;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public int getVehicle_status() {
        return vehicle_status;
    }

    public void setVehicle_status(int vehicle_status) {
        this.vehicle_status = vehicle_status;
    }

    public int getVehicle_alarm_status() {
        return vehicle_alarm_status;
    }

    public void setVehicle_alarm_status(int vehicle_alarm_status) {
        this.vehicle_alarm_status = vehicle_alarm_status;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public byte[] getLock_status_info() {
        return lock_status_info;
    }

    public void setLock_status_info(byte[] lock_status_info) {
        this.lock_status_info = lock_status_info;
    }

    public List<LockStatus> getLocks() {
        return locks;
    }

    public void setLocks(List<LockStatus> locks) {
        this.locks = locks;
    }

    public Date getTrack_time() {
        return track_time;
    }

    public void setTrack_time(Date track_time) {
        this.track_time = track_time;
    }

    public long getLast_valid_track_id() {
        return last_valid_track_id;
    }

    public void setLast_valid_track_id(long last_valid_track_id) {
        this.last_valid_track_id = last_valid_track_id;
    }

    public int getLast_is_lnglat_valid() {
        return last_is_lnglat_valid;
    }

    public void setLast_is_lnglat_valid(int last_is_lnglat_valid) {
        this.last_is_lnglat_valid = last_is_lnglat_valid;
    }

    public float getLast_valid_longitude() {
        return last_valid_longitude;
    }

    public void setLast_valid_longitude(float last_valid_longitude) {
        this.last_valid_longitude = last_valid_longitude;
    }

    public float getLast_valid_latitude() {
        return last_valid_latitude;
    }

    public void setLast_valid_latitude(float last_valid_latitude) {
        this.last_valid_latitude = last_valid_latitude;
    }

    public int getLast_valid_angle() {
        return last_valid_angle;
    }

    public void setLast_valid_angle(int last_valid_angle) {
        this.last_valid_angle = last_valid_angle;
    }

    public int getLast_valid_speed() {
        return last_valid_speed;
    }

    public void setLast_valid_speed(int last_valid_speed) {
        this.last_valid_speed = last_valid_speed;
    }

    public Date getLast_valid_track_time() {
        return last_valid_track_time;
    }

    public void setLast_valid_track_time(Date last_valid_track_time) {
        this.last_valid_track_time = last_valid_track_time;
    }
}
