package cn.modernpoem.date;

import cn.modernpoem.bean.Poem;
import cn.modernpoem.bean.Poet;

import java.util.Arrays;
import java.util.List;

/**
 * Parse the written date at the end of the content
 *
 * @author zhy
 */
public class SuffixDateParser {

    List<DateFormatter> formatters;
    LastLineValidator lastLineValidator = new LastLineValidator();
    MonthReplacer monthReplacer = new MonthReplacer();

    public SuffixDateParser() {
        formatters = Arrays.asList(new YmdFormatter(), new YmFormatter(), new YearFormatter());
    }

    private boolean removeLastEmptyLines(List<String> contents) {
        String latestRemoved = null;
        while (!contents.isEmpty() && lastLineValidator.isInvalid(contents.get(contents.size() - 1))) {
            latestRemoved = contents.remove(contents.size() - 1);
        }
        return latestRemoved != null;
    }

    private String preprocess(String lastLine, Poem poem) {
        Poet poet = poem.getPoet();
        String poetName = poet.getName();
        lastLine = lastLine
                .replace(poetName, "")
                .replaceAll(" ", "")
                .replace("——", "-")
                .trim();

        lastLine = monthReplacer.replace(lastLine);

        if (lastLine.isEmpty()) {
            return lastLine;
        }
        char first = lastLine.charAt(0);
        if (first == '(' || first == '（' || first == '[') {
            lastLine = lastLine.substring(1);
        }
        if (lastLine.isEmpty()) {
            return lastLine;
        }
        char last = lastLine.charAt(lastLine.length() - 1);
        if (last == ')' || last == '）' || last == ']') {
            lastLine = lastLine.substring(0, lastLine.length() - 1);
        }
        if (lastLine.startsWith("——")) {
            lastLine = lastLine.substring(2);
        }
        if (lastLine.startsWith("写于")) {
            lastLine = lastLine.substring(2);
        }
        if (lastLine.startsWith("于")) {
            lastLine = lastLine.substring(1);
        }
        if (lastLine.endsWith("初稿") || lastLine.endsWith("定稿")) {
            lastLine = lastLine.substring(lastLine.length() - 2);
        }
        return lastLine;
    }

    public boolean parse(Poem poem) {
        List<String> contents = poem.getLines();
        boolean result = removeLastEmptyLines(contents);
        if (contents.isEmpty()) {
            return false;
        }
        String lastLine = preprocess(contents.get(contents.size() - 1), poem);
        for (DateFormatter formatter : formatters) {
            String date = formatter.format(lastLine);
            if (date != null) {
                poem.setDate(date);
                contents.remove(contents.size() - 1);
                result = true;
                break;
            }
        }
        removeLastEmptyLines(contents);
        return result;
    }
}
