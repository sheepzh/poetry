package cn.mordernpoem.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhy
 * @since 0.0.1
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Poem {
    Poet poet;
    String title;
    String date;
    List<String> lines = new LinkedList<>();

    public String getTitleAndPoet() {
        return "[" + this.title + "] " + poet + "  " + this.date;
    }

    public void lineAppend(String line) {
        this.lines.add(line);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof Poem)) {
            return false;
        } else {
            Poem o = (Poem) obj;
            return Objects.equals(this.title, o.title) && Objects.equals(poet, o.poet);
        }
    }

    @Override
    public int hashCode() {
        return this.title.hashCode() ^ poet.hashCode();
    }

    @Override
    public String toString() {
        String var10000 = this.getTitleAndPoet();
        return var10000 + "\n\n" + String.join("\n", this.lines);
    }
}
