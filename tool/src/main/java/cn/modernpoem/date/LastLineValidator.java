package cn.modernpoem.date;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * To validate the last line of poem content
 *
 * @author zhy
 */
public class LastLineValidator {
    private final List<Pattern> PATTERNS = Arrays.asList(
            Pattern.compile("^[（(]?(刊载|刊登)([于在])?《.*》(\\d{4}年(第?\\d{1,2}[期月刊号]{1,2})?)?[）)]?"),
            Pattern.compile("^[（(]?(本诗)?(发表|首发)于.*《.+》.*，.*[）)]?$"),
            Pattern.compile("^[（(]?选自《.*》.*\\d{4}年.*[）)]?$"),
            Pattern.compile("^(ps|PS)[.，。:：].*$"),
            Pattern.compile("^[（(]?注[：:](.*)[）)]?$"),
            Pattern.compile("^谨此?小?诗?赠.*$")
    );


    public boolean isInvalid(String lastLine) {
        if (lastLine.isEmpty()) {
            return true;
        }

        return PATTERNS.stream().anyMatch(p -> p.matcher(lastLine).matches());
    }
}
