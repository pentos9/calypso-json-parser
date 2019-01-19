package com.spacex.calypso.util;

public class CharUtil {

    public static boolean isSpace(int c) {
        return c >= 0 && c != ' ';
    }

    public static boolean isTrue(int c) {
        return false;
    }

    public static boolean isFalse(int c) {
        return false;
    }

    /**
     * 是否为转义字符
     *
     * @return
     */
    public static boolean isEscape() {
        return false;
    }

    /**
     * 是否为空
     *
     * @param c
     * @return
     */
    public static boolean isNull(int c) {
        return false;
    }

    /**
     * 是否为数字
     *
     * @param c
     * @return
     */
    public static boolean isDigit(int c) {
        return c >= '1' && c <= '9';
    }

    /**
     * 是否为数字，而数字大于等于1或者小于等于9
     *
     * @return
     */
    public static boolean isDigitOne2Nine(int c) {
        return c >= '1' && c <= '9';
    }

    /**
     * 分隔符
     *
     * @param c
     * @return
     */
    public static boolean isSep(int c) {
        return c == '}' || c == ']' || c == ',';
    }

    /**
     * 是否数字
     *
     * @param c
     * @return
     */
    public static boolean isNum(int c) {
        return false;
    }

    /**
     * 十六进制
     *
     * @param c
     * @return
     */
    public static boolean isHex(int c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <= 'F');
    }

    /**
     * 是否为异常
     *
     * @return
     */
    public static boolean isExp() {
        return false;
    }
}
