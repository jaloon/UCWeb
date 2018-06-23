package com.tipray.bean;

import java.io.Serializable;

/**
 * 坐标点
 *
 * @author chenlong
 * @version 1.0 2018-06-14
 */
public class Point implements Serializable {
    private static final long serialVersionUID = 1L;
    private float longitude;
    private float latitude;

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

    public Point() {
    }

    public Point(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
