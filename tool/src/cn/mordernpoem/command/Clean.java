package cn.mordernpoem.command;

import cn.mordernpoem.bean.Poem;
import cn.mordernpoem.bean.Poet;
import cn.mordernpoem.util.FileHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhy
 */
public class Clean extends BaseCommand {
    private static final Map<String, String> VALID_STRING_MAP = new HashMap<>();
    private static final Node ROOT = new Node('\u0000');

    @Override
    public void help() {
        System.out.println("clean:\n" +
                "No any arguments" +
                "e.g.                    clean");
    }

    @Override
    boolean assertAndSave(ArgReader argReader) {
        return true;
    }

    @Override
    void deal() {
        Map<Poet, List<String>> poetAndPoemListDealt = new ConcurrentHashMap<>(128);
        FileHelper fileHelper = new FileHelper();
        Consumer<Poem> poemConsumer = p -> {
            boolean[] state = new boolean[]{false, false};
            List<String> before = p.getLines();
            List<String> after = new LinkedList<>();
            before.stream().map(s -> {
                String result = this.deal(s);
                if (!s.equals(result)) {
                    state[1] = true;
                }

                return result;
            }).forEach(i -> {
                if (i.length() == 0) {
                    if (state[0]) {
                        state[1] = true;
                    }

                    state[0] = true;
                } else {
                    if (state[0]) {
                        after.add("");
                    }

                    after.add(i);
                    state[0] = false;
                }

            });
            p.setLines(after);
            fileHelper.write(p);
            if (state[1]) {
                List<String> modifiedList = poetAndPoemListDealt.getOrDefault(p.getPoet(), new LinkedList<>());
                modifiedList.add(p.getTitle());
                poetAndPoemListDealt.put(p.getPoet(), modifiedList);
            }

        };
        Predicate<Poem> poemPredicate = p -> true;
        Predicate<Poet> poetPredicate = p -> true;
        this.iterate(null, poetPredicate, poemConsumer, poemPredicate, true);
        if (poetAndPoemListDealt.isEmpty()) {
            System.out.println("No poems modified.");
        } else {
            System.out.println("Modified poems:");
            poetAndPoemListDealt.forEach((key, value) -> {
                this.split();
                System.out.println(key.getName());
                System.out.println(value.stream().map(s -> "[" + s + "]").collect(Collectors.joining(" ")));
            });
        }

        this.split();
    }

    private String deal(String src) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        boolean begin = false;
        boolean lastBlank = false;
        Node now = ROOT;
        char[] strChars = src.toCharArray();
        for (char c : strChars) {
            boolean nowBlank = this.isBlank(c);
            if (nowBlank) {
                if (begin && now != ROOT) {
                    if (now.endChar) {
                        sb.append(VALID_STRING_MAP.get(temp.toString()));
                    } else {
                        sb.append(temp);
                    }

                    temp.setLength(0);
                    now = ROOT;
                    lastBlank = true;
                }
            } else {
                if (lastBlank && sb.length() > 0) {
                    sb.append(' ');
                }

                lastBlank = false;
                begin = true;
                Node last = now;
                now = now.get(c);
                if (now == null) {
                    if (temp.length() > 0) {
                        if (last.endChar) {
                            sb.append(VALID_STRING_MAP.get(temp.toString()));
                        } else {
                            sb.append(temp);
                        }

                        temp.setLength(0);
                    }

                    now = ROOT.get(c);
                    if (now == null) {
                        sb.append(c);
                        now = ROOT;
                    } else {
                        temp.append(c);
                    }
                } else {
                    temp.append(c);
                }
            }
        }

        if (now.endChar) {
            sb.append(VALID_STRING_MAP.get(temp.toString()));
        } else {
            sb.append(temp);
        }

        return sb.toString();
    }

    private boolean isBlank(char c) {
        return c == 0 || c == ' ' || c == '\r' || c == '\t' || c == 160;
    }

    static {
        // INVALID CHAR, REMOVE THEM
        Arrays.asList("\uff2f", "\ufffd", "●").forEach(c -> VALID_STRING_MAP.put(c, ""));
        Arrays.asList("　", "\uE5E5").forEach(c -> VALID_STRING_MAP.put(c, " "));

        VALID_STRING_MAP.put("--", "——");
        VALID_STRING_MAP.put("————", "——");
        VALID_STRING_MAP.put("?", "？");
        VALID_STRING_MAP.put("!", "！");
        VALID_STRING_MAP.put("......", "……");
        VALID_STRING_MAP.put(".....", "……");
        VALID_STRING_MAP.put("....", "……");
        VALID_STRING_MAP.put("...", "…");
        VALID_STRING_MAP.put(";", "；");
        VALID_STRING_MAP.put("０", "0");
        VALID_STRING_MAP.put("１", "1");
        VALID_STRING_MAP.put("２", "2");
        VALID_STRING_MAP.put("３", "3");
        VALID_STRING_MAP.put("４", "4");
        VALID_STRING_MAP.put("５", "5");
        VALID_STRING_MAP.put("６", "6");
        VALID_STRING_MAP.put("７", "7");
        VALID_STRING_MAP.put("８", "8");
        VALID_STRING_MAP.put("９", "9");
        VALID_STRING_MAP.put("○", "〇");
        VALID_STRING_MAP.put(",", "，");

        VALID_STRING_MAP.keySet().forEach(i -> ROOT.add(i.toCharArray(), 0));
    }
}
