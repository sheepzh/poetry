package cn.mordernpoem.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

/**
 * @author zhy
 * @since 0.0.1
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Poet {
    String name;
    List<Poem> poemList;
    String dirName;

    @Override
    public int hashCode() {
        return this.dirName == null ? 0 : this.dirName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Poet && Objects.equals(this.dirName, ((Poet) obj).dirName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
