package cn.mordernpoem.date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhy
 */
public class YmdFormatter implements DateFormatter {
    private final static Pattern PATTERN = Pattern.compile("^(19|20\\d{2})[.．/]?([01]?\\d)[.．/](\\d{1,2})$");

    @Override
    public String format(String lastLine) {
        Matcher matcher = PATTERN.matcher(lastLine);
        if (!matcher.matches()) {
            return null;
        }
        String year = matcher.group(1);
        String month = matcher.group(2);
        String day = matcher.group(3);
        return year + DateFormatter.addZeroOf(month) + DateFormatter.addZeroOf(day);
    }

    public static void main(String[] a) {
        System.out.println(new YmdFormatter().format("2004/5/20"));
    }
}
