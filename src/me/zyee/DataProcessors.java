package me.zyee;

import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    public interface AccessList<T> extends List<T> {
        static <T> DataProcessors.AccessList<T> of(List<T> list) {
            return new AccessListImpl(list);
        }

        static <T> DataProcessors.AccessList<T> of(T[] array) {
            return new AccessListImpl(Arrays.asList(array));
        }

        AccessList<T> execute(Accessor<T> accessor);
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

    /**
     * @author yee
     * @date 2018/11/14
     */
    public static class AccessListImpl<T> implements AccessList<T> {
        private List<T> list;

        private AccessListImpl(List<T> list) {
            this.list = new ArrayList<>(list);
        }

        @Override
        public AccessList<T> execute(Accessor<T> accessor) {
            List<T> result = new ArrayList<>();
            for (T t : list) {
                if (accessor.access(t)) {
                    result.add(t);
                }
            }
            return new AccessListImpl(result);
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return list.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return list.iterator();
        }

        @Override
        public Object[] toArray() {
            return list.toArray();
        }

        @Override
        public <T1> T1[] toArray(T1[] a) {
            return list.toArray(a);
        }

        @Override
        public boolean add(T t) {
            return list.add(t);
        }

        @Override
        public boolean remove(Object o) {
            return list.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return list.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            return list.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            return list.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return list.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return list.retainAll(c);
        }

        @Override
        public void clear() {
            list.clear();
        }

        @Override
        public T get(int index) {
            return list.get(index);
        }

        @Override
        public T set(int index, T element) {
            return list.set(index, element);
        }

        @Override
        public void add(int index, T element) {
            list.add(index, element);
        }

        @Override
        public T remove(int index) {
            return list.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        @Override
        public ListIterator<T> listIterator() {
            return list.listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            return list.listIterator(index);
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            return list.subList(fromIndex, toIndex);
        }
    }
}
