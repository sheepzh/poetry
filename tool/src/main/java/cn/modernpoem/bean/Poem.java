package cn.modernpoem.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.File;
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
        String titleAndPoet = this.getTitleAndPoet();
        return titleAndPoet + " " + (date == null ? "" : date) + "\n\n" + String.join("\n", this.lines);
    }

    public String getFilePath() {
        if (poet == null) {
            return "";
        }
        return poet.getDirName() + File.separator + title + ".pt";
    }
}
