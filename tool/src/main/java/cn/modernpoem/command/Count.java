package cn.mordernpoem.command;

import cn.mordernpoem.bean.Poem;
import cn.mordernpoem.bean.Poet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author zhy
 */
public class Count extends BaseCommand {
    private boolean poemC = true;
    private boolean all = false;
    private boolean title = false;
    private boolean content = false;
    private boolean detail = false;
    private String query;

    @Override
    public void help() {
        System.out.println("count:\n" +
                "[-a]                    Count all poetry files.\n" +
                "                        The following switches will be invalid if this one keeps on.\n" +
                "                        This switch turns off by default, but on if no other switches keep on.\n" +
                "[-d]                    Display details, default off.\n" +
                "[-s]                    Display briefs, default off.\n" +
                "                        Only [-s] is valid while turn on with [-d].\n" +
                "[[-[t][c]] WORD]        Query conditions and their switches：\n" +
                "    [t][c]              Query coverage: [t]=title,[c]=content.\n" +
                "                        [t] is on, if none is on.\n" +
                "    WORD                Word in title/content.\n\n" +
                "e.g.                    count -d -tc moon");
    }

    @Override
    boolean assertAndSave(ArgReader argReader) {
        while (argReader.hasNext()) {
            String s = argReader.read();
            if (this.isArg(s)) {
                s = this.getArg(s);
                if ("s".equals(s)) {
                    this.poemC = false;
                } else if ("a".equals(s)) {
                    this.all = true;
                } else {
                    char[] var3 = s.toCharArray();

                    for (char c : var3) {
                        switch (c) {
                            case 'c':
                                this.content = true;
                                break;
                            case 'd':
                                this.detail = true;
                                break;
                            case 't':
                                this.title = true;
                                break;
                            default:
                                return this.invalidArg(c);
                        }
                    }
                }
            } else {
                this.query = s;
            }
        }

        if (this.query == null) {
            this.all = true;
        }

        if (!this.title && !this.content) {
            this.title = true;
        }

        return true;
    }

    @Override
    void deal() {
        List<Poem> titleContains = new ArrayList<>();
        Map<Poem, List<String>> appear = new HashMap<>(16);
        Consumer<Poem> poem2Poet;
        if (!this.all) {
            Consumer<Poem> poemConsumer = new PoemConsumer(title, query, titleContains, appear);
            this.iterate(null, poemConsumer);
            if (this.poemC) {
                if (this.title) {
                    titleContains.stream().map(p -> p.getTitle() + "  by  " + p.getPoet()).forEach(System.out::println);
                    System.out.printf("[%s] appears %s time(s) in the title.%n", this.query, titleContains.size());
                }

                if (this.content) {
                    this.split();
                    if (this.detail) {
                        appear.keySet().stream().map(p -> "\n------\n" + p).forEach(System.out::println);
                    } else {
                        appear.entrySet().stream().map(e -> {
                            Poem p = e.getKey();
                            String lines = String.join("\n", e.getValue());
                            return "------\n" + p.getTitleAndPoet() + "\n" + lines;
                        }).forEach(System.out::println);
                    }
                    this.split();
                    int contentNum = appear.values().stream().map(List::size).reduce(Integer::sum).orElse(0);
                    System.out.printf("[%s] appears %s time(s) in the content of %s poem(s).%n", this.query, contentNum, appear.size());
                }
            } else {
                Map<Poet, List<String>> map = new HashMap<>(appear.size());
                poem2Poet = p -> {
                    List<String> list = map.getOrDefault(p.getPoet(), new LinkedList<>());
                    list.add(p.getTitle());
                    map.put(p.getPoet(), list);
                };
                BiConsumer<Poet, List<String>> poetShow = (k, v) -> {
                    System.out.println(k.getName());
                    System.out.println(v.stream().map(p -> "[" + p + "]").collect(Collectors.joining(" ")));
                    this.split();
                };
                if (this.title) {
                    titleContains.forEach(poem2Poet);
                    this.split();
                    map.forEach(poetShow);
                    System.out.printf("[%s] appears in the title of poems written by %s poets.%n", this.query, map.size());
                    this.split();
                }

                if (this.content) {
                    map.clear();
                    appear.keySet().forEach(poem2Poet);
                    this.split();
                    map.forEach(poetShow);
                    System.out.printf("[%s] appears in the content of poems written by %s poets.%n", this.query, map.size());
                }
            }
        } else {
            AtomicInteger mc = new AtomicInteger();
            AtomicInteger pc = new AtomicInteger();
            poem2Poet = p -> mc.getAndIncrement();
            Consumer<Poet> poetConsumer = p -> pc.getAndIncrement();
            this.iterate(poetConsumer, poem2Poet);
            System.out.printf("%s poem(s) written by %s poet(s) are included.%n", mc.get(), pc.get());
        }

    }

    public static void main(String[] a) {
        new Count().help();
    }

    /**
     * 诗歌内容处理
     */
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class PoemConsumer implements Consumer<Poem> {
        boolean title;
        String query;
        List<Poem> titleContains;
        Map<Poem, List<String>> appear;

        @Override
        public void accept(Poem p) {
            if (this.title && p.getTitle().contains(this.query)) {
                titleContains.add(p);
            }
            List<String> appearLines = new ArrayList<>();
            p.getLines().stream().filter(s -> s.contains(this.query)).forEach(appearLines::add);
            // 如果内容有关键词，则添加
            if (!appearLines.isEmpty()) {
                appear.put(p, appearLines);
            }
        }
    }
}
