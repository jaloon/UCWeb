package com.tipray.util;

import com.tipray.bean.Point;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 占地范围坐标集合转换工具类
 *
 * @author chenlong
 * @version 1.0 2017年11月29日
 */
public class CoverRegionUtil {
    /**
     * 浮点数值字节数组转为坐标集合字符串
     *
     * @param cover {@link Byte[]} 占地范围 （经纬度浮点值字节数组）
     * @return {@link String} 占地范围 （经纬度集合字符串，用逗号,相隔 ）
     * @throws IllegalArgumentException
     */
    public static String coverToRegion(Byte[] cover) {
        int length = cover.length;
        byte[] coverB = new byte[length];
        for (int i = 0; i < length; i++) {
            coverB[i] = cover[i];
        }
        return coverToRegion(coverB);
    }

    /**
     * 浮点数值字节数组转为坐标集合字符串
     *
     * @param cover {@link byte[]} 占地范围 （经纬度浮点值字节数组）
     * @return {@link String} 占地范围 （经纬度集合字符串，用逗号,相隔 ）
     * @throws IllegalArgumentException
     */
    public static String coverToRegion(byte[] cover) {
        StringBuffer regionBuf = new StringBuffer();
        if (cover != null) {
            int length = cover.length;
            if (isOdd(length) || isOdd(length >> 2)) {
                throw new IllegalArgumentException("字节长度或浮点数值个数为奇数，无法准确表示点");
            } else {
                for (int i = 0; i < length; i += 4) {
                    byte[] xBytes = Arrays.copyOfRange(cover, i, i + 4);
                    float x = BytesConverterByLittleEndian.getFloat(xBytes);
                    i += 4;
                    byte[] yBytes = Arrays.copyOfRange(cover, i, i + 4);
                    float y = BytesConverterByLittleEndian.getFloat(yBytes);
                    regionBuf.append('(').append(x).append(", ").append(y).append("), ");
                }
                int len = regionBuf.length();
                regionBuf.delete(len - 2, len);
            }
        }
        return regionBuf.toString();
    }

    /**
     * 浮点数值字节数组转为坐标点集合
     *
     * @param cover {@link byte[]} 占地范围 （经纬度浮点值字节数组）
     * @return {@link Point} 坐标点集合
     */
    public static List<Point> coverToPoints(byte[] cover) {
        if (cover == null) {
            return null;
        }
        int length = cover.length;
        if (length == 0) {
            return null;
        }
        if (isOdd(length) || isOdd(length >> 2)) {
            throw new IllegalArgumentException("字节长度或浮点数值个数为奇数，无法准确表示点");
        } else {
            List<Point> points = new ArrayList<>();
            for (int i = 0; i < length; i += 4) {
                byte[] xBytes = Arrays.copyOfRange(cover, i, i + 4);
                float x = BytesConverterByLittleEndian.getFloat(xBytes);
                i += 4;
                byte[] yBytes = Arrays.copyOfRange(cover, i, i + 4);
                float y = BytesConverterByLittleEndian.getFloat(yBytes);
                points.add(new Point(x, y));
            }
            return points;
        }
    }

    /**
     * 坐标集合字符串转为浮点数值字节数组
     *
     * @param region {@link String} 占地范围 （经纬度集合字符串，用逗号,相隔 ）
     * @return {@link Byte[]} 占地范围 （经纬度浮点值字节数组）
     */
    public static Byte[] regionToCoverBytes(String region) {
        if (StringUtil.isNotEmpty(region)) {
            // 替换中文符号
            region = region.replace("（", "(");
            region = region.replace("）", ")");
            region = region.replace("，", ",");
            // 替换大部分空白字符， 不限于空格 \s 可以匹配空格、制表符、换页符等空白字符的其中任意一个
            region = region.replaceAll("\\s*", "");
            if (region.endsWith(",")) {
                // 去除末尾多余逗号
                region = region.substring(0, region.length() - 1);
            }
            // 去除首尾括号
            region = region.substring(1, region.length() - 1);

            String[] points = region.split("\\)\\,\\(");
            List<Byte> coverList = new ArrayList<Byte>();
            for (String pointStr : points) {
                String[] point = pointStr.split(",");
                for (String str : point) {
                    float f = Float.parseFloat(str);
                    byte[] bytes = BytesConverterByLittleEndian.getBytes(f);
                    for (byte b : bytes) {
                        coverList.add(b);
                    }
                }
            }
            int len = coverList.size();
            Byte[] cover = coverList.toArray(new Byte[len]);
            return cover;
        }
        return null;
    }

    /**
     * 坐标集合字符串转为浮点数值字节数组
     *
     * @param region {@link String} 占地范围 （经纬度集合字符串，用逗号,相隔 ）
     * @return {@link byte[]} 占地范围 （经纬度浮点值字节数组）
     */
    public static byte[] regionToCover(String region) {
        if (StringUtil.isNotEmpty(region)) {
            // 替换中文符号
            region = region.replace("（", "(");
            region = region.replace("）", ")");
            region = region.replace("，", ",");
            // 替换大部分空白字符， 不限于空格 \s 可以匹配空格、制表符、换页符等空白字符的其中任意一个
            region = region.replaceAll("\\s*", "");
            if (region.endsWith(",")) {
                // 去除末尾多余逗号
                region = region.substring(0, region.length() - 1);
            }
            // 去除首尾括号
            region = region.substring(1, region.length() - 1);

            String[] points = region.split("\\)\\,\\(");
            ByteBuffer buff = ByteBuffer.allocate(points.length * 8);
            for (String pointStr : points) {
                String[] point = pointStr.split(",");
                for (String coord : point) {
                    float f = Float.parseFloat(coord);
                    byte[] bytes = BytesConverterByLittleEndian.getBytes(f);
                    buff = buff.put(bytes);
                }
            }
            byte[] cover = buff.array();
            return cover;
        }
        return null;
    }

    /**
     * 奇数判断
     *
     * @param a int数值
     * @return 奇数为真，偶数为假
     */
    public static boolean isOdd(int a) {
        return (a & 1) == 1;
    }

}
