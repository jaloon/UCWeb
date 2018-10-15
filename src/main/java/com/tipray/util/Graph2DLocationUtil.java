package com.tipray.util;

import com.tipray.bean.Point;

import java.util.List;

/**
 * 平面图形位置关系工具类
 *
 * @author chenlong
 * @version 1.0 2018-09-17
 */
public class Graph2DLocationUtil {
    /**
     * 原理: 将测试点的Y坐标与多边形的每一个点进行比较， 会得到测试点所在的行与多边形边的所有交点。 如果测试点的两边点的个数都是奇数个，
     * 则该测试点在多边形内，否则在多边形外。
     * <p>
     * 函数功能: 判断点(x, y)是否在有ploy_sides个顶点的多边形内
     *
     * @param polySides 测试多边形的顶点数
     * @param polyX     测试多边形的各个顶点的X轴坐标
     * @param polyY     测试多边形的各个顶点的Y轴坐标
     * @param x         测试点的X轴坐标
     * @param y         测试点的Y轴坐标
     * @return 返回false, 表示不在多边形内部;返回true,表示在多边形内部. 说明: 在多边形各边上的点默认不在多边形内部
     */
    private static boolean isPointInPolygon(int polySides, double[] polyX, double[] polyY, double x, double y) {
        int i, j = polySides - 1;
        boolean res = false;
        for (i = 0; i < polySides; i++) {
            if ((polyY[i] < y && polyY[j] >= y || polyY[j] < y && polyY[i] >= y) && (polyX[i] <= x || polyX[j] <= x)) {
                res ^= ((polyX[i] + (y - polyY[i]) / (polyY[j] - polyY[i]) * (polyX[j] - polyX[i])) < x);
            }
            j = i;
        }
        return res;
    }

    /**
     * 判断点是否在多边形内
     *
     * @param polygonPoints 测试多边形的各个顶点
     * @param point         测试点
     * @return 返回false, 表示不在多边形内部;返回true,表示在多边形内部. 说明: 在多边形各边上的点默认不在多边形内部
     */

    public static boolean isPointInPolygon(Point[] polygonPoints, Point point) {

        int poly_sides = polygonPoints.length;
        double[] polyX = new double[poly_sides];
        double[] polyY = new double[poly_sides];
        for (int i = 0; i < poly_sides; i++) {
            polyX[i] = polygonPoints[i].getLongitude();
            polyY[i] = polygonPoints[i].getLatitude();
        }
        return isPointInPolygon(poly_sides, polyX, polyY, point.getLongitude(), point.getLatitude());
    }

    /**
     * 判断点是否在多边形内
     *
     * @param polygonPoints 测试多边形的各个顶点
     * @param point         测试点
     * @return 返回false, 表示不在多边形内部;返回true,表示在多边形内部. 说明: 在多边形各边上的点默认不在多边形内部
     */
    public static boolean isPointInPolygon(List<Point> polygonPoints, Point point) {
        int poly_sides = polygonPoints.size();
        double[] polyX = new double[poly_sides];
        double[] polyY = new double[poly_sides];
        for (int i = 0; i < poly_sides; i++) {
            polyX[i] = polygonPoints.get(i).getLongitude();
            polyY[i] = polygonPoints.get(i).getLatitude();
        }
        return isPointInPolygon(poly_sides, polyX, polyY, point.getLongitude(), point.getLatitude());
    }
}
