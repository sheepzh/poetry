package cn.mordernpoem;

import cn.mordernpoem.command.BaseCommand;
import cn.mordernpoem.command.Clean;
import cn.mordernpoem.command.Count;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhy
 */
public class Main {
    private static final Map<String, Class<? extends BaseCommand>> COMMAND_MAP = new HashMap<>();
    private static final String HELP = "All the instructions:\n" +
            "clean        Format all the text files.\n" +
            "count        Count the poets or poems, according to certain query criteria.\n" +
            "help         Get the help info.\n" +
            "\n" +
            "Use 'help [specific instructions, clean, count etc.]' to get specific help information for an instruction\n";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            help();
        } else {
            Class<? extends BaseCommand> commandType;
            String help = "help";
            if (Objects.equals(args[0], help)) {
                if (args.length > 1) {
                    if ((commandType = COMMAND_MAP.get(args[1])) != null) {
                        newInstance(commandType).help();
                    } else {
                        System.out.println("Instruction not found：" + args[1]);
                    }
                } else {
                    help();
                }

            } else {
                commandType = COMMAND_MAP.get(args[0]);
                if (commandType == null) {
                    help();
                } else {
                    long start = System.currentTimeMillis();
                    newInstance(commandType).deal(args);
                    System.out.println("\nTime used:" + (System.currentTimeMillis() - start) + "ms");
                }
            }
        }
    }

    private static BaseCommand newInstance(Class<? extends BaseCommand> clz) throws Exception {
        Constructor<? extends BaseCommand> constructor = clz.getConstructor();
        return constructor.newInstance();
    }

    private static void help() {
        System.out.println(HELP);
    }

    static {
        COMMAND_MAP.put("count", Count.class);
        COMMAND_MAP.put("clean", Clean.class);
//        COMMAND_MAP.put("generate", Generate.class);
    }
}
