package cn.modernpoem.util;

/**
 * @author zhy
 */
public class StringUtils {
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
