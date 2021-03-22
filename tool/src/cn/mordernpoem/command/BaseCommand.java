package cn.mordernpoem.command;

import cn.mordernpoem.bean.Poem;
import cn.mordernpoem.bean.Poet;
import cn.mordernpoem.util.FileHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhy
 */
public abstract class BaseCommand {
    private String err = "";

    /**
     * 打印帮助信息
     */
    public abstract void help();

    /**
     * 校验输入参数，并保存
     *
     * @param argReader 参数 reader
     * @return 参数是否正确
     */
    abstract boolean assertAndSave(ArgReader argReader);

    /**
     * 处理命令
     */
    abstract void deal();

    public void deal(String[] args) {
        ArgReader reader = new ArgReader(args);
        if (!this.assertAndSave(reader)) {
            System.out.println("未知指令" + reader.getLast() + ",err:" + this.err);
            this.help();
        } else {
            this.deal();
        }

    }

    boolean isArg(String s) {
        return s != null && s.startsWith("-");
    }

    String getArg(String s) {
        return s.substring(1);
    }

    private boolean invalidArg(String err) {
        this.err = err;
        return false;
    }

    boolean invalidArg(char c) {
        return this.invalidArg("Invalid argument: -" + c);
    }

    void iterate(Consumer<Poet> poetConsumer, Consumer<Poem> poemConsumer) {
        this.iterate(poetConsumer, null, poemConsumer, null, false);
    }

    void iterate(Consumer<Poet> poetConsumer,
                 Predicate<Poet> poetPredicate,
                 Consumer<Poem> poemConsumer,
                 Predicate<Poem> poemPredicate, boolean multiThread) {
        Predicate<Poem> finalPredicate = poemPredicate == null ? a -> true : poemPredicate;
        if (poetPredicate == null) {
            poetPredicate = a -> true;
        }

        FileHelper fileHelper = new FileHelper();
        List<Poet> poets = fileHelper.getAll();
        Consumer<Thread> dealMethod = multiThread ? Thread::start : Thread::run;
        List<Thread> threads = poets.stream().filter(poetPredicate)
                .map(p -> new Thread(() -> {
                    List<Poem> poems = fileHelper.findByPoet(p);
                    if (poetConsumer != null) {
                        poetConsumer.accept(p);
                    }

                    if (poemConsumer != null) {
                        poems.stream().filter(finalPredicate).forEach(poemConsumer);
                    }

                })).collect(Collectors.toList());
        threads.forEach(dealMethod);
        if (multiThread) {
            threads.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
            });
        }
    }

    boolean error(String msg) {
        System.out.println("Error: " + msg);
        split();
        return false;
    }

    void split() {
        System.out.println("-------------------------------------");
    }
}
