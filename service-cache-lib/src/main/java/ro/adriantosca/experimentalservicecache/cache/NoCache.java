package ro.adriantosca.experimentalservicecache.cache;

import org.checkerframework.checker.nullness.qual.NonNull;

public class NoCache<TIdentifier, TValue> implements Cache<TIdentifier, TValue> {
    @Override
    @NonNull
    public TValue get(@NonNull TIdentifier id, @NonNull ExternalGet<TValue, TIdentifier> externalGet) {
        return externalGet.call(id);
    }

    @Override
    public void put(
            @NonNull TIdentifier id,
            @NonNull TValue value,
            @NonNull ExternalPut<TValue, TIdentifier> externalPut
    ) {
        externalPut.call(id, value);
    }
}
