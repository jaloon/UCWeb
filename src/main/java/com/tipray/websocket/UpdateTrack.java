package com.tipray.websocket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author chenlong
 * @version 1.0 2018-06-12
 */
public class UpdateTrack implements Serializable {

    private long id;
    private float lng;
    private float lat;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public static List<UpdateTrack> random(){
        List<UpdateTrack> list = new ArrayList<>();
        UpdateTrack track;
        for (int i = 0; i < 3000; i++) {
            track = new UpdateTrack();
            track.setId(i + 1);
            track.setLng(nextLng());
            track.setLat(nextLat());
            list.add(track);
        }
        return list;
    }

    /**
     * 生成max到min范围的浮点数
     * */
    public static float nextFloat(final float min, final float max) {
        return min + ((max - min) * new Random().nextFloat());
    }

    public static float nextLng() {
        return 79 + (42 * new Random().nextFloat());
    }
    public static float nextLat() {
        return 24 + (21 * new Random().nextFloat());
    }
}
