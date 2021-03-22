
package cn.mordernpoem.command;

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
    final Iterator<String> args;
    @Getter
    String last;

    public ArgReader(String[] args) {
        this.args = Arrays.stream(args, 1, args.length)
                .filter(arg -> !isJvmProperty(arg))
                .iterator();
        this.last = null;
    }

    private boolean isJvmProperty(String s) {
        return s.startsWith("-D");
    }

    public boolean hasNext() {
        return args.hasNext();
    }

    public String read() {
        return last = args.next();
    }
}
