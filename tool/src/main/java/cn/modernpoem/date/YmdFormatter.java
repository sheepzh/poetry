package cn.modernpoem.date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhy
 */
public class YmdFormatter implements DateFormatter {
    private final static Pattern PATTERN = Pattern.compile("^[(（]?\\s*(\\d{4})[.,、，．/\\-年]?(\\d{1,2})[.,、，．/\\-月](\\d{1,2})[.．日]?[，于在]?.{0,4}\\s*[)）]?$");

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
}
