package com.tipray.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具
 *
 * @author chends
 */
public class StringUtil {
    /**
     * 字符串转整形数
     *
     * @param str
     * @return
     */
    public static Integer parseInt(String str) {
        if (isNotEmpty(str)) {
            return (int) Double.parseDouble(str);
        }
        return null;
    }

    /**
     * 是否数字字符串
     *
     * @param str
     * @return
     */
    public static boolean isNum(String str) {
        if (isNotEmpty(str)) {
            str = str.startsWith("-") ? str.substring(1) : str;
            if (isNotEmpty(str)) {
                final String regex = "[0-9]*";
                Pattern pattern = Pattern.compile(regex);
                Matcher isNum = pattern.matcher(str);
                return isNum.matches();
            }
        }
        return false;
    }

    /**
     * 字符串非空判断
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().equals("");
    }

    /**
     * null或空串判断
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    /**
     * 对象转字符串
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    /**
     * 字符串拼接
     *
     * @param strs
     * @return
     */
    public static String append(String... strs) {
        StringBuffer sb = new StringBuffer();
        if (strs != null) {
            for (String str : strs) {
                if (str == null) {
                    continue;
                }
                sb.append(sb.length() > 0 ? "," : "");
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    public static String firstCharToLowerCase(String str) {
        if (isNotEmpty(str)) {
            str = str.trim();
            String firstChar = str.substring(0, 1);
            String otherStr = str.substring(1);
            return firstChar.toLowerCase() + otherStr;
        }
        return str;
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String firstCharToUpperCase(String str) {
        if (isNotEmpty(str)) {
            str = str.trim();
            String firstChar = str.substring(0, 1);
            String otherStr = str.substring(1);
            return firstChar.toUpperCase() + otherStr;
        }
        return str;
    }

    /**
     * 判断两个字符串是否不等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equals(String str1, String str2) {
        return (str1 == null && str2 == null) || (str1 != null && str1.equals(str2));
    }

    /**
     * 预处理逗号分隔的字符串
     *
     * @param str {@link String} 逗号分隔的字符串
     * @return {@link String}
     */
    public static String pretreatStrWithComma(String str) {
        // 替换中文符号
        str = str.replace("，", ",");
        // 替换大部分空白字符， 不限于空格 \s 可以匹配空格、制表符、换页符等空白字符的其中任意一个
        str = str.replaceAll("\\s*", "");
        if (str.startsWith(",")) {
            // 去除开头多余逗号
            str = str.substring(1);
        }
        if (str.endsWith(",")) {
            // 去除末尾多余逗号
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * 拆分处理逗号分隔的字符串
     *
     * @param str {@link String} 逗号分隔的字符串
     * @return {@link String[]}
     */
    public static String[] splitStrWithComma(String str) {
        return pretreatStrWithComma(str).split(",");
    }
}