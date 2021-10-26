package ru.innotech.education.rxjava.model;

public final class CollectionItem<T> {

    private T value;
    private Boolean removed;
    private CollectionItem<T> next;

    public CollectionItem(T value) {
        this.value = value;
        removed = false;
    }

    public T getValue() {
        return value;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    public CollectionItem<T> getNext() {
        return next;
    }

    public void setNext(CollectionItem<T> next) {
        this.next = next;
    }
}
