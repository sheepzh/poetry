package cn.modernpoem.command;

import cn.modernpoem.bean.Poem;
import cn.modernpoem.bean.Poet;
import cn.modernpoem.date.SuffixDateParser;
import cn.modernpoem.util.FileHelper;
import cn.modernpoem.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhy
 */
public class Clean extends BaseCommand {
    private static final Map<String, String> VALID_STRING_MAP = new HashMap<>();
    private static final Node ROOT = new Node('\u0000');

    private final SuffixDateParser parser = new SuffixDateParser();

    private final Map<Poet, List<String>> poetAndPoemListDealt = new ConcurrentHashMap<>(128);

    private final FileHelper fileHelper = new FileHelper();

    private String poetName = null;

    @Override
    public void help() {
        System.out.println("clean:\n" +
                "[-s]                    Print the poems with the similar titles written by the same poet.\n" +
                "                        Default is off\n" +
                "[-p poetName]           Specify by poet's name\n" +
                "e.g.                    clean");
    }

    @Override
    public boolean assertAndSave(ArgReader argReader) {
        boolean writerReady = false;
        while (argReader.hasNext()) {
            String arg = argReader.read();
            if (this.isArg(arg)) {
                String argCode = this.getArg(arg);
                if (writerReady) {
                    this.error("except poet name, but " + arg);
                    return false;
                }
                switch (argCode) {
                    case "s":
                        FileHelper.PRINT_SIMILAR = true;
                        break;
                    case "p":
                        writerReady = true;
                        break;
                    default:
                        return error(arg);
                }
            } else if (writerReady) {
                this.poetName = arg;
            } else {
                return error(arg);
            }
        }
        return true;
    }

    @Override
    void deal0() {
        poetAndPoemListDealt.clear();
        Predicate<Poet> poetPredicate = StringUtils.isBlank(poetName)
                ? foo -> true
                : poet -> Objects.equals(poet.getName(), poetName);
        this.iterate(poetPredicate, null, PoemHandler.of(this::poemConsumer), true);
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

    private void poemConsumer(Poem p) {
        boolean[] state = new boolean[]{false, false};
        List<String> before = p.getLines();
        List<String> after = new LinkedList<>();
        before.stream().map(s -> {
            // remove html elements
            String htmlRemoved = s.replaceAll("<[^>]+>", "")
                    .replaceAll("</[\"0-9a-zA-Z\\s]+>", "");
            String result = this.deal0(htmlRemoved);
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
        if (after.isEmpty()) {
            System.out.println("WARNING: Empty lines " + p.getFilePath());
        }
        state[1] |= parser.parse(p);
        fileHelper.write(p);
        if (state[1]) {
            List<String> modifiedList = poetAndPoemListDealt.getOrDefault(p.getPoet(), new LinkedList<>());
            modifiedList.add(p.getTitle());
            poetAndPoemListDealt.put(p.getPoet(), modifiedList);
        }
    }

    private String deal0(String src) {
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
                }
                lastBlank = true;
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
        Arrays.asList("\uff2f", "\ufffd", "●", "\uE003").forEach(c -> VALID_STRING_MAP.put(c, ""));
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
        VALID_STRING_MAP.put("／", "/");

        VALID_STRING_MAP.keySet().forEach(i -> ROOT.add(i.toCharArray(), 0));
    }
}
