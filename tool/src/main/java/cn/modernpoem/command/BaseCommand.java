package cn.modernpoem.command;

import cn.modernpoem.bean.Poem;
import cn.modernpoem.bean.Poet;
import cn.modernpoem.util.FileHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhy
 */
public abstract class BaseCommand implements ArgAssertable {
    private String err = "";

    /**
     * 打印帮助信息
     */
    public abstract void help();

    /**
     * 处理命令
     */
    abstract void deal0();

    public void deal() {
        ArgReader reader = ArgReader.get();
        if (!this.assertAndSave(reader)) {
            System.out.println("未知指令" + reader.getLast() + ",err:" + this.err);
            this.help();
        } else {
            this.deal0();
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

    boolean error(String msg) {
        this.err = msg;
        return false;
    }

    void iterate(Consumer<Poet> poetConsumer, Consumer<Poem> poemConsumer) {
        this.iterate(null, poetConsumer, poemConsumer, false);
    }

    void iterate(Consumer<Poet> poetConsumer,
                 Consumer<Poem> poemConsumer,
                 boolean multiThread) {
        this.iterate(null, poetConsumer, poemConsumer, multiThread);
    }

    void iterate(Predicate<Poet> poetPredicate,
                 Consumer<Poet> poetConsumer,
                 Consumer<Poem> poemConsumer,
                 boolean multiThread) {
        Predicate<Poet> finalPoetPredicate = poetPredicate == null ? foo -> true : poetPredicate;
        Predicate<Poem> finalPredicate = a -> true;

        FileHelper fileHelper = new FileHelper();
        List<Poet> poets = fileHelper.getAll();
        Consumer<Thread> dealMethod = multiThread ? Thread::start : Thread::run;
        List<Thread> threads = poets.stream().filter(finalPoetPredicate)
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

    void split() {
        System.out.println("-------------------------------------");
    }
}
