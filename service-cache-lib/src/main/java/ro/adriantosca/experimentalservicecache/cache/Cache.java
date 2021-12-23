package ro.adriantosca.experimentalservicecache.cache;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Cache<TIdentifier, TValue> {
    @NonNull
    TValue get(@NonNull TIdentifier id, @NonNull ExternalGet<TValue, TIdentifier> externalGet);

    void put(@NonNull TIdentifier id, @NonNull TValue value, @NonNull ExternalPut<TValue, TIdentifier> externalPut);
}
