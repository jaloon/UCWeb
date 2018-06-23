package com.tipray.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

/**
 * 站点信息
 *
 * @author chenlong
 * @version 1.0 2018-06-14
 */
public class Station implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String name;
    private float longitude;
    private float latitude;
    @JsonIgnore
    private byte[] cover;
    private List<Point> cover_lonlatlist_region;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public List<Point> getCover_lonlatlist_region() {
        return cover_lonlatlist_region;
    }

    public void setCover_lonlatlist_region(List<Point> cover_lonlatlist_region) {
        this.cover_lonlatlist_region = cover_lonlatlist_region;
    }
}
