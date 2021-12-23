package ro.adriantosca.experimentalservicecache.cache;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Node<T> {
    @NonNull private T item;
    @Nullable private Node<T> previous;
    @Nullable private Node<T> next;
    private long accessTimeNanos;

    public Node(@NonNull T item) {
        this.item = item;
        this.previous = null;
        this.next = null;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Node<T> getPrevious() {
        return previous;
    }

    public void setPrevious(Node<T> previous) {
        this.previous = previous;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public long getAccessTimeNanos() {
        return accessTimeNanos;
    }

    public void setAccessTimeNanos(long accessTimeNanos) {
        this.accessTimeNanos = accessTimeNanos;
    }
}
