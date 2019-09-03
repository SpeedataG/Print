package com.spd.jxprint.utils;

/**
 * 条码内容检测
 *
 * @author zzc
 */
public class CheckUtils {

    public static boolean numberCheck(String content) {
        if (!content.isEmpty()) {
            String regex = "^\\d*$";
            return content.matches(regex);
        }
        return false;
    }

    public static boolean code39Check(String content) {
        if (!content.isEmpty()) {
            String regex = "^[0-9A-Z\\s$%+]*$";
            return content.matches(regex);
        }
        return false;
    }

    public static boolean codeBarCheck(String content) {
        if (!content.isEmpty()) {
            String regex = "^[0-9A-D$+-./:]*$";
            return content.matches(regex);
        }
        return false;
    }
}
