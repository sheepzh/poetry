package cn.modernpoem.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhy
 */
public class StringUtils {
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static final Set<Character> BLANK_CHARS = new HashSet<>(Arrays.asList('\u3000', '\u2800'));

    public static boolean isBlankChar(char c) {
        return c <= ' ' || BLANK_CHARS.contains(c);
    }

    public static String trim(String str) {
        int length = str.length();
        int len = length;
        int st = 0;
        while (st < len && isBlankChar(str.charAt(st))) {
            st++;
        }
        while (st < len && isBlankChar(str.charAt(len - 1))) {
            len--;
        }
        return ((st > 0) || (len < length)) ?
                str.substring(st, len)
                : str;
    }
}
