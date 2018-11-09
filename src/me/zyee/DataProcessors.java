package me.zyee;

import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author yee
 * @date 2018/11/9
 */
public class DataProcessors {
    public static <T> Processor<T> checkProcessor(Collection<T> collection, Accessor<T> accessor) {
        return new CheckProcessor<>(collection, accessor);
    }

    public interface Accessor<T> {
        Accessor TRUE = element -> true;
        Accessor FALSE = element -> false;

        boolean access(T element);
    }

    private static class CheckProcessor<T> implements Processor<T> {

        private Collection<T> set;
        private Accessor<T> accessor;

        public CheckProcessor(@Nullable Collection<T> collection, @NotNull Accessor<T> accessor) {

            if (null != collection) {
                this.set = collection;
            } else {
                this.set = new HashSet<>();
            }
            this.accessor = accessor;
        }

        public CheckProcessor(Collection<T> collection) {
            this(collection, Accessor.TRUE);
        }

        public CheckProcessor(Accessor<T> accessor) {
            this(null, accessor);
        }

        public CheckProcessor() {
            this(Accessor.TRUE);
        }

        @Override
        public boolean process(T t) {
            return accessor.access(t) && set.add(t);
        }


    }
}
