package com.tipray.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 版本号工具类
 *
 * @author chenlong
 * @version 1.0 2018-07-17
 */
public class VersionUtil {
    /**
     * 版本号验证正则
     */
    public static final String VERSION_REGEX = "^\\d+\\.\\d+\\.\\d+$";
    /**
     * 版本号验证正则的编译表示
     */
    public static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEX);
    /**
     * 版本号分隔符
     */
    public static final String VERSION_SEPARATOR = "\\.";

    /**
     * 是否版本号字符串
     *
     * @param version 版本号字符串
     * @return 是否
     */
    public static boolean isVerSion(String version) {
        if (version == null || version.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = VERSION_PATTERN.matcher(version);
        return matcher.matches();
    }

    /**
     * 比较两个版本号
     *
     * @param ver1 版本号1
     * @param ver2 版本号2
     * @return 0: ver1 = ver2; 1: ver1 > ver2; -1: ver1 < ver2;
     */
    public static int compareVer(String ver1, String ver2) {
        if (ver1 == null || ver2 == null) {
            throw new IllegalArgumentException("版本号为null！");
        }
        if (ver1.equals(ver2)) {
            return 0;
        }
        String[] ver1_arr = ver1.split(VERSION_SEPARATOR);
        String[] ver2_arr = ver2.split(VERSION_SEPARATOR);
        for (int i = 0; i < 3; i++) {
            int v1 = Integer.parseInt(ver1_arr[i], 10);
            int v2 = Integer.parseInt(ver2_arr[i], 10);
            int compare = v1 - v2;
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }

    /**
     * 将版本号字符串解析为整形数版本号
     *
     * @param verStr 版本号字符串（格式：1.2.3456）
     * @return 整形数版本号
     * @throws IllegalArgumentException 版本号字符串格式不正确
     */
    public static int parseVerToInt(String verStr) {
        if (verStr == null || verStr.isEmpty()) {
            throw new IllegalArgumentException("版本号字符串为空！");
        }
        String[] verArray = verStr.split("\\.");
        if (verArray.length != 3) {
            throw new IllegalArgumentException("版本号字符串格式不正确！");
        }
        byte v1 = Byte.parseByte(verArray[0], 10);
        byte v2 = Byte.parseByte(verArray[1], 10);
        short v3 = Short.parseShort(verArray[2], 10);
        int ver = ((0xff & v1) << 24)
                | ((0xff & v2) << 16)
                | (0xffff & v3);
        return ver;
    }

    /**
     * 将整形数版本号转为版本号字符串
     *
     * @param ver 整形数版本号
     * @return 版本号字符串（格式：1.2.3456）
     */
    public static String stringifyVer(int ver) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append((ver >> 24) & 0xff).append('.');
        strBuf.append((ver >> 16) & 0xff).append('.');
        strBuf.append(ver & 0xffff);
        return strBuf.toString();
    }
}
