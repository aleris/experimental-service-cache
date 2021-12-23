package ro.adriantosca.experimentalservicecache.cache;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ServiceCache<TIdentifier, TValue> {
    @NonNull
    private Cache<TIdentifier, TValue> cache = new NoCache<>();

    public ServiceCache<TIdentifier, TValue> with(@NonNull Cache<TIdentifier, TValue> cache) {
        this.cache = cache;
        return this;
    }

    @NonNull
    public TValue get(@NonNull TIdentifier id, @NonNull ExternalGet<TValue, TIdentifier> externalGet) {
        return cache.get(id, externalGet);
    }

    public void put(
            @NonNull TIdentifier id,
            @NonNull TValue value,
            @NonNull ExternalPut<TValue, TIdentifier> externalPut
    ) {
        cache.put(id, value, externalPut);
    }
}
