package cn.modernpoem.date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhy
 */
public class YmFormatter implements DateFormatter {

    private final static Pattern PATTERN = Pattern.compile("^(\\d{4})[.．/-年]?([01]?\\d)[.．/-月]?[于在]?[^\\d]{0,3}$");

    @Override
    public String format(String lastLine) {
        Matcher matcher = PATTERN.matcher(lastLine);
        if (!matcher.matches()) {
            return null;
        }
        String year = matcher.group(1);
        String month = matcher.group(2);
        return year + DateFormatter.addZeroOf(month);
    }

    public static void main(String []a){
        System.out.println(new YmFormatter().format("1992"));
    }
}
