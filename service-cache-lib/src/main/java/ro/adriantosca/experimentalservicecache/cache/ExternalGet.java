package ro.adriantosca.experimentalservicecache.cache;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface ExternalGet<TValue, TIdentifier> {
    @NonNull
    TValue call(@NonNull TIdentifier id);
}
