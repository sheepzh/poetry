package cn.modernpoem.command;

import cn.modernpoem.bean.Poem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.function.Consumer;

public interface PoemHandler extends Consumer<Poem> {
    /**
     * 是否忽略内容
     */
    boolean ignoreContent();

    static PoemHandler of(Consumer<Poem> consumer) {
        return new PoemHandlerWrapper(consumer, false);
    }

    static PoemHandler of(Consumer<Poem> consumer, boolean ignoredContent) {
        return new PoemHandlerWrapper(consumer, ignoredContent);
    }

    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class PoemHandlerWrapper implements PoemHandler {
        Consumer<Poem> consumer;
        boolean ignoredContent;

        @Override
        public void accept(Poem poem) {
            consumer.accept(poem);
        }

        @Override
        public boolean ignoreContent() {
            return ignoredContent;
        }
    }

}
