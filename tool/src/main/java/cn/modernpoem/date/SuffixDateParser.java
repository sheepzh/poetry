package cn.mordernpoem.date;

import cn.mordernpoem.bean.Poem;

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
        formatters = Collections.singletonList(new YmdFormatter());
    }

    public void parse(Poem poem) {
        if (poem.getDate() != null) {
            return;
        }
        List<String> contents = poem.getLines();
        String lastLine = contents.get(contents.size() - 1);
        if (Objects.equals(lastLine, "")) {
            return;
        }
        for (DateFormatter formatter : formatters) {
            String date = formatter.format(lastLine);
            if (date != null) {
                poem.setDate(date);
                return;
            }
        }
    }
}
