package cn.mordernpoem.date;

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
}
