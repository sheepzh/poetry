package cn.modernpoem.date;

import cn.modernpoem.bean.Poem;

import java.util.Arrays;
import java.util.List;

/**
 * Parse the written date at the end of the content
 *
 * @author zhy
 */
public class SuffixDateParser {

    List<DateFormatter> formatters;

    public SuffixDateParser() {
        formatters = Arrays.asList(new YmdFormatter(), new YmFormatter(), new YearFormatter());
    }

    private boolean removeLastEmptyLines(List<String> contents) {
        String latestRemoved = null;
        while (!contents.isEmpty() && contents.get(contents.size() - 1).isEmpty()) {
            latestRemoved = contents.remove(contents.size() - 1);
        }
        return latestRemoved != null;
    }

    private String preprocess(String lastLine) {
        lastLine = lastLine.replace(" ", "").trim();

        char first = lastLine.charAt(0);
        if (first == '(' || first == '（') {
            lastLine = lastLine.substring(1);
        }
        char last = lastLine.charAt(lastLine.length() - 1);
        if (last == ')' || last == '）') {
            lastLine = lastLine.substring(0, lastLine.length() - 1);
        }

        if (lastLine.startsWith("——")) {
            lastLine = lastLine.substring(2);
        }
        if (lastLine.startsWith("写于")) {
            lastLine = lastLine.substring(2);
        }
        return lastLine;
    }

    public boolean parse(Poem poem) {
        List<String> contents = poem.getLines();
        if (contents.isEmpty()) {
            return false;
        }
        boolean result = removeLastEmptyLines(contents);
        String lastLine = preprocess(contents.get(contents.size() - 1));
        for (DateFormatter formatter : formatters) {
            String date = formatter.format(lastLine);
            if (date != null) {
                poem.setDate(date);
                contents.remove(contents.size() - 1);
                result = true;
            }
        }
        removeLastEmptyLines(contents);
        return result;
    }
}
