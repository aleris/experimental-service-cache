package ro.adriantosca.experimentalservicecache.cache;

import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.value.qual.IntRange;

public class AdiMemoryCache<TIdentifier, TValue> implements Cache<TIdentifier, TValue> {

    private static final int NO_MAXIMUM_SIZE = -1;
    private static final long NO_EXPIRE_TIME = -1;

    @NonNull
    private final PriorityMapQueue<TIdentifier, CachedItem<TIdentifier, TValue>> queue;

    private final ReentrantLock lock = new ReentrantLock();

    private final int maximumSize;
    private final long expireAfterAccessNanos;
    private final boolean eagerRemoveExpired;
    private final Timer timer;

    private AdiMemoryCache(
            int maximumSize,
            long expireAfterAccessNanos,
            boolean eagerRemoveExpired,
            @NonNull Timer timer
    ) {
        this.maximumSize = maximumSize;
        this.expireAfterAccessNanos = expireAfterAccessNanos;
        this.eagerRemoveExpired = eagerRemoveExpired;
        this.timer = timer;

        queue = new PriorityMapQueue<>((int) (1.25 * maximumSize + 1));
    }

    public static class Builder<TIdentifier, TValue> {
        private int maximumSize = NO_MAXIMUM_SIZE;
        private long expireAfterAccessNanos = NO_EXPIRE_TIME;
        private boolean eagerRemoveExpired = false;
        private Timer timer = SystemTimer.INSTANCE;

        public Builder<TIdentifier, TValue> withMaximumSize(@IntRange(from=2, to=Integer.MAX_VALUE) int maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public Builder<TIdentifier, TValue> withExpireAfterAccess(@NonNull Duration duration) {
            this.expireAfterAccessNanos = duration.toNanos();
            return this;
        }

        public Builder<TIdentifier, TValue> withEagerRemoveExpired(boolean eagerRemoveExpired) {
            this.eagerRemoveExpired = eagerRemoveExpired;
            return this;
        }

        public Builder<TIdentifier, TValue> withTimer(@NonNull Timer timer) {
            this.timer = timer;
            return this;
        }

        public AdiMemoryCache<TIdentifier, TValue> build() {
            return new AdiMemoryCache<>(maximumSize, expireAfterAccessNanos, eagerRemoveExpired, timer);
        }
    }

    public int size() {
        return queue.size();
    }

    @Override
    @NonNull
    public TValue get(@NonNull TIdentifier id, @NonNull ExternalGet<TValue, TIdentifier> externalGet) {
        var now = timer.nanoTime();

        if (eagerRemoveExpired && expireAfterAccessNanos != NO_EXPIRE_TIME) {
            removeAllExpired(now);
        }

        lock.lock();

        try {
            var existing = queue.get(id);

            var isExpired = expireAfterAccessNanos != NO_EXPIRE_TIME && existing != null && isExpired(existing, now);

            if (existing == null || isExpired) {
                var value = externalGet.call(id);
                addEnsuringSize(id, value, now);
                return value;
            } else {
                update(existing, now);
                return existing.getItem().value();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(
            @NonNull TIdentifier id,
            @NonNull TValue value,
            @NonNull ExternalPut<TValue, TIdentifier> externalPut
    ) {
        var now = timer.nanoTime();

        lock.lock();

        try {
            var existing = queue.get(id);
            if (existing == null) {
                addEnsuringSize(id, value, now);
                externalPut.call(id, value);
            } else {
                externalPut.call(id, value);
                update(existing, now);
                // another option would be to put it in cache before doing the actual update with the external call
                // this would make it immediately available in the cache for get, but will also break consistency
                // guaranties of the external store
            }
        } finally {
            lock.unlock();
        }
    }

    private void addEnsuringSize(@NonNull TIdentifier id, @NonNull TValue value, long now) {
        var item = new CachedItem<>(id, value);
        if (maximumSize != NO_MAXIMUM_SIZE && queue.size() == maximumSize) {
            queue.removeLeastRecent();
        }
        var node = queue.add(item);
        updateAccessTime(node, now);
    }

    private void update(Node<CachedItem<TIdentifier, TValue>> node, long now) {
        queue.makeMostRecent(node);
        updateAccessTime(node, now);
    }

    private void updateAccessTime(@NonNull Node<CachedItem<TIdentifier, TValue>> node, long now) {
        node.setAccessTimeNanos(now);
    }

    private void removeAllExpired(long now) {
        var toRemove = queue.stream()
                .filter(node -> isExpired(node, now))
                .toList();
        toRemove.forEach(queue::remove);
    }

    private boolean isExpired(@NonNull Node<CachedItem<TIdentifier, TValue>> node, long now) {
        return expireAfterAccessNanos <= now - node.getAccessTimeNanos();
    }
}
