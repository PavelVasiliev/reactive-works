package ru.innotech.education.rxjava;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.innotech.education.rxjava.model.CollectionItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AppendOnlyList<T>
        implements Collection<T> {

    private static Logger LOGGER = LogManager.getLogger(AppendOnlyList.class);
    private final transient Object lock = new Object();

    private final AtomicInteger size = new AtomicInteger(0);
    private CollectionItem<T> head;
    private CollectionItem<T> tail;

    public AppendOnlyList() {
    }

    public AppendOnlyList(Collection<T> c) {
        addAll(c);
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public boolean contains(Object o) {
        return ifOEqualsToItem(o, head);
    }

    private boolean ifOEqualsToItem(Object o, CollectionItem<T> item) {
        if (item != null) {
            if (item.getValue().equals(o)) {
                return true;
            } else {
                return ifOEqualsToItem(o, item.getNext());
            }
        }
        return false;
    }

    @Override
    public MyIterator<T> iterator() {
        return new MyIterator<>(head);
    }

    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    private List<T> toList() {
        MyIterator<T> iterator = iterator();
        List<T> tmp = new ArrayList<>();
        while (iterator.hasNext()) {
            tmp.add(iterator.next());
        }
        return tmp;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        Object[] elementData = new Object[size()];
        if (a.length < elementData.length) {
            return (T1[]) Arrays.copyOf(elementData, elementData.length, a.getClass());
        }

        System.arraycopy(elementData, 0, a, 0, elementData.length);
        if (a.length > elementData.length) {
            a[elementData.length] = null;
        }
        return a;
    }

    @Override
    public boolean add(T t) {
        CollectionItem<T> item;
        synchronized (lock) {
            item = new CollectionItem<>(t);
            if (head == null) {
                head = item;
                tail = head;
                size.incrementAndGet();
                return true;
            } else if (tail != null) {
                tail.setNext(item);
                tail = item;
                size.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        return makeItemRemoved(o, head);
    }

    private boolean makeItemRemoved(Object o, CollectionItem<T> item) {
        synchronized (lock) {
            if (item.getValue() != null && item.getValue().equals(o)) {
                item.setRemoved(true);
                size.decrementAndGet();
                LOGGER.info("removed, o = " + o.toString());
                return true;
            } else {
                makeItemRemoved(o, item.getNext());
            }
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean result = true;
        for (Object o : c) {
            if (!contains(o)) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = true;
        for (T t : c) {
            if (!add(t)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = true;
        for (Object o : c) {
            if (!remove(o)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<T> tmp = toList();
        List<T> newCollection = new ArrayList<>();
        boolean result = true;
        for (Object o : c) {
            if (tmp.contains(o)) {
                newCollection.add((T) o);
            } else {
                result = false;
            }
        }
        head = new CollectionItem<>(newCollection.get(0));
        for (int i = 1; i < newCollection.size(); i++) {
            retain(head, newCollection.get(i));
        }
        return result;
    }

    private void retain(CollectionItem<T> item, T value) {
        if(item.getNext() != null) {
            retain(item.getNext(), value);
        }
        item.setNext(new CollectionItem<>(value));
    }

    @Override
    public void clear() {
        head = null;
    }
}
