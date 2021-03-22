package cn.modernpoem.date;

import cn.modernpoem.bean.Poem;
import cn.modernpoem.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Parse the written date at the end of the content
 *
 * @author zhy
 */
public class SuffixDateParser {

    List<DateFormatter> formatters;

    public SuffixDateParser() {
        formatters = Arrays.asList(new YmdFormatter(), new YmFormatter());
    }

    public boolean parse(Poem poem) {
        if (!StringUtils.isBlank(poem.getDate())) {
            return false;
        }
        List<String> contents = poem.getLines();
        if (contents.isEmpty()) {
            return false;
        }
        String lastLine = contents.get(contents.size() - 1);
        if (Objects.equals(lastLine, "")) {
            return false;
        }
        for (DateFormatter formatter : formatters) {
            String date = formatter.format(lastLine);
            if (date != null) {
                poem.setDate(date);
                contents.remove(contents.size() - 1);
                return true;
            }
        }
        return false;
    }
}
