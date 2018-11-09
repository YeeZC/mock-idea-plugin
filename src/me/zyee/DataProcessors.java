package me.zyee;

import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yee
 * @date 2018/11/9
 */
public class DataProcessors {
    public static <T> Processor<T> uniqueCheckProcessor(Collection<T> collection, Accessor<T> accessor) {
        return new UniqueCheckProcessor<>(collection, accessor);
    }

    public interface Accessor<T> {
        Accessor TRUE = element -> true;
        Accessor FALSE = element -> false;

        boolean access(T element);
    }

    private static class UniqueCheckProcessor<T> implements Processor<T> {

        private Set<T> set;
        private Accessor<T> accessor;

        public UniqueCheckProcessor(@Nullable Collection<T> collection, @NotNull Accessor<T> accessor) {
            this.set = new HashSet<>();
            if (null != collection) {
                set.addAll(collection);
            }
            this.accessor = accessor;
        }

        public UniqueCheckProcessor(Collection<T> collection) {
            this(collection, Accessor.TRUE);
        }

        public UniqueCheckProcessor(Accessor<T> accessor) {
            this(null, accessor);
        }

        public UniqueCheckProcessor() {
            this(Accessor.TRUE);
        }

        @Override
        public boolean process(T t) {
            return accessor.access(t) && set.add(t);
        }


    }
}
