package cn.modernpoem.date;

import java.util.Calendar;
import java.util.Date;
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
        monthOrDate = monthOrDate.replace("l", "1");
        if (monthOrDate.length() == 1) {
            return '0' + monthOrDate;
        } else {
            return monthOrDate;
        }
    }

    static String yearOf2Bit(String year) {
        // Current period
        // e.g.
        //
        // 2021 => 2
        // 2019 => 1
        int yearOfNow = Calendar.getInstance().get(Calendar.YEAR);
        int currentPeriod = (yearOfNow % 100) / 10;
        int currentCentury = yearOfNow / 100;
        int century = year.charAt(0) - '0' <= currentPeriod ?
                // this century
                currentCentury :
                // last century
                currentCentury - 1;
        return century + year;
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
        if (Character.isDigit(hanZi)) {
            return "" + ((char) hanZi);
        }
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
