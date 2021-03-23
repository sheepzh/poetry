package cn.modernpoem.date;

import java.util.regex.Matcher;

/**
 * @author zhy
 */
public interface DateFormatter {
    /**
     * Format the date to yyyyMMdd/yyyyMM/yyyy
     *
     * @param lastLine the last line of contents
     * @return date string, or null
     */
    String format(String lastLine);

    /**
     * Format 2 digit number
     *
     * @param monthOrDate month or date less than 10
     * @return 2 digit number
     */
    static String addZeroOf(String monthOrDate) {
        if (monthOrDate.length() == 1) {
            return '0' + monthOrDate;
        } else {
            return monthOrDate;
        }
    }

    char[] HAN_ZI0 = "〇一二三四五六七八九".toCharArray();
    char[] HAN_ZI1 = "零壹贰叁肆伍陆柒捌玖".toCharArray();

    /**
     * Translate han_zi to number
     *
     * @param hanZi han_zi
     * @return number, or input char
     */
    static String toNumber(int hanZi) {
        for (int i = 0; i < HAN_ZI0.length; i++) {
            if (hanZi == HAN_ZI0[i] || hanZi == HAN_ZI1[i]) {
                return String.valueOf((char) ('0' + i));
            }
        }
        if (hanZi == 'o' || hanZi == 'O') {
            return "0";
        }
        return String.valueOf((char) hanZi);
    }
}
