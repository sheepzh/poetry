package cn.modernpoem.date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhy
 */
public class YearFormatter implements DateFormatter {
    private final static Pattern PATTERN = Pattern.compile("^写?于?(\\d{4})[.．/-年]?([于在]?[^\\d]{0,4})?$");

    @Override
    public String format(String lastLine) {
        Matcher matcher = PATTERN.matcher(lastLine);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

    public static void main(String []a){
        System.out.println(new YearFormatter().format("1992"));
    }
}
