package ru.innotech.education.rxjava;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.innotech.education.rxjava.model.CollectionItem;

import java.util.Iterator;

public class MyIterator<T> implements Iterator<T> {

    private final Logger logger = LogManager.getLogger(MyIterator.class);
    private CollectionItem<T> current;

    public MyIterator(CollectionItem<T> head) {
        current = head;
    }

    @Override
    public boolean hasNext() {
        if(current != null && !current.getRemoved()){
            return true;
        }
        else {
            if(current != null && current.getNext() != null) {
                current = current.getNext();
                return hasNext();
            }
        }
        return false;
    }

    @Override
    public T next() {
        T value = current.getValue();

        current = current.getNext();
        if (current == null) {
            logger.warn("current is null");
        }
        return value;
    }
}

