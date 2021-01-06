package me.qping.utils.database.util;

/**
 * @ClassName StringUtils
 * @Description TODO
 * @Author qping
 * @Date 2021/1/5 14:00
 * @Version 1.0
 **/
public class StringUtils {

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

}
