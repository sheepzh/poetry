
package cn.modernpoem.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author zhy
 * @since 0.0.1
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArgReader {
    private static String[] ARGS;

    final Iterator<String> iterator;

    @Getter
    String last;

    public static void init(String[] args) {
        ARGS = Arrays.stream(args, 1, args.length)
                .filter(arg -> !isJvmProperty(arg))
                .toArray(String[]::new);
    }

    public static ArgReader get() {
        return new ArgReader(ARGS);
    }

    private static boolean isJvmProperty(String s) {
        return s.startsWith("-D");
    }

    private ArgReader(String[] args) {
        this.iterator = Arrays.stream(args).iterator();
        this.last = null;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public String read() {
        return last = iterator.next();
    }
}
