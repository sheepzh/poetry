package cn.modernpoem.date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhy
 */
public class YearFormatter implements DateFormatter {
    private final static Pattern PATTERN0 = Pattern.compile("^写?于?(\\d{4})[.．/-年]?([于在]?[^\\d]{0,4})?$");

    private final static Pattern PATTERN1 = Pattern.compile("^([一二三四五六七八九〇零O0-9]{4})[.．/\\-年]?([于在]?[^\\d]{0,4})?$");

    private String pattern0(String lastLine) {
        Matcher matcher = PATTERN0.matcher(lastLine);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

    private String pattern1(String lastLine) {
        Matcher matcher = PATTERN1.matcher(lastLine);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1).chars().mapToObj(DateFormatter::toNumber).reduce((a, b) -> a + b).orElse("");
    }

    @Override
    public String format(String lastLine) {
        String result = pattern0(lastLine);
        if (result == null) {
            result = pattern1(lastLine);
        }
        return result;
    }
}
