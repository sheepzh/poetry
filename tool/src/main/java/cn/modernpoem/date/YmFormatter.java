package cn.modernpoem.date;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhy
 */
public class YmFormatter implements DateFormatter {
    private final static Pattern PATTERN0 = Pattern.compile("^（?\\s*(\\d{4})[.，。·,、．/╱\\-年]?([01]?\\d)[.，。·,、．/╱\\-月]?[，于在]?[^\\d]{0,3}\\s*）?$");

    private final static Pattern PATTERN1 = Pattern.compile("^（?\\s*([一二三四五六七八九〇零O0-9]{4})[.，、,．/╱\\-年]?\\s*([0-9元正一二三四五六七八九十]{1,2})[.，、,．/╱\\-月]?[于在]?[^\\d]{0,3}\\s*）?$");

    private final static Pattern PATTERN2 = Pattern.compile("^（?\\s*(\\d{1,2})[.，。,、．/╱\\-](\\d{4})[/]{0,2}$");

    private String format0(String lastLine) {
        Matcher matcher = PATTERN0.matcher(lastLine);
        if (!matcher.matches()) {
            return null;
        }
        String year = matcher.group(1);
        String month = matcher.group(2);
        return year + DateFormatter.addZeroOf(month);
    }

    private String format1(String lastLine) {
        Matcher matcher = PATTERN1.matcher(lastLine);
        if (!matcher.matches()) {
            return null;
        }
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
        return year + DateFormatter.addZeroOf(month);
    }

    private String format2(String lastLine) {
        Matcher matcher = PATTERN2.matcher(lastLine);
        if (!matcher.matches()) {
            return null;
        }
        String year = matcher.group(2);
        String month = matcher.group(1);
        return year + DateFormatter.addZeroOf(month);
    }

    List<Function<String, String>> formatters = Arrays.asList(this::format0, this::format1, this::format2);

    @Override
    public String format(String lastLine) {
        for (Function<String, String> formatter : formatters) {
            String result = formatter.apply(lastLine);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static void main(String[] a) {
        System.out.println(new YmFormatter().format("一九八O年十月"));
    }
}
