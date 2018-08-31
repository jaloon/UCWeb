package com.tipray.bean.sqlite;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * 油库
 *
 * @author chenlong
 * @version 1.0 2018-08-22
 */
public class OildepotInfo extends BaseSqlite implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String name;
    private float longitude;
    private float latitude;
    private int radius;
    private byte[] cover;

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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof OildepotInfo) {
            OildepotInfo oildepotInfo = (OildepotInfo) o;
            return new EqualsBuilder()
                    .append(id, oildepotInfo.id)
                    .append(longitude, oildepotInfo.longitude)
                    .append(latitude, oildepotInfo.latitude)
                    .append(radius, oildepotInfo.radius)
                    .append(name, oildepotInfo.name)
                    .append(cover, oildepotInfo.cover)
                    .isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(name)
                .append(longitude)
                .append(latitude)
                .append(radius)
                .append(cover)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OildepotInfo{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", radius=").append(radius);
        sb.append(", cover=");
        if (cover == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < cover.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(cover[i]);
            sb.append(']');
        }
        sb.append('}');
        return sb.toString();
    }
}
