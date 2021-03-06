package com.tipray.util;

/**
 * 坐标转换工具类
 * <p>
 * 坐标互转：WGS-84(GPS)、GCJ-02(Google地图)、BD-09(百度地图)<br>
 * WGS-84：是国际标准，GPS坐标（Google Earth使用、或者GPS模块）<br>
 * GCJ-02：中国坐标偏移标准，Google Map、高德、腾讯使用<br>
 * BD-09：百度坐标偏移标准，Baidu Map使用
 * </p>
 *
 * @author chenlong
 * @version 1.0 2018-09-17
 */
public class CoordinateTransformUtil {
    /**
     * 圆周率
     */
    private static double pi = 3.14159265358979324;
    /**
     * 卫星椭球坐标投影到平面地图坐标系的投影因子
     */
    private static double a = 6378245.0;
    /**
     * 椭球的偏心率
     */
    private static double ee = 0.00669342162296594323;
    /**
     * 圆周率转换量
     */
    private final static double x_pi = pi * 3000.0 / 180.0;

    /**
     * GPS坐标转百度坐标
     *
     * @param lat GPS经度
     * @param lon GPS纬度
     * @return 百度坐标
     */
    public static double[] wgs2bd(double lat, double lon) {
        double[] wgs2gcj = wgs2gcj(lat, lon);
        double[] gcj2bd = gcj2bd(wgs2gcj[0], wgs2gcj[1]);
        return gcj2bd;
    }

    /**
     * 谷歌、高德转百度
     *
     * @param lat 经度
     * @param lon 纬度
     * @return 百度坐标
     */
    public static double[] gcj2bd(double lat, double lon) {
        double x = lon, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd_lat, bd_lon};
    }

    /**
     * 百度转谷歌、高德
     *
     * @param lat 经度
     * @param lon 纬度
     * @return 谷歌、高德坐标
     */
    public static double[] bd2gcj(double lat, double lon) {
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[]{gg_lat, gg_lon};
    }

    /**
     * @param lat
     * @param lon
     * @return
     */
    public static double[] wgs2gcj(double lat, double lon) {
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        double[] loc = {mgLat, mgLon};
        return loc;
    }

    private static double transformLat(double lat, double lon) {
        double ret = -100.0 + 2.0 * lat + 3.0 * lon + 0.2 * lon * lon + 0.1 * lat * lon + 0.2 * Math.sqrt(Math.abs(lat));
        ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lon * pi) + 40.0 * Math.sin(lon / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lon / 12.0 * pi) + 320 * Math.sin(lon * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double lat, double lon) {
        double ret = 300.0 + lat + 2.0 * lon + 0.1 * lat * lat + 0.1 * lat * lon + 0.1 * Math.sqrt(Math.abs(lat));
        ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lat / 12.0 * pi) + 300.0 * Math.sin(lat / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }
}
