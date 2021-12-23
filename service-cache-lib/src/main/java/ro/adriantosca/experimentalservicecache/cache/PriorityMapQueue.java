package ro.adriantosca.experimentalservicecache.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PriorityMapQueue<TIdentifier, TItem extends Identifiable<TIdentifier>> {
    @NonNull
    private final Map<TIdentifier, Node<TItem>> map;

    @Nullable
    private Node<TItem> leastRecent;

    @Nullable
    private Node<TItem> mostRecent;

    public PriorityMapQueue(int capacity) {
        map = new HashMap<>(capacity, 1f);
    }

    public int size() {
        return this.map.size();
    }

    @NonNull
    Node<TItem> add(@NonNull TItem item) {
        var node = new Node<>(item);
        if (mostRecent == null) {
            mostRecent = node;
            leastRecent = mostRecent;
            map.put(item.id(), node);
            return node;
        }

        node.setPrevious(mostRecent);
        mostRecent.setNext(node);
        mostRecent = node;
        if (leastRecent == null) {
            leastRecent = mostRecent;
        }

        map.put(item.id(), node);

        return node;
    }

    @Nullable
    public Node<TItem> get(TIdentifier id) {
        return this.map.get(id);
    }

    public void makeMostRecent(@NonNull Node<TItem> existingNode) {
        if (mostRecent == null || leastRecent == null) {
            throw new IllegalStateException(
                    "Invalid list state. This is most likely an issue with internal implementation."
                    // aka buggy code, this "cannot" happen, because is called only on non-empty list
            );
        }

        // empty list: [null]
        // one node: [null] <- LR === MR -> [null]
        // two nodes: [null] <- LR <-> MR -> [null]
        // more nodes: [null] <- LR <-> V1 <-> V2 <-> MR -> [null]

        if (existingNode == mostRecent) {
            return;
        }

        var previous = existingNode.getPrevious();
        var next = existingNode.getNext();
        if (previous != null) {
            previous.setNext(next);
        }
        if (next != null) {
            next.setPrevious(previous);
            leastRecent = next;
        }
        existingNode.setPrevious(mostRecent);
        existingNode.setNext(null);
        mostRecent.setNext(existingNode);
        mostRecent = existingNode;
    }

    public void removeLeastRecent() {
        if (leastRecent == null) {
            return;
        }
        map.remove(leastRecent.getItem().id());
        var next = leastRecent.getNext();
        if (next != null) {
            next.setPrevious(null);
        }
        leastRecent.setNext(null);
        leastRecent = next;
    }

    public void remove(@NonNull Node<TItem> node) {
        map.remove(node.getItem().id());

        var next = node.getNext();
        if (next != null) {
            next.setPrevious(node.getPrevious());
        } else {
            mostRecent = node.getPrevious();
        }

        var previous = node.getPrevious();
        if (previous != null) {
            previous.setNext(node.getNext());
        } else {
            leastRecent = node.getNext();
        }

        node.setNext(null);
        node.setPrevious(null);
    }

    public Stream<Node<TItem>> stream() {
        return map.values().stream();
    }
}
