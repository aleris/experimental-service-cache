package ro.adriantosca.experimentalservicecache.cache;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface ExternalPut<TValue, TIdentifier> {
    void call(@NonNull TIdentifier id, @NonNull TValue value);
}
