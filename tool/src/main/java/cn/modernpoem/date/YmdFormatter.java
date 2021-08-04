package cn.modernpoem.date;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhy
 */
public class YmdFormatter implements DateFormatter {
    private final static Pattern PATTERN = Pattern.compile("^[(（]?\\s*(\\d{4})[.,、′，．/╱\\-年]?(\\d{1,2})[.,、′，．/╱\\-月](\\d{1,2})[.．日]?[，于在]?.{0,4}\\s*[)）]?$");
    private final static Pattern PATTERN_1 = Pattern.compile("([零一二三四五六七八九十〇]{4})年([一二三四五六七八九十〇]{1,2})月([一二三四五六七八九十〇廿卅]{1,3})日");

    @Override
    public String format(String lastLine) {
        Matcher matcher = PATTERN.matcher(lastLine);
        if (matcher.matches()) {
            String year = matcher.group(1);
            String month = matcher.group(2);
            String day = matcher.group(3);
            return year + DateFormatter.addZeroOf(month) + DateFormatter.addZeroOf(day);
        }
        matcher = PATTERN_1.matcher(lastLine);
        if (matcher.matches()) {
            String year = matcher.group(1).chars().mapToObj(DateFormatter::toNumber).collect(Collectors.joining());
            String month = matcher.group(2).chars().mapToObj(DateFormatter::toNumber).collect(Collectors.joining());
            if (Objects.equals(month, "元") || Objects.equals(month, "正")) {
                // Replace Chinese calendar with international calendar
                month = "01";
            } else if (month.length() == 2) {
                month = month.replaceAll("十", "1");
            } else {
                month = Objects.equals(month, "十") ? "10" : "0" + month;
            }
            String date = matcher.group(3)
                    .replace("廿", "二")
                    .replace("二十", "2")
                    .replace("卅", "三")
                    .replace("三十", "3")
                    .replace("十", "1")
                    .chars().mapToObj(DateFormatter::toNumber)
                    .collect(Collectors.joining());
            return year + DateFormatter.addZeroOf(month) + DateFormatter.addZeroOf(date);
        }
        return null;
    }
}
